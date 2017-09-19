package services

import models.Game
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsArray, JsPath, Reads}
import play.api.libs.ws.WSResponse

object ResultsParser {

  implicit val gameReads: Reads[Game] =
    ((JsPath \ "name").read[String] and (JsPath \ "deck")
      .readNullable[String] and (JsPath \ "platforms")
      .readNullable[JsArray]
      .map(
        jsArray => {
          if (jsArray.isDefined) {
            jsArray.get.value
              .map(jsValue => (jsValue \ "name").as[String])
              .toList
          } else {
            List.empty[String]
          }
        }
      ) and (JsPath \ "image" \ "thumb_url")
      .readNullable[String])(Game.apply _)

  def getGameSearchResults(responseValue: WSResponse): List[Game] = {
    (responseValue.json \ "results").as[JsArray].as[List[Game]]
  }

}
