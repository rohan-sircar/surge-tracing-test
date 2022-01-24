package com.example.model

import surge.scaladsl.command.AggregateCommandModel

import scala.util.{Failure, Success, Try}
import com.example.Book
import com.example.command._
import com.example.event._
import com.example.exception._
import org.slf4j.{Logger, LoggerFactory}
import java.util.UUID

object LibraryCommandModel
    extends AggregateCommandModel[Book, LibraryCommand, LibraryEvent] {
  val log: Logger = LoggerFactory.getLogger(getClass)

  override def processCommand(
      aggregate: Option[Book],
      command: LibraryCommand
  ): Try[List[LibraryEvent]] = {
    log.info("Processing Command ...")
    command match {
      case CreateBook(id, authorName, title) =>
        if (aggregate.isDefined) {
          // Aggregate already exists - no need to recreate
          Success(List.empty)
        } else {
          Success(
            List(
              BookCreated(
                id,
                authorName,
                title
              )
            )
          )
        }
      case DeleteBook(id) =>
        aggregate match {
          case Some(value) => Success(List(BookDeleted(id)))
          case None        => Failure(new Exception("Book does not exist"))
        }

    }
  }

  override def handleEvent(
      aggregate: Option[Book],
      event: LibraryEvent
  ): Option[Book] = {
    event match {
      case BookCreated(id, authorName, title) =>
        Some(Book(id, authorName, title))
      case BookDeleted(id) => None
      // case updated: BookUpdated =>
      //   aggregate.map(_.copy(balance = updated.newBalance))
    }
  }
}
