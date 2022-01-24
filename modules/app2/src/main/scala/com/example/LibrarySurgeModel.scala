package com.example

import julienrf.json.derived

import play.api.libs.json.Json
import surge.core.{
  SerializedAggregate,
  SerializedMessage,
  SurgeAggregateReadFormatting,
  SurgeAggregateWriteFormatting,
  SurgeEventWriteFormatting
}
import surge.kafka.KafkaTopic
import surge.scaladsl.command.{AggregateCommandModel, SurgeCommandBusinessLogic}
import com.example.LibraryVerification

import java.util.UUID

import org.graalvm.polyglot.Context
import play.api.libs.json.Format

sealed trait LibraryVerificationCommand
final case class VerifyBook(id: UUID, book: Book)

sealed trait LibraryVerificationEvent {
  def id: UUID
}

object LibraryVerificationEvent {
  case object LibraryVerificationEvent1 extends LibraryVerificationEvent {
    def id = UUID.randomUUID()
  }
  implicit def format: Format[LibraryVerificationEvent] =
    derived.oformat[LibraryVerificationEvent]()
}

final class LibraryVerificationSurgeModel
    extends SurgeCommandBusinessLogic[
      UUID,
      LibraryVerification,
      LibraryVerificationCommand,
      LibraryVerificationEvent
    ] {

  def commandModel: AggregateCommandModel[
    LibraryVerification,
    LibraryVerificationCommand,
    LibraryVerificationEvent
  ] = LibraryVerificationCommandModel

  def aggregateName: String = "library-verification"

  def stateTopic: KafkaTopic = KafkaTopic("library-verification-state")

  def eventsTopic: KafkaTopic = KafkaTopic("library-verification-events")

  def aggregateReadFormatting
      : SurgeAggregateReadFormatting[LibraryVerification] =
    (bytes: Array[Byte]) => Json.parse(bytes).asOpt[LibraryVerification]

  def aggregateWriteFormatting
      : SurgeAggregateWriteFormatting[LibraryVerification] =
    (agg: LibraryVerification) => {
      val aggBytes = Json.toJson(agg).toString().getBytes()
      val messageHeaders = Map("aggregate_id" -> agg.id.toString)
      SerializedAggregate(aggBytes, messageHeaders)
    }

  def eventWriteFormatting
      : SurgeEventWriteFormatting[LibraryVerificationEvent] =
    evt => {
      val evtKey = evt.id.toString
      val evtBytes = Json.toBytes(Json.toJson(evt))
      val messageHeaders = Map("aggregate_id" -> evt.id.toString)
      SerializedMessage(evtKey, evtBytes, messageHeaders)
    }
}
