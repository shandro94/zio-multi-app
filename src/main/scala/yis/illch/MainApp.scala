package yis.illch

import yis.illch.Exceptions.{AppNotExistException, ArgsMismatchException}
import yis.illch.apps.{CatApp, IllchApp}
import zio._

import scala.util.Try

object MainApp extends App {
  import zio.console._

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    (for {
      app <- parseArgs(args)
      _   <- app.start
    } yield ()).exitCode

  private val name2App: Map[String, List[String] => IllchApp] = Map(
    "cat" -> (args => new CatApp(args))
//    "translate"
//    "parseVkGroups"
//    "server_Http4s+doobie"
//    "tg_send_messages_or_bot_some_work"
  )

  private def parseArgs(args: List[String]) =
    for {
      appName <- ZIO
        .fromOption(args.headOption)
        .tapError(_ => putStrLn(ArgsMismatchException().getMessage))
      app <- chooseApp(appName, Try(args.tail).getOrElse(Nil))
    } yield app

  private def chooseApp(appName: String, argsTail: List[String]) =
    ZIO
      .fromOption(name2App get appName)
      .tapError(_ => putStrLn(AppNotExistException(appName, name2App.keys.toList).getMessage))
      .map(f => f(argsTail))
}
