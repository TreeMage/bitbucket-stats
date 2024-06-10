package org.treemage
package service

import model.db.BitBucketPullRequestDB
import model.db.BitBucketPullRequestDB.*
import repository.BitBucketPullRequestRepository
import shared.model.domain.pullrequest.PullRequest

import zio.*

case class PullRequestServiceLive(
    pullRequestRepository: BitBucketPullRequestRepository,
    userService: UserService
) extends PullRequestService:
  private def parseFromDB(
      pr: BitBucketPullRequestDB
  ): ZIO[Any, Nothing, PullRequest] =
    for
      user <- userService.getById(pr.authorId)
      parsedPr <- ZIO
        .fromOption(user.flatMap(pr.toDomain))
        .orDieWith(_ =>
          new RuntimeException(
            s"Failed to parse PullRequest ${pr.id} from DB"
          )
        )
    yield parsedPr

  override def getById(id: Int): ZIO[Any, Nothing, Option[PullRequest]] =
    for
      pullRequestDB <- pullRequestRepository.getById(id).orDie
      pr <- pullRequestDB match
        case Some(value) => parseFromDB(value).map(Some(_))
        case None        => ZIO.succeed(None)
    yield pr

  override def getAll: ZIO[Any, Nothing, List[PullRequest]] =
    for
      pullRequestsDB <- pullRequestRepository.getAll.orDie
      prs <- ZIO.foreach(pullRequestsDB)(parseFromDB)
    yield prs

  override def getPaginated(
      page: Int,
      pageSize: Int
  ): ZIO[Any, Nothing, List[PullRequest]] =
    for
      pullRequestsDB <- pullRequestRepository.getPaginated(page, pageSize).orDie
      prs <- ZIO.foreach(pullRequestsDB)(parseFromDB)
    yield prs

  override def createOrUpdate(
      pullRequest: PullRequest
  ): ZIO[Any, Nothing, Int] =
    pullRequestRepository
      .createOrUpdate(BitBucketPullRequestDB.fromDomain(pullRequest))
      .orDie

object PullRequestServiceLive:
  val layer: URLayer[
    BitBucketPullRequestRepository & UserService,
    PullRequestServiceLive
  ] =
    ZLayer.fromFunction(PullRequestServiceLive.apply)
