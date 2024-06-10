package org.treemage
package model.response

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.util.Try

package object bitbucket:
  def parseBitBucketUUID(uuid: String): Option[UUID] =
    Try(UUID.fromString(uuid.substring(1, uuid.length - 1))).toOption

  def parseBitBucketDateTime(
      dateTime: String
  ): Option[LocalDateTime] =
    val format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX")
    Try(LocalDateTime.from(format.parse(dateTime))).toOption
