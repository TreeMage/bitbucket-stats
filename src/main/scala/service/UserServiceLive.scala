package org.treemage
package service

import model.domain.BitBucketUser
import repository.BitBucketUserRepository

import zio.*

import java.util.UUID

case class UserServiceLive(userRepository: BitBucketUserRepository)
    extends UserService:
  override def getById(id: UUID): ZIO[Any, Nothing, Option[BitBucketUser]] =
    for user <- userRepository.getById(id).orDie
    yield user.map(BitBucketUser.fromDB)

  override def createOrUpdate(user: BitBucketUser): ZIO[Any, Nothing, UUID] =
    userRepository.createOrUpdate(user.toDB).orDie

object UserServiceLive:
  val layer: URLayer[BitBucketUserRepository, UserServiceLive] =
    ZLayer.fromFunction(UserServiceLive.apply)
