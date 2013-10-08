package spray.contrib.aws.client

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.amazonaws.auth.{ AWSCredentials, BasicAWSCredentials }
import spray.can.Http
import spray.client.pipelining._
import spray.contrib.aws.common._
import spray.contrib.aws.services._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class SQSClient(props: SprayAWSProps)(implicit system: ActorSystem) extends AwsSQS with Client with Signer with Credentials with RequestFactory {
  implicit val contxt = system.dispatchers.lookup("pony-express.amazon-dispatcher")

  val serviceName: String = "sqs"

  val credentials: AWSCredentials = new BasicAWSCredentials(props.accessKey, props.secretKey)

  val pipeline: Future[SendReceive] =
    for (
      Http.HostConnectorInfo(connector, _) ← (IO(Http) ? Http.HostConnectorSetup(props.amazonUrl, port = 443, sslEncryption = true))(10 seconds)
    ) yield sendReceive(connector)(contxt, 10 seconds)
}