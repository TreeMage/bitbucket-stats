package org.treemage
package shared.model.domain.pullrequest

import io.getquill.MappedEncoding

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
