package controllers

import javax.inject._

import akka.actor.ActorSystem
import models.SearchInfo
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, _}
import services.SearchService._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class GameController @Inject()(ws: WSClient, actorSystem: ActorSystem)
    extends Controller {

  def search(query: String) = Action.async {
    val searchInfoFuture = Future {
      new SearchInfo(_searchString = query,
                     _serviceResponse = getServiceResponse(ws, query))
    }
    searchInfoFuture.map(searchInfo =>
      Ok(views.html.searchResponse(searchInfo)))
  }

//  def search(searchString: String, fingerprint: String) = Action.async {
//    val searchResultsFuture = Future {
//      createSearchInfoResponse(searchString, fingerprint)
//    }
//    searchResultsFuture.map(searchResults =>
//      Ok(views.html.searchResponse(searchResults)))
//  }

//  _numOfCartItems = CartHelper.getNumOfItems(fingerprint),

}

object GameController {
  val SHOPPING_CART: mutable.HashMap[String, ListBuffer[String]] =
    mutable.HashMap.empty
}
