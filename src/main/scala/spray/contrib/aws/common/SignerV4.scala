package spray.contrib.aws.common

import com.amazonaws.Request
import com.amazonaws.auth.{ AWS4Signer, AWSCredentials }

trait SignerV4 { this: Signer ⇒
  def serviceName: String

  class SignatureProvider(service: String) extends SignatureProviderLike {
    def sign[T](request: Request[T], credentials: AWSCredentials): Unit = {
      signer.sign(request, credentials)
    }

    private[this] val signer = new AWS4Signer()
  }

  override def SignatureProvider(): SignatureProvider = new SignatureProvider(serviceName)
}