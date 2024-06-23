package org.treemage
package api.pullrequest

import org.treemage.service.PullRequestService
import org.treemage.shared.api.pullrequest.PullRequestApiError
import org.treemage.shared.model.api.PullRequestResponse
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
