package org.treemage
package service

import zio.*
import model.domain.pullrequest.PullRequest

trait PullRequestService:
  def getById(id: Int): ZIO[Any, Nothing, Option[PullRequest]]
  def createOrUpdate(pullRequest: PullRequest): ZIO[Any, Nothing, Int]
