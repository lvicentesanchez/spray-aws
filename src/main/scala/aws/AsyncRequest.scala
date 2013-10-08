package aws

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler
import scala.concurrent.{ Future, Promise }
import scalaz.\/

trait AsyncRequest {
  def asyncRequest[Requ <: AmazonWebServiceRequest, Resp, Error >: Throwable, Type](f: (Requ, AsyncHandler[Requ, Resp]) ⇒ Unit, g: Resp ⇒ Type, request: Requ): Future[Error \/ Type] = {
    val promise: Promise[Error \/ Type] = Promise[Error \/ Type]()
    val handler: AsyncHandler[Requ, Resp] = new AsyncHandler[Requ, Resp] {
      override def onError(exception: Exception): Unit = {
        promise.trySuccess(\/.left(exception))
      }

      override def onSuccess(request: Requ, result: Resp): Unit = {
        val resu = g(result)
        promise.trySuccess(\/.right(resu))
      }
    }
    f(request, handler)
    promise.future
  }
}
