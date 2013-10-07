import akka.io.IO
import akka.pattern.ask
import akka.actor.ActorSystem
import akka.util.Timeout
import com.amazonaws.{ AmazonServiceException, Request, AmazonWebServiceResponse, DefaultRequest }
import com.amazonaws.auth.{ AWS4Signer, BasicAWSCredentials }
import com.amazonaws.http.{ HttpResponse ⇒ AWSHttpResponse, JsonErrorResponseHandler, JsonResponseHandler, HttpMethodName }
import com.amazonaws.services.sqs.model._
import com.amazonaws.services.sqs.model.transform._
import com.amazonaws.transform.{ JsonErrorUnmarshaller, JsonUnmarshallerContext, Unmarshaller, Marshaller }
import com.amazonaws.util.StringInputStream
import com.amazonaws.util.json.JSONObject
import java.net.URI
import java.util.{ List ⇒ JList }
import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.duration._
import spray.can.Http
import spray.can.client.ClientConnectionSettings
import spray.http._
import HttpHeaders._
import spray.http.Uri.Query
import spray.http.HttpMethods._
import spray.http.HttpProtocols.`HTTP/1.1`
import spray.client.pipelining._

object SprayAWS extends App {
  implicit val timeout = Timeout(60 seconds)
  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures

  val accessKey: String = ""
  val secretKey: String = ""

  val endpoint: String = "sqs.eu-west-1.amazonaws.com"
  val endpointUri = new URI(s"https://$endpoint:443")
  val clientSettings = ClientConnectionSettings(system)

  def exceptionUnmarshallers: JList[JsonErrorUnmarshaller] = new java.util.ArrayList[JsonErrorUnmarshaller]()

  val pipeline: Future[SendReceive] =
    for (
      Http.HostConnectorInfo(connector, _) ← IO(Http) ? Http.HostConnectorSetup(endpoint, port = 443, sslEncryption = true)
    ) yield sendReceive(connector)

  //val pipeline = sendReceive

  val credentials = new BasicAWSCredentials(accessKey, secretKey)
  val signer = new AWS4Signer()
  signer.setServiceName("sqs")

  //val `application/x-amz-json-1.0` = MediaType.custom("application/x-amz-json-1.0")

  def request[T](t: T)(implicit marshaller: Marshaller[Request[T], T]): HttpRequest = {
    val awsReq = marshaller.marshall(t)
    awsReq.setEndpoint(endpointUri)
    awsReq.getHeaders.put("User-Agent", clientSettings.userAgentHeader)
    //val body = awsReq.getContent.asInstanceOf[StringInputStream].getString
    signer.sign(awsReq, credentials)
    awsReq.getHeaders.remove("Host")
    awsReq.getHeaders.remove("User-Agent")
    awsReq.getHeaders.remove("Content-Length")
    awsReq.getHeaders.remove("Content-Type")
    val params = awsReq.getParameters().asScala
    val uriPath: String = Option(awsReq.getResourcePath).getOrElse("/")
    val request = HttpRequest(awsReq.getHttpMethod, Uri.from(path = uriPath), headers(awsReq), HttpEntity(MediaTypes.`application/x-www-form-urlencoded`, Query(params.toSeq: _*).toString), `HTTP/1.1`)
    println(request)
    request
  }

  def headers(req: Request[_]): List[HttpHeader] = {
    req.getHeaders.asScala.map {
      case (k, v) ⇒
        HttpHeaders.RawHeader(k, v)
    }.toList
  }

  implicit def bridgeMethods(m: HttpMethodName): HttpMethod = m match {
    case HttpMethodName.POST ⇒ POST
    case HttpMethodName.GET ⇒ GET
    case HttpMethodName.PUT ⇒ PUT
    case HttpMethodName.DELETE ⇒ DELETE
    case HttpMethodName.HEAD ⇒ HEAD
  }

  implicit val listQueuesRequestMarshaller: ListQueuesRequestMarshaller = new ListQueuesRequestMarshaller()

  val response = pipeline.flatMap(_(request(new ListQueuesRequest()))).map(response ⇒ response.entity.data.asString)

  response.onSuccess {
    case resul @ _ ⇒ println(resul)
  }
  response.onFailure {
    case error @ _ ⇒ error.printStackTrace()
  }
}