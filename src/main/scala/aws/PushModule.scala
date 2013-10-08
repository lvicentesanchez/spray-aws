package aws

import scala.concurrent.{ ExecutionContext, Future }
import scalaz.\/
import scalaz.effect.IO

trait PushModule { self: PushConfiguration ⇒
  def publish(user: String, message: String): Future[Throwable \/ String]
}
