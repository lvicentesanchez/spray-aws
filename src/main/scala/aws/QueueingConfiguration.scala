package aws

import com.amazonaws.services.sqs.AmazonSQSAsync

trait QueueConfiguration {
  def queue: AmazonSQSAsync
}
