package spray.contrib.aws.common

import com.amazonaws.Request
import com.amazonaws.http.HttpMethodName
import com.amazonaws.transform.Marshaller
import java.net.URI
import scala.collection.JavaConverters._
import scala.language.implicitConversions
import spray.http._
import Uri.Query
import HttpProtocols.`HTTP/1.1`
import HttpMethods._

trait RequestFactoryV4 { this: RequestFactory with Signer with Credentials ⇒
  def request[T](data: T)(implicit marshaller: Marshaller[Request[T], T]): HttpRequest = {
    val awsReq = marshaller.marshall(data)
    awsReq.setEndpoint(new URI("https://sqs.eu-west-1.amazonaws.com:443"))
    awsReq.getHeaders.put("User-Agent", "spray-can/1.2-20131011")
    //val body = awsReq.getContent.asInstanceOf[StringInputStream].getString
    signature.sign(awsReq, credentials)
    awsReq.getHeaders.remove("Host")
    awsReq.getHeaders.remove("User-Agent")
    awsReq.getHeaders.remove("Content-Length")
    awsReq.getHeaders.remove("Content-Type")
    val params = awsReq.getParameters().asScala
    val uriPath: String = Option(awsReq.getResourcePath).getOrElse("/")
    val request = HttpRequest(awsReq.getHttpMethod, Uri.from(port = 443, path = uriPath), headers(awsReq), HttpEntity(MediaTypes.`application/x-www-form-urlencoded`, Query(params.toSeq: _*).toString), `HTTP/1.1`)
    request
  }

  private[this] val signature = SignatureProvider()

  private[this] implicit def bridgeMethods(m: HttpMethodName): HttpMethod = m match {
    case HttpMethodName.POST ⇒ POST
    case HttpMethodName.GET ⇒ GET
    case HttpMethodName.PUT ⇒ PUT
    case HttpMethodName.DELETE ⇒ DELETE
    case HttpMethodName.HEAD ⇒ HEAD
  }

  private[this] def headers(req: Request[_]): List[HttpHeader] = {
    req.getHeaders.asScala.map {
      case (k, v) ⇒
        HttpHeaders.RawHeader(k, v)
    }.toList
  }

}