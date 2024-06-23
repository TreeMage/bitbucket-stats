package org.treemage
package shared.model

enum RequestedCount:
  case Fixed(count: Int)
  case All
