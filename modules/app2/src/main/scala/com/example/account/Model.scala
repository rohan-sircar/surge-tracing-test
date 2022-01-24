package com.example

import play.api.libs.json.{Format, Json}

import java.util.UUID

final case class LibraryVerification(id: UUID)
object LibraryVerification {
  implicit val format: Format[LibraryVerification] = Json.format
}
