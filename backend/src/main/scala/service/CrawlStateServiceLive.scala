package org.treemage
package service

import repository.CrawlStateRepository
import shared.model.domain.crawl.{CrawlState, CrawlStatus}

import zio.*

case class CrawlStateServiceLive(crawlStateRepository: CrawlStateRepository)
    extends CrawlStateService:

  override val create: ZIO[Any, Nothing, Int] =
    crawlStateRepository.create.orDie
  override def updateState(
      id: Int,
      state: CrawlStatus
  ): ZIO[Any, Nothing, Unit] =
    crawlStateRepository.updateState(id, state).orDie

  override def updateCrawledActivityCount(
      id: Int,
      count: Int
  ): ZIO[Any, Nothing, Unit] =
    crawlStateRepository.updateCrawledActivityCount(id, count).orDie

  override def getById(
      id: Int
  ): ZIO[Any, Nothing, Option[CrawlState]] =
    for state <- crawlStateRepository.getById(id).orDie
    yield state.map(_.toDomain)

object CrawlStateServiceLive:
  val layer: URLayer[CrawlStateRepository, CrawlStateService] =
    ZLayer.fromFunction(CrawlStateServiceLive(_))
