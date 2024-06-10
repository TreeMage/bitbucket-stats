package org.treemage
package service

import shared.model.domain.BitBucketUser

import zio.*

import java.util.UUID

trait UserService:
  def getById(id: UUID): ZIO[Any, Nothing, Option[BitBucketUser]]
  def getAll: ZIO[Any, Nothing, List[BitBucketUser]]
  def createOrUpdate(user: BitBucketUser): ZIO[Any, Nothing, UUID]
