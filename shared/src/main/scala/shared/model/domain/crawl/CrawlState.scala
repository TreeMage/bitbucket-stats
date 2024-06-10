package org.treemage
package shared.model.domain.crawl

import java.time.LocalDateTime

case class CrawlState(
    id: Int,
    state: CrawlStatus,
    totalActivitiesCrawled: Option[Int],
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime
)
