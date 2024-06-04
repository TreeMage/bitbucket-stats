package org.treemage
package model.response.pullrequest

import zio.http.codec.QueryCodec
import zio.schema.Schema
import zio.schema.codec.BinaryCodec

enum PullRequestState:
  case Open, Merged, Declined, Superseded

object PullRequestState:
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
