package org.treemage
package repository

import model.db.BitBucketPullRequestDB

import zio.*

import java.sql.SQLException

trait BitBucketPullRequestRepository:
  def getById(id: Int): ZIO[Any, SQLException, Option[BitBucketPullRequestDB]]
  def getAll: ZIO[Any, SQLException, List[BitBucketPullRequestDB]]
  def getPaginated(
      page: Int,
      pageSize: Int
  ): ZIO[Any, SQLException, List[BitBucketPullRequestDB]]
  def createOrUpdate(
      pullRequest: BitBucketPullRequestDB
  ): ZIO[Any, SQLException, Int]
