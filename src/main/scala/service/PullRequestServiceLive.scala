package org.treemage
package service

import zio.*
import model.domain.pullrequest.PullRequest
import repository.BitBucketPullRequestRepository

case class PullRequestServiceLive(
    pullRequestRepository: BitBucketPullRequestRepository,
    userService: UserService
) extends PullRequestService:
  override def getById(id: Int): ZIO[Any, Nothing, Option[PullRequest]] =
    for
      pullRequestDB <- pullRequestRepository.getById(id).orDie
      pr <- pullRequestDB match
        case Some(pr) =>
          for
            user <- userService.getById(pr.authorId)
            parsedPr <- ZIO
              .fromOption(user.flatMap(PullRequest.fromDB(pr, _)))
              .orDieWith(_ =>
                new RuntimeException(
                  s"Failed to parse PullRequest ${pr.id} from DB"
                )
              )
          yield Some(parsedPr)
        case None => ZIO.succeed(None)
    yield pr

  override def createOrUpdate(
      pullRequest: PullRequest
  ): ZIO[Any, Nothing, Int] =
    pullRequestRepository.createOrUpdate(pullRequest.toDB).orDie

object PullRequestServiceLive:
  val layer: URLayer[
    BitBucketPullRequestRepository & UserService,
    PullRequestServiceLive
  ] =
    ZLayer.fromFunction(PullRequestServiceLive.apply)
