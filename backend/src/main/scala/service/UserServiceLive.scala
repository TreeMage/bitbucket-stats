package org.treemage
package service

import model.db.BitBucketUserDB
import repository.BitBucketUserRepository
import shared.model.domain.BitBucketUser

import zio.*

import java.util.UUID

case class UserServiceLive(userRepository: BitBucketUserRepository)
    extends UserService:
  override def getById(id: UUID): ZIO[Any, Nothing, Option[BitBucketUser]] =
    for user <- userRepository.getById(id).orDie
    yield user.map(_.toDomain)

  override def getAll: ZIO[Any, Nothing, List[BitBucketUser]] =
    userRepository.getAll.orDie.map(_.map(_.toDomain))

  override def createOrUpdate(user: BitBucketUser): ZIO[Any, Nothing, UUID] =
    userRepository.createOrUpdate(BitBucketUserDB.fromDomain(user)).orDie

object UserServiceLive:
  val layer: URLayer[BitBucketUserRepository, UserServiceLive] =
    ZLayer.fromFunction(UserServiceLive.apply)
