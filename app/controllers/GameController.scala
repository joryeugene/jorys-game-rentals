package controllers

import javax.inject._

import akka.actor.ActorSystem
import controllers.GameController.SHOPPING_CART
import models.{SavedGame, SearchInfo}
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

  def addToCart(fingerprint: String,
                name: String,
                deck: String,
                platforms: String,
                thumbUrl: String) = Action {

    val newGameToSave = SavedGame(fingerprint, name, deck, platforms, thumbUrl)

    if (SHOPPING_CART.contains(fingerprint)) {
      val usersGames: ListBuffer[SavedGame] = SHOPPING_CART(fingerprint)
      if (!usersGames.contains(newGameToSave)) {
        usersGames.+=(newGameToSave)
      }
    } else {
      SHOPPING_CART.put(fingerprint, ListBuffer(newGameToSave))
    }

    Ok(getNumOfCartItemsForUser(fingerprint))
  }

  def getNumOfCartItems(fingerprint: String) = Action {
    Ok(getNumOfCartItemsForUser(fingerprint))
  }

  def getNumOfCartItemsForUser(fingerprint: String) = {
    var numOfItems = 0

    if (SHOPPING_CART.contains(fingerprint)) {
      numOfItems = SHOPPING_CART(fingerprint).length
    }

    numOfItems.toString
  }

}

object GameController {
  val SHOPPING_CART: mutable.HashMap[String, ListBuffer[SavedGame]] =
    mutable.HashMap.empty
}
