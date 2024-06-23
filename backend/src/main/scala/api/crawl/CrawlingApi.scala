package org.treemage
package api.crawl

import shared.api.crawl.{CrawlingApiError, CrawlingEndpoints}
import shared.model.RequestedCount
import shared.model.api.{CrawlIdResponse, CrawlStateResponse}
import shared.model.domain.pullrequest.PullRequestState

import org.treemage.service.{CrawlStateService, CrawlingService}
import zio.*
import zio.http.*

trait CrawlingApi:
  def crawl(
      state: Set[PullRequestState],
      count: RequestedCount
  ): ZIO[CrawlingService & CrawlStateService & Scope, Nothing, Int]
  def getCrawlState(
      crawlId: Int
  ): ZIO[CrawlStateService, Nothing, Option[CrawlStateResponse]]

case class CrawlingApiHandler(api: CrawlingApi):
  val routes: Routes[CrawlingService & CrawlStateService & Scope, Nothing] =
    Routes.fromIterable(
      List(
        crawl,
        getCrawlState
      )
    )
  def crawl: Route[CrawlingService & CrawlStateService & Scope, Nothing] =
    CrawlingEndpoints.crawl.implement(
      Handler.fromFunctionZIO { req =>
        for crawlId <- api.crawl(req.state, req.count)
        yield CrawlIdResponse(crawlId)
      }
    )

  def getCrawlState: Route[CrawlStateService, Nothing] =
    CrawlingEndpoints.state.implement(
      Handler.fromFunctionZIO { id =>
        for
          maybeState <- api.getCrawlState(id)
          state <- ZIO
            .fromOption(maybeState)
            .mapError(_ => CrawlingApiError.NotFound(id))
        yield state
      }
    )
