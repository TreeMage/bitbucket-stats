package org.treemage
package api.pullrequest

import org.treemage.model.response.api.PullRequestResponse
import org.treemage.service.PullRequestService
import zio.*

case object PullRequestApiLive extends PullRequestApi:
  def getById(
      id: Int
  ): ZIO[PullRequestService, PullRequestApiError, PullRequestResponse] =
    for
      service <- ZIO.service[PullRequestService]
      maybePr <- service.getById(id)
      pr <- ZIO.fromOption(maybePr).orElseFail(PullRequestApiError.NotFound(id))
    yield PullRequestResponse.fromDomain(pr)

  def getAll: ZIO[PullRequestService, Nothing, List[PullRequestResponse]] =
    for
      service <- ZIO.service[PullRequestService]
      prs <- service.getAll
    yield prs.map(PullRequestResponse.fromDomain)
  def getPaginated(
      page: Int,
      pageSize: Int
  ): ZIO[PullRequestService, Nothing, List[PullRequestResponse]] =
    for
      service <- ZIO.service[PullRequestService]
      prs <- service.getPaginated(page, pageSize)
    yield prs.map(PullRequestResponse.fromDomain)
