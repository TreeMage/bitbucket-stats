package org.treemage
package model.response.api

import zio.schema.*

case class CrawlIdResponse(crawlId: Int)

object CrawlIdResponse:
  given Schema[CrawlIdResponse] = DeriveSchema.gen[CrawlIdResponse]
