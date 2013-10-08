package spray.contrib.aws.common

import com.amazonaws.Request
import com.amazonaws.auth.AWSCredentials

trait Signer {
  type SignatureProvider <: SignatureProviderLike

  trait SignatureProviderLike { this: SignatureProvider ⇒
    def sign[T](request: Request[T], credentials: AWSCredentials): Unit
  }

  def SignatureProvider(): SignatureProvider
}
