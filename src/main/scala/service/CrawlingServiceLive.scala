package org.treemage
package service

import client.BitbucketClient
import model.RequestedCount
import model.domain.crawl.CrawlStatus
import model.domain.pullrequest.{PullRequest, PullRequestActivity}
import model.response.bitbucket.BitBucketApiError
import model.response.bitbucket.pullrequest.PullRequestState

import zio.*

private type Dependencies = UserService & PullRequestService & ActivityService &
  BitbucketClient & CrawlStateService

case class CrawlingServiceLive(
    bitBucketUserService: UserService,
    bitBucketPullRequestService: PullRequestService,
    bitBucketPullRequestActivityService: ActivityService,
    bitbucketClient: BitbucketClient,
    crawlStateService: CrawlStateService
) extends CrawlingService:

  private def shouldRefetchActivities(
      pr: PullRequest
  ): ZIO[Scope, Nothing, Boolean] =
    for
      storedPr <- bitBucketPullRequestService
        .getById(pr.id)
      shoudlRefetch =
        storedPr.fold(true)(_.updatedAt.isBefore(pr.updatedAt))
    yield shoudlRefetch

  private def saveActivity(
      activity: PullRequestActivity
  ): ZIO[Scope, Nothing, Unit] =
    for
      _ <- bitBucketUserService
        .createOrUpdate(activity.associatedUser)
      _ <- bitBucketPullRequestActivityService
        .create(activity)
    yield ()

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
      _ <- bitBucketUserService
        .createOrUpdate(pr.author)
      _ <- bitBucketPullRequestService
        .createOrUpdate(pr)
      activities <- fetchActivities(pr.id)
      _ <- ZIO.foreachDiscard(activities)(saveActivity)
    yield activities.length

  override def fetchPullRequestActivityAndSave(
      state: Set[PullRequestState],
      count: RequestedCount,
      providedCrawlId: Option[Int] = None
  ): ZIO[Scope, CrawlingError, Int] =
    def execute(crawlId: Int) =
      for
        _ <- ZIO.logInfo(s"Fetching PRs with state $state and count $count")
        _ <- crawlStateService.updateState(
          crawlId,
          CrawlStatus.FetchingPullRequests
        )
        prs <- bitbucketClient
          .listPullRequests(state, count)
          .mapError(CrawlingError.APIError.apply)
        _ <- ZIO.logInfo(s"Found ${prs.length} PRs")
        _ <- crawlStateService.updateState(
          crawlId,
          CrawlStatus.FetchingActivities
        )
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
              shouldRefetch <- shouldRefetchActivities(pr)
              numberOfActivities <-
                if shouldRefetch then
                  for
                    _ <- ZIO.logInfo(
                      s"Refetching activities for PR ${pr.id}"
                    )
                    _ <- bitBucketPullRequestActivityService
                      .deleteByPullRequestId(pr.id)
                    _ <- ZIO.logInfo(
                      s"Fetching and saving activities for PR ${pr.id}"
                    )
                    numberOfActivities <- fetchAndSaveActivities(pr)
                    _ <- ZIO.logInfo(
                      s"Saved $numberOfActivities activities for PR ${pr.id}"
                    )
                  yield numberOfActivities
                else
                  ZIO.logInfo(
                    s"Skipping PR ${pr.id} since it has not been updated since the last crawl"
                  ) *> ZIO.succeed(0)
            yield numberOfActivities
          }
          .map(_.sum)
      yield total

    for
      crawlId <- providedCrawlId.fold(crawlStateService.create)(ZIO.succeed(_))
      total <- execute(crawlId).tapError(
        crawlStateService
          .updateState(crawlId, CrawlStatus.Failed)
          *> ZIO.fail(_)
      )
      _ <- crawlStateService.updateState(crawlId, CrawlStatus.Succeeded)
      _ <- crawlStateService.updateCrawledActivityCount(crawlId, total)
    yield total

object CrawlingServiceLive:
  val layer: URLayer[Dependencies, CrawlingService] =
    ZLayer.fromFunction(CrawlingServiceLive.apply)
