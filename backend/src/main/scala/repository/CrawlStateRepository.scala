package org.treemage
package repository

import model.db.CrawlStateDB
import shared.model.domain.crawl.CrawlStatus

import zio.ZIO

import java.sql.SQLException

trait CrawlStateRepository:
  val create: ZIO[Any, SQLException, Int]
  def updateState(id: Int, state: CrawlStatus): ZIO[Any, SQLException, Unit]
  def updateCrawledActivityCount(
      id: Int,
      count: Int
  ): ZIO[Any, SQLException, Unit]
  def getById(id: Int): ZIO[Any, SQLException, Option[CrawlStateDB]]
