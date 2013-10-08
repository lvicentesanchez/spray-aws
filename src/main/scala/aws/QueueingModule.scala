package aws

import com.amazonaws.services.sqs.model.Message
import scala.concurrent.{ ExecutionContext, Future }
import scalaz.\/

trait QueueModule { self: QueueConfiguration ⇒
  def deleteMessage(queueUrl: String, messages: List[Tuple2[String, Any]]): Future[Throwable \/ Tuple2[List[Tuple2[String, Any]], String]]
  def readMessage(queueUrl: String, nrOfMessages: Int): Future[Throwable \/ List[Message]]
  def sendMessage(queueUrl: String, message: String): Future[Throwable \/ String]
  def queueAttributes(queueUrl: String, attributes: List[String]): Future[Throwable \/ Map[String, String]]
  def listQueues: Future[Throwable \/ Int]
}
