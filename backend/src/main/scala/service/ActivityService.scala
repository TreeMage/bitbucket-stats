package org.treemage
package service

import shared.model.domain.pullrequest.PullRequestActivity

import zio.*

trait ActivityService:
  def getById(id: Int): ZIO[Any, Nothing, Option[PullRequestActivity]]
  def getByPullRequestId(
      pullRequestId: Int
  ): ZIO[Any, Nothing, List[PullRequestActivity]]
  def create(
      pullRequestActivity: PullRequestActivity
  ): ZIO[Any, Nothing, Int]
  def deleteByPullRequestId(
      pullRequestId: Int
  ): ZIO[Any, Nothing, Unit]
