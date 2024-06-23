package org.treemage
package shared.model.api

import shared.model.domain.pullrequest.PullRequestState

import org.treemage.shared.model.RequestedCount
import zio.schema.{DeriveSchema, Schema}

case class CrawlingRequest(state: Set[PullRequestState], count: RequestedCount)

object CrawlingRequest:
  given Schema[CrawlingRequest] = DeriveSchema.gen[CrawlingRequest]
