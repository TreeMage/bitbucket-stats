package org.treemage
package model.response.pullrequest

import zio.schema.Schema

enum PullRequestState:
  case Open, Merged, Declined, Superseded

object PullRequestState:
  val default: Set[PullRequestState] = Set(Open, Merged)

  given Schema[PullRequestState] = Schema[String].transform(
    v => PullRequestState.valueOf(v.toLowerCase.capitalize),
    _.toString.toUpperCase
  )
  def parse(s: String): Option[PullRequestState] =
    s.toLowerCase match
      case "open"       => Some(Open)
      case "merged"     => Some(Merged)
      case "declined"   => Some(Declined)
      case "superseded" => Some(Superseded)
      case _            => None

  extension (state: PullRequestState)
    // TODO: There must be a way to use the Schema for this
    def asQueryParam: String = state.asString.toUpperCase

    def asString: String = state.toString.toLowerCase
