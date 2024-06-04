package org.treemage
package model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.util.Try

package object domain:
  def parseBitBucketUUID(uuid: String): Option[UUID] =
    Try(UUID.fromString(uuid.substring(1, uuid.length - 1))).toOption

  def parseBitBucketDateTime(
      dateTime: String
  ): Option[java.time.LocalDateTime] =
    val format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX")
    Try(LocalDateTime.from(format.parse(dateTime))).toOption
