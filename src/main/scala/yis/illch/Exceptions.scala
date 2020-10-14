package yis.illch

object Exceptions {
  case class ArgsMismatchException() extends Exception("You must specify an app name as first argument.")
  case class AppNotExistException(app: String, apps: List[String])
      extends Exception(s"App $app is incorrect. Current apps: ${apps.mkString("[", ", ", "]")}")
}