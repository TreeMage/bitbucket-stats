package org.treemage
package api.pullrequest

import org.treemage.service.PullRequestService
import org.treemage.shared.api.pullrequest.{
  PullRequestApiError,
  PullRequestEndpoints
}
import org.treemage.shared.model.api.PullRequestResponse
import zio.*
import zio.http.*

trait PullRequestApi:
  def getById(
      id: Int
  ): ZIO[PullRequestService, PullRequestApiError, PullRequestResponse]
  def getAll: ZIO[PullRequestService, Nothing, List[PullRequestResponse]]
  def getPaginated(
      page: Int,
      pageSize: Int
  ): ZIO[PullRequestService, Nothing, List[PullRequestResponse]]

case class PullRequestApiHandler(api: PullRequestApi):
  val routes: Routes[PullRequestService, Nothing] =
    Routes.fromIterable(
      List(
        getById,
        getAll,
        getPaginated
      )
    )

  def getById: Route[PullRequestService, Nothing] =
    PullRequestEndpoints.getById.implement(
      Handler.fromFunctionZIO(api.getById)
    )

  def getAll: Route[PullRequestService, Nothing] =
    PullRequestEndpoints.getAll.implement(
      Handler.fromZIO(api.getAll)
    )

  def getPaginated: Route[PullRequestService, Nothing] =
    PullRequestEndpoints.getPaginated.implement(
      Handler.fromFunctionZIO(
        api.getPaginated
      )
    )
