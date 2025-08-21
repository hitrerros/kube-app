package org.service.auth

case class AuthUser(login: String, id: Long, avatar_url: String, name: Option[String], email: Option[String])
