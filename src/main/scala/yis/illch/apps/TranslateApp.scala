package yis.illch.apps
import java.io.InputStreamReader
import java.net.{URL, URLEncoder}

import yis.illch.apps.GoogleLangs.{En, Ru}
import zio._

class TranslateApp(args: Seq[String]) extends IllchApp {

  import zio.console._

  private val apiUrl = "https://script.google.com/macros/s/" +
    "AKfycbwPEekPyWbaUaft1arFQGruAaGlZLVfdzoUDEq4WMbyX8VbhuI/" +
    "exec"

  override def start: ZIO[ZEnv, Throwable, ExitCode] =
    (for {
      translate <- translateArgs
      _         <- putStrLn("[ORIGINAL]:")
      _         <- putStrLn(translate.orig)
      _         <- putStrLn("[RU]:")
      _         <- putStrLn(translate.ru)
      _         <- putStrLn("[EN]:")
      _         <- putStrLn(translate.en)
    } yield ()).exitCode

  private def translateArgs =
    for {
      text <- ZIO.succeed(args.mkString(" "))
      ru   <- ZIO.effect(translateImpl(Ru, text))
      en   <- ZIO.effect(translateImpl(En, text))
    } yield Translation(text, ru, en)

  private def translateImpl(to: GoogleLang, text: String) = {
    val requestUrl = apiUrl + "?q=" + URLEncoder.encode(text, "UTF-8") +
      "&target=" + to.code
    //      "&source=" + from.code по-хорошему гугл сам понимает source язык

    val url = new URL(requestUrl)
    import java.io.BufferedReader
    val response   = new StringBuilder
    val connection = url.openConnection()
    connection.setRequestProperty("User-Agent", "Mozilla/5.0")
    val in                = new BufferedReader(new InputStreamReader(connection.getInputStream))
    var inputLine: String = in.readLine()
    while (inputLine != null) {
      response.append(inputLine)
      inputLine = in.readLine()
    }
    in.close()
    response.toString
  }
}

case class Translation(orig: String, ru: String, en: String)

trait GoogleLang { def code: String }
object GoogleLangs {
  object En extends GoogleLang { val code = "en" }
  object Ru extends GoogleLang { val code = "ru" }
}
