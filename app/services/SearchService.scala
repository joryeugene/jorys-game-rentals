package services

import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

import models.ServiceResponse
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.util.{Failure, Success, Try}

object SearchService {

  val SEARCH_SERVICE_URL =
    "http://www.giantbomb.com/api/search/?api_key=7da59c68027d5e706bf85af4a73e54df639e9580&format=json&resources=game&field_list=name,image,deck,platforms&query="
  val DOUBLE_QUOTE = "\""

  val REQUEST_TIMEOUT_DURATION: FiniteDuration =
    Duration.create(20, TimeUnit.SECONDS)
  val TIMEOUT = "Timeout"
  val UNKNOWN_HOST = "Unknown Host"

  def getServiceResponse(ws: WSClient,
                         searchString: String): ServiceResponse = {
    val serviceResponseFuture: Future[Try[WSResponse]] =
      generateWSResponse(ws, searchString)
    resolveServiceResponse(serviceResponseFuture)
  }

  def generateWSResponse(ws: WSClient,
                         searchString: String): Future[Try[WSResponse]] = {
    ws.url(getUrl(searchString))
      .withRequestTimeout(REQUEST_TIMEOUT_DURATION)
      .get()
      .map(Success(_))
      .recover({ case x => Failure(x) })
  }

  def getUrl(searchString: String): String = {
    new StringBuilder(SEARCH_SERVICE_URL)
      .append(DOUBLE_QUOTE)
      .append(searchString)
      .append(DOUBLE_QUOTE)
      .toString()
  }

  def resolveServiceResponse(
      serviceResponseFuture: Future[Try[WSResponse]]): ServiceResponse = {
    Await.result(serviceResponseFuture, Duration.Inf)
    val triedSearchResponse: Try[WSResponse] =
      serviceResponseFuture.value.get.get

    val serviceResponse = new ServiceResponse

    triedSearchResponse match {
      case Success(value) =>
        serviceResponse.success = true
        serviceResponse.gameSearchResults =
          ResultsParser.getGameSearchResults(value)
        serviceResponse.responseDetail = value.statusText
      case Failure(e: TimeoutException) =>
        serviceResponse.success = false
        serviceResponse.responseDetail = TIMEOUT
      case Failure(e: UnknownHostException) =>
        serviceResponse.success = false
        serviceResponse.responseDetail = UNKNOWN_HOST
      case Failure(e) =>
        serviceResponse.success = false
        serviceResponse.responseDetail = e.getMessage
    }

    serviceResponse
  }

}
