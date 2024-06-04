package org.treemage
package model

enum RequestedCount:
  case Fixed(count: Int)
  case All
