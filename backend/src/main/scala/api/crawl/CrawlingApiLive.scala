package org.treemage
package api.crawl

import model.RequestedCount
import model.domain.crawl.CrawlState
import model.response.api.CrawlStateResponse
import model.response.bitbucket.pullrequest.PullRequestState
import service.{CrawlStateService, CrawlingService}

import zio.*

case object CrawlingApiLive extends CrawlingApi:
  override def crawl(
      state: Set[PullRequestState],
      count: RequestedCount
  ): ZIO[CrawlingService & CrawlStateService & Scope, Nothing, Int] =
    for
      crawlService <- ZIO.service[CrawlingService]
      crawlStateService <- ZIO.service[CrawlStateService]
      crawlId <- crawlStateService.create
      _ <- crawlService
        .fetchPullRequestActivityAndSave(state, count, Some(crawlId))
        .orDieWith(e => new RuntimeException(s"Error while crawling: $e"))
        .forkDaemon
    yield crawlId

  override def getCrawlState(
      crawlId: Int
  ): ZIO[CrawlStateService, Nothing, Option[CrawlStateResponse]] =
    for
      crawlStateService <- ZIO.service[CrawlStateService]
      state <- crawlStateService.getById(crawlId)
    yield state.map(CrawlStateResponse.fromDomain)
