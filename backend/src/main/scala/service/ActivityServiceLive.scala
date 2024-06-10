package org.treemage
package service

import model.db.BitBucketActivityDB
import repository.BitBucketPullRequestActivityRepository
import shared.model.domain.pullrequest.PullRequestActivity

import zio.*

case class ActivityServiceLive(
    activityRepository: BitBucketPullRequestActivityRepository,
    userService: UserService
) extends ActivityService:

  private def parseActivity(
      activity: BitBucketActivityDB
  ): ZIO[Any, Nothing, PullRequestActivity] =
    for
      maybeUser <- userService.getById(activity.authorId)
      user <- ZIO
        .fromOption(maybeUser)
        .orDieWith(_ =>
          new RuntimeException(
            s"Failed to parse activity ${activity.id} from DB to due missing author"
          )
        )
    yield activity.toDomain(user)

  override def getById(
      id: Int
  ): ZIO[Any, Nothing, Option[PullRequestActivity]] =
    for
      activityDb <- activityRepository.getById(id).orDie
      parsed <- activityDb match
        case Some(value) => parseActivity(value).map(Some(_))
        case None        => ZIO.succeed(None)
    yield parsed

  override def getByPullRequestId(
      pullRequestId: RuntimeFlags
  ): ZIO[Any, Nothing, List[PullRequestActivity]] =
    for
      activitiesDb <- activityRepository.getByPullRequestId(pullRequestId).orDie
      parsed <- ZIO.foreachPar(activitiesDb)(parseActivity)
    yield parsed

  override def create(
      pullRequestActivity: PullRequestActivity
  ): ZIO[Any, Nothing, Int] =
    activityRepository
      .create(BitBucketActivityDB.fromDomain(pullRequestActivity))
      .orDie

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
