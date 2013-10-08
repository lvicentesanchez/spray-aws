package aws

import argonaut._
import Argonaut._
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.sns.{ AmazonSNSAsync, AmazonSNSAsyncClient }
import com.amazonaws.services.sns.model._
import scala.collection.immutable
import scala.collection.JavaConverters._
import scala.concurrent.{ ExecutionContext, Future, Promise }
import scalaz.Monad
import scalaz.\/
import scalaz.effect.IO

trait PushModuleImpl { self: PushModule with PushConfiguration with AsyncRequest ⇒
  override def publish(user: String, message: String): Future[Throwable \/ String] = {
    val request: PublishRequest = new PublishRequest()

    request.setTargetArn(user)
    request.setMessageStructure("json")
    request.setMessage(
      (
        ("default" := message) ->:
        ("GCM" := jString((
          ("delay_while_idle" := jBool(true)) ->:
          ("dry_run" := jBool(true)) ->:
          ("data" :=
            (
              ("msgcnt" := jNumber(10)) ->:
              ("message" := jString(message)) ->:
              ("title" := jString("none")) ->: jEmptyObject
            )
          ) ->: jEmptyObject).nospaces
        )) ->: jEmptyObject).nospaces
    )

    asyncRequest(publishMessage _, fromPublishMessage _, request)
  }

  private def publishMessage(request: PublishRequest, handler: AsyncHandler[PublishRequest, PublishResult]): Unit =
    notification.publishAsync(request, handler)

  private def fromPublishMessage(result: PublishResult): String = {
    result.getMessageId()
  }
}
