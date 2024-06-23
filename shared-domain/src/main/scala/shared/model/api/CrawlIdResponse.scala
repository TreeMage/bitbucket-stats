package org.treemage
package shared.model.api

import zio.schema.*

case class CrawlIdResponse(crawlId: Int)

object CrawlIdResponse:
  given Schema[CrawlIdResponse] = DeriveSchema.gen[CrawlIdResponse]
