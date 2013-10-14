package spray.contrib.aws.common

import com.amazonaws.Request
import com.amazonaws.http.HttpMethodName
import com.amazonaws.transform.Marshaller
import java.net.URI
import scala.collection.JavaConverters._
import scala.language.implicitConversions
import spray.http._
import spray.http.HttpHeaders.RawHeader
import spray.http.HttpProtocols.`HTTP/1.1`
import spray.http.HttpMethods._
import spray.http.Uri.Query

trait RequestFactoryV4 { this: RequestFactory with Signer with Credentials ⇒
  def request[T](data: T)(implicit marshaller: Marshaller[Request[T], T]): HttpRequest = {
    val request = marshaller.marshall(data)
    request.setEndpoint(new URI("https://sqs.eu-west-1.amazonaws.com:443"))
    request.getHeaders.put("User-Agent", "spray-can/1.2-20131011")
    //val body = request.getContent.asInstanceOf[StringInputStream].getString
    signature.sign(request, credentials)
    val params = request.getParameters().asScala
    val uriPath: String = Option(request.getResourcePath).getOrElse("/")
    val result = HttpRequest(methods(request.getHttpMethod), Uri.from(port = 443, path = uriPath), headers(request, illegalHeaders), HttpEntity(MediaTypes.`application/x-www-form-urlencoded`, Query(params.toSeq: _*).toString), `HTTP/1.1`)
    result
  }

  private[this] val signature = SignatureProvider()

  private[this] val methods: Map[HttpMethodName, HttpMethod] = Map(
    HttpMethodName.POST -> POST,
    HttpMethodName.GET -> GET,
    HttpMethodName.DELETE -> DELETE,
    HttpMethodName.HEAD -> HEAD
  )

  private[this] val illegalHeaders: Set[String] = Set(
    "content-length",
    "content-type",
    "host",
    "transfer-encoding",
    "user-agent"
  )

  @inline
  private[this] def headers(req: Request[_], exclude: Set[String]): List[HttpHeader] =
    req.getHeaders.asScala.foldLeft(List.empty[HttpHeader]) {
      case (acc, value @ (key, _)) if exclude(key.toLowerCase) == false ⇒ builder(value) :: acc
    }

  @inline
  private[this] val builder: Tuple2[String, String] ⇒ HttpHeader = (RawHeader.apply _).tupled
}