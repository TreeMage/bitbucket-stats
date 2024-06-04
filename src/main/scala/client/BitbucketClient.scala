package org.treemage
package client

import model.RequestedCount
import model.response.BitBucketApiError
import model.response.pullrequest.{
  PullRequestActivityResponseValueWrapper,
  PullRequestResponse,
  PullRequestState
}

import zio.*

trait BitbucketClient:
  def listPullRequests(
      state: Set[PullRequestState],
      count: RequestedCount
  ): ZIO[Scope, BitBucketApiError, List[PullRequestResponse]]

  def listPullRequestActivity(
      pullRequestId: Int
  ): ZIO[Scope, BitBucketApiError, List[
    PullRequestActivityResponseValueWrapper
  ]]

object BitbucketClient:
  def listPullRequests(
      state: Set[PullRequestState],
      count: RequestedCount
  ): ZIO[
    BitbucketClient & Scope,
    BitBucketApiError,
    List[PullRequestResponse]
  ] = for
    client <- ZIO.service[BitbucketClient]
    prs <- client.listPullRequests(state, count)
  yield prs

  def listPullRequestActivity(pullRequestId: Int): ZIO[
    BitbucketClient & Scope,
    BitBucketApiError,
    List[PullRequestActivityResponseValueWrapper]
  ] = for
    client <- ZIO.service[BitbucketClient]
    activity <- client.listPullRequestActivity(pullRequestId)
  yield activity
