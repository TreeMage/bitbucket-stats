package org.treemage
package service

import model.domain.pullrequest.PullRequestActivity
import repository.BitBucketPullRequestActivityRepository

import zio.*

case class ActivityServiceLive(
    activityRepository: BitBucketPullRequestActivityRepository,
    userService: UserService
) extends ActivityService:
  override def getById(
      id: Int
  ): ZIO[Any, Nothing, Option[PullRequestActivity]] =
    for
      activityDb <- activityRepository.getById(id).orDie
      parsed <- activityDb match
        case Some(value) =>
          for
            maybeUser <- userService
              .getById(value.authorId)
            user <- ZIO
              .fromOption(maybeUser)
              .orDieWith(_ =>
                new RuntimeException(
                  s"Failed to parse activity ${value.id} from DB to due missing author"
                )
              )
          yield Some(PullRequestActivity.fromDB(value, user))
        case None => ZIO.succeed(None)
    yield parsed

  override def create(
      pullRequestActivity: PullRequestActivity
  ): ZIO[Any, Nothing, Int] =
    activityRepository.create(pullRequestActivity.toDB).orDie

  override def deleteByPullRequestId(
      pullRequestId: Int
  ): ZIO[Any, Nothing, Unit] =
    activityRepository.deleteByPullRequestId(pullRequestId).orDie

object ActivityServiceLive:
  val layer: URLayer[
    BitBucketPullRequestActivityRepository & UserService,
    ActivityServiceLive
  ] =
    ZLayer.fromFunction(ActivityServiceLive.apply)
