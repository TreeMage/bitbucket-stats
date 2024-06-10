package org.treemage
package model.response.api

import model.RequestedCount
import shared.model.domain.pullrequest.PullRequestState

import zio.schema.{DeriveSchema, Schema}

case class CrawlingRequest(state: Set[PullRequestState], count: RequestedCount)

object CrawlingRequest:
  given Schema[CrawlingRequest] = DeriveSchema.gen[CrawlingRequest]
