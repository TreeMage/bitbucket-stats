package org.treemage
package service

import org.treemage.model.domain.BitBucketUser
import zio.*

import java.util.UUID

trait UserService:
  def getById(id: UUID): ZIO[Any, Nothing, Option[BitBucketUser]]
  def createOrUpdate(user: BitBucketUser): ZIO[Any, Nothing, UUID]
