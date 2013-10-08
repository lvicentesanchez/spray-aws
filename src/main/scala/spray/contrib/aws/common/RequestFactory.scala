package spray.contrib.aws.common

import com.amazonaws.Request
import com.amazonaws.transform.Marshaller
import spray.http.HttpRequest

trait RequestFactory {
  def request[T](request: T)(implicit marshaller: Marshaller[Request[T], T]): HttpRequest
}