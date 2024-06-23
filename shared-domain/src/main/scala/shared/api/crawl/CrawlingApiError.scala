package org.treemage
package shared.api.crawl

import zio.schema.*

enum CrawlingApiError:
  case NotFound(id: Int)

object CrawlingApiError:
  given Schema[CrawlingApiError] = DeriveSchema.gen
