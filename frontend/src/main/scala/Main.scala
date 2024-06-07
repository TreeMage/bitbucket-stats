package org.treemage

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native @JSImport("/vite.svg", JSImport.Default)
val viteLogo: String = js.native

@main
def main(): Unit =
  dom.document.getElementById("app").innerHTML = s"<img src='$viteLogo' />"
