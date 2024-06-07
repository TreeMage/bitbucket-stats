package org.treemage
package repository

import model.db.{BitBucketActivityDB, BitBucketActivityDBForInsert}

import zio.*

import java.sql.SQLException

trait BitBucketPullRequestActivityRepository:
  def getById(id: Int): ZIO[Any, SQLException, Option[BitBucketActivityDB]]
  def getByPullRequestId(
      pullRequestId: Int
  ): ZIO[Any, SQLException, List[BitBucketActivityDB]]
  def create(
      pullRequestActivity: BitBucketActivityDBForInsert
  ): ZIO[Any, SQLException, Int]
  def deleteByPullRequestId(
      pullRequestId: Int
  ): ZIO[Any, SQLException, Unit]
