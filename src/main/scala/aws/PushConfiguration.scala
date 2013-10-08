package aws

import com.amazonaws.services.sns.AmazonSNSAsync

trait PushConfiguration {
  def notification: AmazonSNSAsync
}
