package co

import scala.util.continuations.suspendable

sealed trait Task[T] {
  private[co] def join(co: Coroutine): T@suspendable
}

private[co] trait TaskP[T] extends Task[T]