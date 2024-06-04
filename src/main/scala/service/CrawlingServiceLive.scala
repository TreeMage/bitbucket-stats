package org.treemage
package service

import client.BitbucketClient
import model.RequestedCount
import model.domain.BitBucketUser
import model.domain.pullrequest.{PullRequest, PullRequestActivity}
import model.response.BitBucketApiError
import model.response.pullrequest.PullRequestState
import repository.{
  BitBucketPullRequestActivityRepository,
  BitBucketPullRequestRepository,
  BitBucketUserRepository
}

import zio.*

private type Dependencies = BitBucketUserRepository &
  BitBucketPullRequestRepository & BitBucketPullRequestActivityRepository &
  BitbucketClient

case class CrawlingServiceLive(
    bitBucketUserRepository: BitBucketUserRepository,
    bitBucketPullRequestRepository: BitBucketPullRequestRepository,
    bitBucketPullRequestActivityRepository: BitBucketPullRequestActivityRepository,
    bitbucketClient: BitbucketClient
) extends CrawlingService:

  private def saveActivity(
      activity: PullRequestActivity
  ): ZIO[Scope, CrawlingError, Unit] =
    (for
      _ <- bitBucketUserRepository
        .createOrUpdate(activity.associatedUser.toDB)
      _ <- bitBucketPullRequestActivityRepository
        .create(activity.toDB)
    yield ()).mapError(CrawlingError.PersistenceError.apply)

  private def fetchActivities(
      prId: Int
  ): ZIO[Scope, CrawlingError, List[PullRequestActivity]] =
    for
      activities <- bitbucketClient
        .listPullRequestActivity(prId)
        .mapError(CrawlingError.APIError.apply)
        .map(
          _.map(PullRequestActivity.fromAPIResponse)
        )
      parsed <- ZIO
        .attempt(activities.map(_.get))
        .orElseFail(
          CrawlingError.MalformedAPIResponse("Failed to parse activities")
        )
    yield parsed

  private def fetchAndSaveActivities(
      pr: PullRequest
  ): ZIO[Scope, CrawlingError, Int] =
    for
      _ <- bitBucketUserRepository
        .createOrUpdate(pr.author.toDB)
        .mapError(CrawlingError.PersistenceError.apply)
      _ <- bitBucketPullRequestRepository
        .create(pr.toDB)
        .mapError(CrawlingError.PersistenceError.apply)
      activities <- fetchActivities(pr.id)
      _ <- ZIO.foreachDiscard(activities)(saveActivity)
    yield activities.length

  override def fetchPullRequestActivityAndSave(
      state: PullRequestState,
      count: RequestedCount
  ): ZIO[Scope, CrawlingError, Int] =
    for
      prs <- bitbucketClient
        .listPullRequests(state, count)
        .mapError(CrawlingError.APIError.apply)
      total <- ZIO
        .foreachPar(prs) { prResponse =>
          for
            pr <- ZIO
              .fromOption(PullRequest.fromAPIResponse(prResponse))
              .orElse(
                ZIO.fail(
                  CrawlingError.MalformedAPIResponse(
                    s"Failed to parse PR response $prResponse"
                  )
                )
              )
            _ <- bitBucketPullRequestActivityRepository
              .deleteByPullRequestId(pr.id)
              .mapError(CrawlingError.PersistenceError.apply)
            numberOfActivities <- fetchAndSaveActivities(pr)
          yield numberOfActivities
        }
        .map(_.sum)
    yield total

object CrawlingServiceLive:
  val layer: URLayer[Dependencies, CrawlingService] =
    ZLayer.fromFunction(CrawlingServiceLive.apply)
