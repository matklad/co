package co

import scala.collection.immutable.Queue

import scala.util.continuations.suspendable

class CoQueue[T] private(val capacity: Int) {
  assert(capacity > 0)
  var queue: Queue[T] = Queue()

  def deq(co: Coroutine): T@suspendable = {
    while (queue.isEmpty)
      co.offer()
    val p = queue.dequeue
    queue = p._2
    p._1
  }

  def enc(co: Coroutine)(item: T): Unit@suspendable = {
    while (queue.size >= capacity)
      co.offer()
    queue = queue.enqueue(item)
  }
}

object CoQueue {
  def apply[T](capacity: Int = 1): CoQueue[T] =
    new CoQueue(capacity)
}