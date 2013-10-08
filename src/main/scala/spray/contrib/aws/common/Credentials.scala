package spray.contrib.aws.common

import com.amazonaws.auth.AWSCredentials

trait Credentials {
  def credentials: AWSCredentials
}