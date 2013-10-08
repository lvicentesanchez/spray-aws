package spray.contrib.aws.common

import scala.concurrent.Future
import spray.client.pipelining.SendReceive

trait Client {
  def pipeline: Future[SendReceive]
}