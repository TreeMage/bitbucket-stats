package org.treemage
package repository

import org.treemage.model.db.BitBucketUserDB
import zio.ZIO

import java.sql.SQLException
import java.util.UUID

trait BitBucketUserRepository:
  def getById(id: UUID): ZIO[Any, SQLException, Option[BitBucketUserDB]]
  def createOrUpdate(
      user: BitBucketUserDB
  ): ZIO[Any, SQLException, UUID]

object BitBucketUserRepository:
  def getById(
      id: UUID
  ): ZIO[BitBucketUserRepository, SQLException, Option[BitBucketUserDB]] =
    for
      repo <- ZIO.service[BitBucketUserRepository]
      result <- repo.getById(id)
    yield result

  def createOrUpdate(
      user: BitBucketUserDB
  ): ZIO[BitBucketUserRepository, SQLException, UUID] =
    for
      repo <- ZIO.service[BitBucketUserRepository]
      result <- repo.createOrUpdate(user)
    yield result
