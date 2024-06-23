package org.treemage
package api

import zio.*
import zio.http.*
import zio.http.endpoint.{EndpointExecutor, EndpointLocator}

object ApiExecutor:
  val live: URLayer[URL & Client, EndpointExecutor[Unit]] = ZLayer.fromZIO {
    for
      client <- ZIO.service[Client]
      url <- ZIO.service[URL]
      locator = EndpointLocator.fromURL(url)
    yield EndpointExecutor(client, locator, ZIO.unit)
  }
