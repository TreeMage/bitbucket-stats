package org.treemage
package shared.model.api

import shared.model.domain.crawl.{CrawlState, CrawlStatus}

import zio.schema.*

import java.time.LocalDateTime

case class CrawlStateResponse(
    id: Int,
    state: CrawlStatus,
    totalActivitiesCrawled: Option[Int],
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime
)

object CrawlStateResponse:
  given Schema[CrawlStateResponse] = DeriveSchema.gen

  def fromDomain(crawlState: CrawlState): CrawlStateResponse =
    CrawlStateResponse(
      crawlState.id,
      crawlState.state,
      crawlState.totalActivitiesCrawled,
      crawlState.createdAt,
      crawlState.updatedAt
    )
