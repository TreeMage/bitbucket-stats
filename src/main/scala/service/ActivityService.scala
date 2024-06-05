package org.treemage
package service

import model.domain.pullrequest.PullRequestActivity

import zio.*

trait ActivityService:
  def getById(id: Int): ZIO[Any, Nothing, Option[PullRequestActivity]]
  def create(
      pullRequestActivity: PullRequestActivity
  ): ZIO[Any, Nothing, Int]
  def deleteByPullRequestId(
      pullRequestId: Int
  ): ZIO[Any, Nothing, Unit]
