package aws

import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.sqs.model._
import scala.collection.JavaConverters._
import scala.concurrent.{ ExecutionContext, Future }
import scalaz.\/

trait QueueModuleImpl { self: QueueModule with QueueConfiguration with AsyncRequest ⇒
  def deleteMessage(queueUrl: String, messages: List[Tuple2[String, Any]]): Future[Throwable \/ Tuple2[List[Tuple2[String, Any]], String]] = {
    val request: DeleteMessageBatchRequest = new DeleteMessageBatchRequest().withQueueUrl(queueUrl).withEntries(messages.map(_ match {
      case (uuid, message: Message) ⇒ new DeleteMessageBatchRequestEntry(uuid, message.getReceiptHandle())
    }).asJava)
    asyncRequest(deleteMessage _, (result: DeleteMessageBatchResult) ⇒ fromDeleteMessage(messages, result), request)
  }

  def readMessage(queueUrl: String, nrOfMessages: Int): Future[Throwable \/ List[Message]] = {
    val request: ReceiveMessageRequest = new ReceiveMessageRequest(queueUrl).withMaxNumberOfMessages(nrOfMessages)
    asyncRequest(receiveMessage _, fromReceiveMessage _, request)
  }

  def sendMessage(queueUrl: String, message: String): Future[Throwable \/ String] = {
    val request: SendMessageRequest = new SendMessageRequest(queueUrl, message)
    asyncRequest(sendMessage _, fromSendMsg _, request)
  }

  def queueAttributes(queueUrl: String, attributes: List[String]): Future[Throwable \/ Map[String, String]] = {
    val request: GetQueueAttributesRequest = new GetQueueAttributesRequest(queueUrl).withAttributeNames(attributes.asJava)
    asyncRequest(queueAttributes _, fromQueueAttributes _, request)
  }

  def listQueues: Future[Throwable \/ Int] = {
    val request: ListQueuesRequest = new ListQueuesRequest()
    asyncRequest(listQueues _, fromListQueues _, request)
  }

  private def deleteMessage(request: DeleteMessageBatchRequest, handler: AsyncHandler[DeleteMessageBatchRequest, DeleteMessageBatchResult]): Unit = {
    queue.deleteMessageBatchAsync(request, handler)
  }

  private def fromDeleteMessage(messages: List[Tuple2[String, Any]], result: DeleteMessageBatchResult): Tuple2[List[Tuple2[String, Any]], String] = {
    Tuple2(messages, result.toString())
  }

  private def queueAttributes(request: GetQueueAttributesRequest, handler: AsyncHandler[GetQueueAttributesRequest, GetQueueAttributesResult]): Unit = {
    queue.getQueueAttributesAsync(request, handler)
  }

  private def fromQueueAttributes(result: GetQueueAttributesResult): Map[String, String] = {
    result.getAttributes().asScala.toMap
  }

  private def receiveMessage(request: ReceiveMessageRequest, handler: AsyncHandler[ReceiveMessageRequest, ReceiveMessageResult]): Unit =
    queue.receiveMessageAsync(request, handler)

  private def fromReceiveMessage(result: ReceiveMessageResult): List[Message] = {
    result.getMessages().asScala.toList
  }

  private def sendMessage(request: SendMessageRequest, handler: AsyncHandler[SendMessageRequest, SendMessageResult]): Unit =
    queue.sendMessageAsync(request, handler)

  private def fromSendMsg(result: SendMessageResult): String = {
    result.getMessageId()
  }

  private def listQueues(request: ListQueuesRequest, handler: AsyncHandler[ListQueuesRequest, ListQueuesResult]): Unit =
    queue.listQueuesAsync(request, handler)

  private def fromListQueues(result: ListQueuesResult): Int = {
    result.getQueueUrls().size
  }
}
