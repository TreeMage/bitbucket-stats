package org.treemage
package model.db

import shared.model.domain.crawl.{CrawlState, CrawlStatus}

import io.getquill.MappedEncoding
import zio.schema.{DeriveSchema, Schema}

import java.time.LocalDateTime

case class CrawlStateDB(
    id: Int,
    state: CrawlStatus,
    totalActivitiesCrawled: Option[Int],
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime
)

object CrawlStateDB:
  given Schema[CrawlStatus] = DeriveSchema.gen[CrawlStatus]

  def fromDomain(crawlState: CrawlState): CrawlStateDB = CrawlStateDB(
    id = crawlState.id,
    state = crawlState.state,
    totalActivitiesCrawled = crawlState.totalActivitiesCrawled,
    createdAt = crawlState.createdAt,
    updatedAt = crawlState.updatedAt
  )

  extension (status: CrawlStateDB)
    def toDomain: CrawlState = CrawlState(
      id = status.id,
      state = status.state,
      totalActivitiesCrawled = status.totalActivitiesCrawled,
      createdAt = status.createdAt,
      updatedAt = status.updatedAt
    )
