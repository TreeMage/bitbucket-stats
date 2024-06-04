package org.treemage
package repository

import model.db.BitBucketPullRequestDB

import io.getquill.*
import zio.*

import java.sql.SQLException

trait BitBucketPullRequestRepository:
  def getById(id: Int): ZIO[Any, SQLException, Option[BitBucketPullRequestDB]]
  def create(
      pullRequest: BitBucketPullRequestDB
  ): ZIO[Any, SQLException, Int]

object BitBucketPullRequestRepository:
  def getById(
      id: Int
  ): ZIO[BitBucketPullRequestRepository, SQLException, Option[
    BitBucketPullRequestDB
  ]] =
    for
      repo <- ZIO.service[BitBucketPullRequestRepository]
      result <- repo.getById(id)
    yield result
