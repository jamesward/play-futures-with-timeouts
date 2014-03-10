package controllers

import play.api.mvc.{Action, Controller}
import scala.util.Random
import play.api.libs.concurrent.Promise
import scala.concurrent.Future
import play.api.libs.ws.WS
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.TimeUnit

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Hello Play Framework"))
  }
  
  def pause = Action.async {
    // between 5 and 10 seconds
    val delay = Random.nextInt(5) + 5
    Promise.timeout(Ok(delay.toString), delay, TimeUnit.SECONDS)
  }
  
  // future of the total paused time (which isn't total time)
  private def allPauses: Future[Int] = {
    Future.sequence {
      for (i <- 1 to 10) yield {
        // max timeout of 15 seconds so everything should be good
        WS.url("http://localhost:9000/pause").withRequestTimeout(15000).get().map(_.body.toInt)
      }
    } map (_.sum)
  }
  
  def test = Action.async {
    allPauses.map { seconds =>
      Ok(s"Total paused time was $seconds seconds")
    }
  }
  
}