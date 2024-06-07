package org.treemage
package model.db

import io.getquill.MappedEncoding

import java.time.LocalDateTime
import java.util.UUID

enum ActivityType:
  case Approval
  case Comment
  case Update

object ActivityType:
  given MappedEncoding[String, ActivityType] = MappedEncoding(parse(_).get)
  given MappedEncoding[ActivityType, String] = MappedEncoding(_.asString)

  def parse(activityType: String): Option[ActivityType] =
    activityType.toLowerCase match
      case "approval" => Some(Approval)
      case "comment"  => Some(Comment)
      case "update"   => Some(Update)
      case _          => None

  extension (self: ActivityType)
    def asString: String =
      self match
        case Approval => "approval"
        case Comment  => "comment"
        case Update   => "update"

trait BitBucketActivityDBContent(
    pullRequestId: Int,
    createdAt: LocalDateTime,
    activityType: ActivityType,
    authorId: UUID
)

case class BitBucketActivityDBForInsert(
    pullRequestId: Int,
    createdAt: LocalDateTime,
    activityType: ActivityType,
    authorId: UUID
) extends BitBucketActivityDBContent(
      pullRequestId,
      createdAt,
      activityType,
      authorId
    )

case class BitBucketActivityDB(
    id: Int,
    pullRequestId: Int,
    createdAt: LocalDateTime,
    activityType: ActivityType,
    authorId: UUID
) extends BitBucketActivityDBContent(
      pullRequestId,
      createdAt,
      activityType,
      authorId
    )
