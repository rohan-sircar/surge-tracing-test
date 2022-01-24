package com.example.model

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
import com.example.Book
import com.example.command._
import com.example.event._

import java.util.UUID

import org.graalvm.polyglot.Context

final class LibrarySurgeModel
    extends SurgeCommandBusinessLogic[
      UUID,
      Book,
      LibraryCommand,
      LibraryEvent
    ] {

  // val cm = new JsLibraryCommandModel(ctx)
  def commandModel: AggregateCommandModel[
    Book,
    LibraryCommand,
    LibraryEvent
  ] = LibraryCommandModel

  def aggregateName: String = "library"

  def stateTopic: KafkaTopic = KafkaTopic("library-state")

  def eventsTopic: KafkaTopic = KafkaTopic("library-events")

  def aggregateReadFormatting: SurgeAggregateReadFormatting[Book] =
    (bytes: Array[Byte]) => Json.parse(bytes).asOpt[Book]

  def aggregateWriteFormatting: SurgeAggregateWriteFormatting[Book] =
    (agg: Book) => {
      val aggBytes = Json.toJson(agg).toString().getBytes()
      val messageHeaders = Map("aggregate_id" -> agg.id.toString)
      SerializedAggregate(aggBytes, messageHeaders)
    }

  def eventWriteFormatting: SurgeEventWriteFormatting[LibraryEvent] =
    (evt: LibraryEvent) => {
      val evtKey = evt.id.toString
      val evtBytes = evt.toJson.toString().getBytes()
      val messageHeaders = Map("aggregate_id" -> evt.id.toString)
      SerializedMessage(evtKey, evtBytes, messageHeaders)
    }
}
