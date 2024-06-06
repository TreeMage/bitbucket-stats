package org.treemage
package api.activity

import model.response.api.ActivityResponse

import zio.http.*
import zio.http.endpoint.*
import zio.http.endpoint.EndpointMiddleware.None

object ActivityEndpoints:

  val getAllForPullRequest
      : Endpoint[Int, Int, Nothing, List[ActivityResponse], None] =
    Endpoint(Method.GET / "activities" / int("pullRequestId"))
      .out[List[ActivityResponse]]
