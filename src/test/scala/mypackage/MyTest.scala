package mypackage
import akka.actor.typed._
import org.scalatest._
import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit

class MyTest extends ScalaTestWithActorTestKit with FreeSpecLike with Matchers {
  "Unexpected NoSuchElementException" in {

    case class Ping(message: String, response: ActorRef[Pong])
    case class Pong(message: String)
    import akka.actor.typed.scaladsl.Behaviors
    val echoActor: Behavior[Ping] = Behaviors.receive { (_, message) => 
      message match {
        case Ping(m, replyTo) =>
          replyTo ! Pong(m)
          Behaviors.same
      }
    }
    val pinger = testKit.spawn(echoActor, "ping")
    val probe = testKit.createTestProbe[Pong]()
    pinger ! Ping("hello", probe.ref)
    probe.expectMessage(Pong("hello"))
  }
}