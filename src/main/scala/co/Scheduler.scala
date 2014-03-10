package co

import scala.util.continuations._
import scala.util.Random

class Scheduler private {
  scheduler =>
  def addCoroutine(block: Coroutine => Unit@suspendable): Unit = {
    addCoroutine(new CoroutineImpl(block))
  }

  def run() = {
    while (!coroutines.isEmpty) {
      runCont()
    }
  }

  private var coroutines: Set[CoroutineImpl] = Set()

  private def addCoroutine(coroutine: CoroutineImpl): Unit = {
    coroutines += coroutine
  }

  private def runCont() = {
    val next = selectNext()
    runCoroutine(next)
  }

  private def runCoroutine(c: CoroutineImpl) = {
    assert(coroutines.contains(c), "Running unknown coroutine")
    coroutines -= c
    c.runBody()
  }

  private def selectNext(): CoroutineImpl = {
    val keys = coroutines.toVector
    val ind = Random.nextInt(keys.size)
    keys(ind)
  }

  class CoroutineImpl private[Scheduler](block: Coroutine => Unit@suspendable)
    extends CoroutineP {
    def offer(): Unit@suspendable = {
      shift {
        cont: (Unit => Unit) =>
          body = cont
          scheduler.addCoroutine(this)
      }
    }

    def spawn[T](block: Coroutine => T@suspendable): Task[T] = {
      val q: CoQueue[T] = CoQueue(1)
      val co: CoroutineImpl = new CoroutineImpl((co: Coroutine) => {
        q.enc(co)(block(co))
      })
      scheduler.addCoroutine(co)
      new TaskP[T] {
        override def join(co: Coroutine): T@suspendable = {
          q.deq(co)
        }
      }
    }

    private var body: Unit => Unit = (_: Unit) => reset {
      block(this)
    }

    private[Scheduler] def runBody(): Unit = body()

  }

}

object Scheduler {
  def apply(): Scheduler = new Scheduler()
}