package org.treemage
package service

import model.RequestedCount
import model.response.BitBucketApiError
import model.response.pullrequest.PullRequestState

import zio.*

enum CrawlingError:
  case MalformedAPIResponse(message: String)
  case PersistenceError(cause: Throwable)
  case APIError(cause: BitBucketApiError)

trait CrawlingService:
  def fetchPullRequestActivityAndSave(
      state: Set[PullRequestState],
      count: RequestedCount
  ): ZIO[Scope, CrawlingError, Int]

object CrawlingService:
  def fetchPullRequestActivityAndSave(
      state: Set[PullRequestState],
      count: RequestedCount
  ): ZIO[Scope & CrawlingService, CrawlingError, Int] =
    for
      service <- ZIO.service[CrawlingService]
      total <- service.fetchPullRequestActivityAndSave(state, count)
    yield total
