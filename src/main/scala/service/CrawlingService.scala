package org.treemage
package service

import model.RequestedCount
import org.treemage.model.response.bitbucket.BitBucketApiError
import org.treemage.model.response.bitbucket.pullrequest.PullRequestState

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
