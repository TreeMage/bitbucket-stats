package org.treemage
package api.activity

import zio.*
import zio.http.*
import service.ActivityService
import org.treemage.shared.api.activity.ActivityEndpoints
import org.treemage.shared.model.api.ActivityResponse

trait ActivityApi:
  def getByPullRequestId(
      pullRequestId: Int
  ): ZIO[ActivityService, Nothing, List[ActivityResponse]]

case class ActivityApiHandler(api: ActivityApi):
  val routes: Routes[ActivityService, Nothing] =
    Routes.fromIterable(
      List(
        getAllForPullRequest
      )
    )

  def getAllForPullRequest: Route[ActivityService, Nothing] =
    ActivityEndpoints.getAllForPullRequest.implement(
      Handler.fromFunctionZIO(api.getByPullRequestId)
    )
