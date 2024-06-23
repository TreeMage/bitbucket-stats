package org.treemage
package shared.api.pullrequest

import shared.model.api.PullRequestResponse

import zio.*
import zio.http.*
import zio.http.codec.QueryCodec
import zio.http.endpoint.*
import zio.http.endpoint.EndpointMiddleware.None

object PullRequestEndpoints:
  val getById
      : Endpoint[Int, Int, PullRequestApiError, PullRequestResponse, None] =
    Endpoint(Method.GET / "pullrequests" / int("id"))
      .out[PullRequestResponse]
      .outError[PullRequestApiError](Status.NotFound)

  val getAll: Endpoint[Unit, Unit, ZNothing, List[PullRequestResponse], None] =
    Endpoint(Method.GET / "pullrequests" / "all")
      .out[List[PullRequestResponse]]

  val getPaginated
      : Endpoint[Unit, (Int, Int), ZNothing, List[PullRequestResponse], None] =
    Endpoint(Method.GET / "pullrequests")
      .query(
        QueryCodec.queryInt("page") ++ QueryCodec.queryInt("pageSize")
      )
      .out[List[PullRequestResponse]]
