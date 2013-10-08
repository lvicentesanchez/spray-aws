package spray.contrib.aws.services

import com.amazonaws.services.sqs.model._
import com.amazonaws.services.sqs.model.transform._
import scala.concurrent.{ blocking, ExecutionContext, Future }
import spray.contrib.aws.common._

trait AwsSQS extends RequestFactoryV4 with SignerV4 { this: Client with Signer with Credentials with RequestFactory ⇒
  import AwsSQS._

  def listQueues(implicit ctxt: ExecutionContext): Future[Int] =
    pipeline.flatMap(_(request(new ListQueuesRequest()))).map(result ⇒ result.entity.asString.length)
}

object AwsSQS {
  implicit val listQueuesRequestMarshaller: ListQueuesRequestMarshaller = new ListQueuesRequestMarshaller()
}