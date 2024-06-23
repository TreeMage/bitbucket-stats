package org.treemage
package service

import model.response.bitbucket.BitBucketApiError
import shared.model.domain.pullrequest.PullRequestState
import org.treemage.shared.model.RequestedCount

import zio.*

enum CrawlingError:
  case MalformedAPIResponse(message: String)
  case PersistenceError(cause: Throwable)
  case APIError(cause: BitBucketApiError)

trait CrawlingService:
  def fetchPullRequestActivityAndSave(
      state: Set[PullRequestState],
      count: RequestedCount,
      providedCrawlId: Option[Int] = None
  ): ZIO[Scope, CrawlingError, Int]

object CrawlingService:
  def fetchPullRequestActivityAndSave(
      state: Set[PullRequestState],
      count: RequestedCount,
      providedCrawlId: Option[Int] = None
  ): ZIO[Scope & CrawlingService, CrawlingError, Int] =
    for
      service <- ZIO.service[CrawlingService]
      total <- service.fetchPullRequestActivityAndSave(
        state,
        count,
        providedCrawlId
      )
    yield total
