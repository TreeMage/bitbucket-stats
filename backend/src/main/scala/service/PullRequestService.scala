package org.treemage
package service

import shared.model.domain.pullrequest.PullRequest

import zio.*

trait PullRequestService:
  def getById(id: Int): ZIO[Any, Nothing, Option[PullRequest]]
  def getAll: ZIO[Any, Nothing, List[PullRequest]]
  def getPaginated(
      page: Int,
      pageSize: Int
  ): ZIO[Any, Nothing, List[PullRequest]]
  def createOrUpdate(pullRequest: PullRequest): ZIO[Any, Nothing, Int]
