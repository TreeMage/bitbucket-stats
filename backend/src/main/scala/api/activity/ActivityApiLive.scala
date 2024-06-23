package org.treemage
package api.activity

import service.ActivityService
import org.treemage.shared.model.api.ActivityResponse

import zio.*

case object ActivityApiLive extends ActivityApi:
  def getByPullRequestId(
      pullRequestId: Int
  ): ZIO[ActivityService, Nothing, List[ActivityResponse]] =
    for
      service <- ZIO.service[ActivityService]
      activities <- service.getByPullRequestId(pullRequestId)
    yield activities.map(ActivityResponse.fromDomain)
