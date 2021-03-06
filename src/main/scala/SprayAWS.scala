import akka.actor.ActorSystem
import scala.concurrent.Future
import scalaz.contrib.std.scalaFuture._
import scalaz.std.list._
import scalaz.syntax.traverse._
import spray.contrib.aws.client._
import spray.contrib.aws.common._

object SprayAWS extends App {
  implicit val system = ActorSystem()
  implicit val contxt = system.dispatchers.lookup("pony-express.amazon-dispatcher")

  val accessKey: String = ""
  val secretKey: String = ""

  val sqsclient: SQSClient = new SQSClient(SprayAWSProps(accessKey, secretKey, "sqs.eu-west-1.amazonaws.com"))

  val respons1 = sqsclient.listQueues

  respons1.onSuccess {
    case resul @ _ ⇒ println(s"Respons1: $resul")
  }
  respons1.onFailure {
    case error @ _ ⇒ error.printStackTrace()
  }

  readLine()

  println(System.currentTimeMillis())

  val respons2 = (1 to 256).toList.map(_ ⇒ sqsclient.listQueues).sequenceU.map(_.sum)

  respons2.onSuccess {
    case resul @ _ ⇒ { println(System.currentTimeMillis()); println(s"Respons2: $resul") }
  }
  respons2.onFailure {
    case error @ _ ⇒ error.printStackTrace()
  }

  readLine()

  println(System.currentTimeMillis())

  val respons3 = (1 to 512).toList.map(_ ⇒ sqsclient.listQueues).sequenceU.map(_.sum)

  respons3.onSuccess {
    case resul @ _ ⇒ { println(System.currentTimeMillis()); println(s"Respons3: $resul") }
  }
  respons3.onFailure {
    case error @ _ ⇒ error.printStackTrace()
  }

  readLine()

  println(System.currentTimeMillis())

  val respons4 = (1 to 256).toList.map(_ ⇒ sqsclient.listQueues).sequenceU.map(_.sum)

  respons4.onSuccess {
    case resul @ _ ⇒ { println(System.currentTimeMillis()); println(s"Respons4: $resul") }
  }
  respons4.onFailure {
    case error @ _ ⇒ error.printStackTrace()
  }

  readLine()

  println(System.currentTimeMillis())

  val respons5 = (1 to 512).toList.map(_ ⇒ sqsclient.listQueues).sequenceU.map(_.sum)

  respons5.onSuccess {
    case resul @ _ ⇒ { println(System.currentTimeMillis()); println(s"Respons5: $resul") }
  }
  respons4.onFailure {
    case error @ _ ⇒ error.printStackTrace()
  }
}