package yis.illch.apps

import zio.{Task, ZIO}

import scala.io.Source
import scala.util.Using

class CatApp(args: List[String]) extends IllchApp {

  import zio.console._

  def start =
    (for {
      contents <- fileContents(args)
      _        <- ZIO.foreach(contents)(cnt => yellow(cnt._1) *> putStrLn(cnt._2) *> putStrLn(""))
    } yield ()).exitCode

  private def fileContents(args: List[String]) =
    ZIO.foreach(args) { path =>
      for {
        content <- fileContent(path)
      } yield path -> content
    }

  private def fileContent(path: String): Task[String] =
    Task.fromTry {
      Using(Source.fromFile(path))(_.mkString)
    }

  private def yellow(s: String) = putStrLn(scala.Console.YELLOW + s + scala.Console.RESET)
}