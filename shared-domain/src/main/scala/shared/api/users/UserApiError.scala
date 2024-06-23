package org.treemage
package shared.api.users

import zio.schema.*

import java.util.UUID

enum UserApiError:
  case NotFound(id: UUID)

object UserApiError:
  given Schema[UserApiError] = DeriveSchema.gen[UserApiError]
