package org.treemage
package service

import zio.*
import model.domain.pullrequest.PullRequest

trait PullRequestService:
  def getById(id: Int): ZIO[Any, Nothing, Option[PullRequest]]
  def getAll: ZIO[Any, Nothing, List[PullRequest]]
  def getPaginated(
      page: Int,
      pageSize: Int
  ): ZIO[Any, Nothing, List[PullRequest]]
  def createOrUpdate(pullRequest: PullRequest): ZIO[Any, Nothing, Int]
