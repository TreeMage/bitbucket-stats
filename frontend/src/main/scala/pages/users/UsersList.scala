package org.treemage
package pages.users

import shared.model.api.UserResponse

import com.raquo.laminar.api.L.*

case class UserRow(user: UserResponse):
  def render: HtmlElement =
    div(
      display.flex,
      flexDirection.row,
      alignItems.center,
      justifyContent.spaceBetween,
      padding.px(8),
      borderWidth.px(1),
      borderStyle.solid,
      borderColor.gray,
      borderRadius.px(4),
      div(
        user.name
      ),
      div(
        user.accountId
      )
    )

case class UserList(users: Var[List[UserResponse]]):
  def render: HtmlElement =
    div(
      display.flex,
      flexDirection.column,
      rowGap.px(4),
      children <-- users.signal.split(_.id) { (_, user, _) =>
        UserRow(user).render
      }
    )
