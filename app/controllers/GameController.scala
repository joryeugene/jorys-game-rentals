package controllers

import javax.inject._

import akka.actor.ActorSystem
import controllers.GameController.SHOPPING_CART
import models.SearchInfo
import play.api.libs.json._
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

  def addToCart(fingerprint: String, name: String) = Action {

    if (SHOPPING_CART.contains(fingerprint)) {
      val usersGames: ListBuffer[String] = SHOPPING_CART(fingerprint)
      if (!usersGames.contains(name)) {
        usersGames.+=(name)
      }
    } else {
      SHOPPING_CART.put(fingerprint, ListBuffer(name))
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

  def getCartItems(fingerprint: String) = Action {
    var usersGames: List[String] = List.empty[String]

    if (SHOPPING_CART.contains(fingerprint)) {
      usersGames = SHOPPING_CART(fingerprint).toList
    }

    Ok(views.html.shoppingCartContents(usersGames))
  }

  def emptyCart(fingerprint: String) = Action {
    SHOPPING_CART(fingerprint) = ListBuffer.empty[String]
    Ok(views.html.shoppingCartContents(List.empty[String]))
  }

  def getDatabaseInfo() = Action {
    val databaseJson: List[JsObject] =
      SHOPPING_CART
        .map(
          entry =>
            JsObject(
              List(
                "userFingerprint" -> JsString(entry._1),
                "cart" -> JsArray(entry._2.map(game => JsString(game)).toList)
              )))
        .toList
    Ok(JsArray(databaseJson))
  }

}

object GameController {
  val SHOPPING_CART: mutable.HashMap[String, ListBuffer[String]] =
    mutable.HashMap.empty
}
