package org.treemage
package shared.api.pullrequest

import zio.schema.{DeriveSchema, Schema}

enum PullRequestApiError:
  case NotFound(id: Int)

object PullRequestApiError:
  given Schema[PullRequestApiError] = DeriveSchema.gen[PullRequestApiError]
