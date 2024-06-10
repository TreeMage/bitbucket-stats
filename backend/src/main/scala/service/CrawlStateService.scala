package org.treemage
package service

import shared.model.domain.crawl.{CrawlState, CrawlStatus}

import zio.*

trait CrawlStateService:
  val create: ZIO[Any, Nothing, Int]
  def updateState(id: Int, state: CrawlStatus): ZIO[Any, Nothing, Unit]
  def updateCrawledActivityCount(id: Int, count: Int): ZIO[Any, Nothing, Unit]
  def getById(id: Int): ZIO[Any, Nothing, Option[CrawlState]]
