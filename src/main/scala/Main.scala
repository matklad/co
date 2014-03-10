import co.{Coroutine, Scheduler}
import scala.util.continuations.suspendable

object Main {
  def main(args: Array[String]) {
    pairOfCoroutines()
  }

  def countFib(): Unit = {
    def fib(n: Int)(co: Coroutine): Int@suspendable = {
      if (n == 0 || n == 1)
        1
      else {
        val f1 = co.spawn(fib(n - 1))
        val f2 = co.spawn(fib(n - 2))
        co.join(f1) + co.join(f2)
      }
    }

    val scheduler = Scheduler()
    scheduler.addCoroutine {
      co: Coroutine =>
        val task = co.spawn(fib(10))
        val f10 = co.join(task)
        println(s"fib10 = $f10")
    }
    scheduler.run()
  }

  def pairOfCoroutines(): Unit = {
    val scheduler = Scheduler()
    scheduler.addCoroutine(co =>
      while (true) {
        println("A")
        co.offer()
      }
    )
    scheduler.addCoroutine(co =>
      while (true) {
        println("B")
        co.offer()
      }
    )
    scheduler.run()
  }
}
