package org.treemage
package shared.api.crawl

import shared.model.api.{CrawlIdResponse, CrawlStateResponse, CrawlingRequest}

import zio.http.codec.PathCodec.*
import zio.http.endpoint.Endpoint
import zio.http.endpoint.EndpointMiddleware.None
import zio.http.{Method, Status}

object CrawlingEndpoints:
  val crawl: Endpoint[Unit, CrawlingRequest, Nothing, CrawlIdResponse, None] =
    Endpoint(Method.POST / "crawl" / "start")
      .in[CrawlingRequest]
      .out[CrawlIdResponse]

  val state: Endpoint[Int, Int, CrawlingApiError, CrawlStateResponse, None] =
    Endpoint(
      Method.GET / "crawl" / int("id")
    )
      .out[CrawlStateResponse]
      .outError[CrawlingApiError](Status.NotFound)
