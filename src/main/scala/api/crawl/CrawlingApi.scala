package org.treemage
package api.crawl

import model.RequestedCount
import model.response.api.{CrawlIdResponse, CrawlStateResponse}
import model.response.bitbucket.pullrequest.PullRequestState
import service.{CrawlStateService, CrawlingService}

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
  def crawl = CrawlingEndpoints.crawl.implement(
    Handler.fromFunctionZIO { req =>
      for crawlId <- api.crawl(req.state, req.count)
      yield CrawlIdResponse(crawlId)
    }
  )

  def getCrawlState = CrawlingEndpoints.state.implement(
    Handler.fromFunctionZIO { id =>
      for
        maybeState <- api.getCrawlState(id)
        state <- ZIO
          .fromOption(maybeState)
          .mapError(_ => CrawlingApiError.NotFound(id))
      yield state
    }
  )
