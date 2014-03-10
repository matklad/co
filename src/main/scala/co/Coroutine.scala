package co

import scala.util.continuations.suspendable

sealed trait Coroutine {
  def offer(): Unit@suspendable

  def spawn[T](block: Coroutine => T@suspendable): Task[T]

  def join[T](task: Task[T]): T@suspendable = task.join(this)
}

private[co] trait CoroutineP extends Coroutine