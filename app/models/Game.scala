package models

case class Game(name: String,
                deck: Option[String],
                platforms: List[String],
                thumbUrl: Option[String])
