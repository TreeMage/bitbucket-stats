package org.treemage
package model.domain.crawl

import io.getquill.MappedEncoding
import zio.schema.*

enum CrawlStatus:
  case Started
  case FetchingPullRequests
  case FetchingActivities
  case Succeeded
  case Failed

object CrawlStatus:
  given MappedEncoding[String, CrawlStatus] = MappedEncoding(
    CrawlStatus.valueOf
  )
  given MappedEncoding[CrawlStatus, String] = MappedEncoding(_.toString)
  given Schema[CrawlStatus] = DeriveSchema.gen
