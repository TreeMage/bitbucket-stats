package org.treemage
package repository

import model.db.BitBucketUserDB

import zio.ZIO

import java.sql.SQLException
import java.util.UUID

trait BitBucketUserRepository:
  def getById(id: UUID): ZIO[Any, SQLException, Option[BitBucketUserDB]]
  def getAll: ZIO[Any, SQLException, List[BitBucketUserDB]]
  def createOrUpdate(
      user: BitBucketUserDB
  ): ZIO[Any, SQLException, UUID]
