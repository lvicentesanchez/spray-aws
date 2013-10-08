import akka.actor.ActorSystem
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.regions.{ Region, Regions }
import com.amazonaws.services.sqs.{ AmazonSQSAsync, AmazonSQSAsyncClient }
import aws._
import scala.concurrent.Future
import scalaz.\/-
import scalaz.std.list._
import scalaz.contrib.std.scalaFuture._
import scalaz.std.list._
import scalaz.syntax.traverse._
import spray.contrib.aws.common._

object SprayAWS2 extends App with QueueModule with QueueModuleImpl with QueueConfiguration with AsyncRequest {
  implicit val system = ActorSystem()
  import system.dispatcher

  val accessKey: String = ""
  val secretKey: String = ""
  val authSQS: AWSCredentials = new AWSCredentials {
    override def getAWSAccessKeyId(): String = accessKey
    override def getAWSSecretKey(): String = secretKey
  }
  val queue: AmazonSQSAsync = {
    val t = new AmazonSQSAsyncClient(authSQS)
    t.setRegion(Region.getRegion(Regions.EU_WEST_1))
    t
  }

  val respons1 = listQueues

  respons1.onSuccess {
    case resul @ _ ⇒ println(s"Respons1: $resul")
  }
  respons1.onFailure {
    case error @ _ ⇒ error.printStackTrace()
  }

  readLine()

  println(System.currentTimeMillis())

  val respons2 = (1 to 256).toList.map(_ ⇒ listQueues).sequenceU.map(list ⇒ (0 /: list) {
    case (a, \/-(num)) ⇒ a + num
    case (a, _) ⇒ a
  })

  respons2.onSuccess {
    case resul @ _ ⇒ { println(System.currentTimeMillis()); println(s"Respons2: $resul") }
  }
  respons2.onFailure {
    case error @ _ ⇒ error.printStackTrace()
  }

  readLine()

  println(System.currentTimeMillis())

  val respons3 = (1 to 512).toList.map(_ ⇒ listQueues).sequenceU.map(list ⇒ (0 /: list) {
    case (a, \/-(num)) ⇒ a + num
    case (a, _) ⇒ a
  })

  respons3.onSuccess {
    case resul @ _ ⇒ { println(System.currentTimeMillis()); println(s"Respons3: $resul") }
  }
  respons3.onFailure {
    case error @ _ ⇒ error.printStackTrace()
  }

  readLine()

  println(System.currentTimeMillis())

  val respons4 = (1 to 256).toList.map(_ ⇒ listQueues).sequenceU.map(list ⇒ (0 /: list) {
    case (a, \/-(num)) ⇒ a + num
    case (a, _) ⇒ a
  })

  respons4.onSuccess {
    case resul @ _ ⇒ { println(System.currentTimeMillis()); println(s"Respons4: $resul") }
  }
  respons4.onFailure {
    case error @ _ ⇒ error.printStackTrace()
  }

  readLine()

  println(System.currentTimeMillis())

  val respons5 = (1 to 512).toList.map(_ ⇒ listQueues).sequenceU.map(list ⇒ (0 /: list) {
    case (a, \/-(num)) ⇒ a + num
    case (a, _) ⇒ a
  })

  respons5.onSuccess {
    case resul @ _ ⇒ { println(System.currentTimeMillis()); println(s"Respons5: $resul") }
  }
  respons4.onFailure {
    case error @ _ ⇒ error.printStackTrace()
  }
}