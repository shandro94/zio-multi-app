package yis.illch.apps

import zio.{ExitCode, ZEnv, ZIO}

trait IllchApp {
  def start: ZIO[ZEnv, Throwable, ExitCode]
}