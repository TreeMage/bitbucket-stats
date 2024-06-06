package org.treemage
package model.response.bitbucket

import model.response.bitbucket.BitBucketApiError

enum BitBucketApiError:
  case RequestFailed(throwable: Throwable)
  case NotFound
  case Unauthorized

object BitBucketApiError:
  def fromStatusCode(statusCode: Int): BitBucketApiError = statusCode match
    case 401 => Unauthorized
    case 404 => NotFound
    case _ =>
      RequestFailed(
        new Exception(s"Request failed with status code $statusCode")
      )
