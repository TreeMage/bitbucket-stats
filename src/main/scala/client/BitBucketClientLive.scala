package org.treemage
package client

import config.{ApplicationConfig, BitbucketConfig}
import model.RequestedCount
import model.response.BitBucketApiError
import model.response.pullrequest.*

import zio.*
import zio.http.{Client, Header, MediaType, Request}
import zio.schema.codec.BinaryCodec

private case class BitBucketClientLive(client: Client, config: BitbucketConfig)
    extends BitbucketClient:
  private val BASE_URL =
    s"https://api.bitbucket.org/2.0/repositories/${config.workspace}/${config.repository}"

  private def makeBaseRequest(url: String): Request =
    Request
      .get(url)
      .addHeader("Authorization", s"Bearer ${config.accessToken}")
      .addHeader(Header.Accept(MediaType.application.json))

  private def fetchPage[A: BinaryCodec](
      request: Request
  ): ZIO[Scope, BitBucketApiError, A] =
    for
      response <- client
        .request(request)
        .mapError(BitBucketApiError.RequestFailed.apply)
      _ <- ZIO.when(response.status.isError)(
        ZIO.fail(BitBucketApiError.fromStatusCode(response.status.code))
      )
      body <- response.body
        .to[A]
        .mapError(BitBucketApiError.RequestFailed.apply)
    yield body

  private def fetchUntil[A: BinaryCodec](
      startUrl: String,
      makeRequest: String => Request,
      urlExtractor: A => Option[String],
      continue: List[A] => Boolean
  ): ZIO[Scope, BitBucketApiError, List[A]] =
    for
      first <- fetchPage(makeRequest(startUrl))
      result <- ZIO.iterate((first, List.empty[A])) { case (page, acc) =>
        urlExtractor(page).nonEmpty && continue(acc)
      } { case (page, acc) =>
        for next <- fetchPage(makeRequest(urlExtractor(page).get))
        yield (next, next +: acc)
      }
    yield result._2.reverse

  override def listPullRequests(
      state: PullRequestState,
      count: RequestedCount
  ): ZIO[Scope, BitBucketApiError, List[PullRequestResponse]] =
    val url = s"$BASE_URL/pullrequests"
    def shouldContinue(
        pages: List[BitBucketPullRequestResponse]
    ): Boolean =
      count match
        case RequestedCount.All => true
        case RequestedCount.Fixed(target) =>
          pages.map(_.values.length).sum < target

    def makeRequest(url: String) =
      makeBaseRequest(url).addQueryParam("state", state.asQueryParam)

    for responses <- fetchUntil[BitBucketPullRequestResponse](
        url,
        makeRequest,
        _.next,
        shouldContinue
      )
    yield
      val prs = responses.flatMap(_.values)
      count match
        case RequestedCount.All           => prs
        case RequestedCount.Fixed(target) => prs.take(target)

  override def listPullRequestActivity(
      pullRequestId: Int
  ): ZIO[Scope, BitBucketApiError, List[
    PullRequestActivityResponseValueWrapper
  ]] =
    val url = s"$BASE_URL/pullrequests/$pullRequestId/activity"
    for activities <- fetchUntil[PullRequestActivityResponse](
        url,
        makeBaseRequest,
        _.next,
        _ => true
      )
    yield activities.flatMap(_.values)

object BitBucketClientLive {
  val layer: ZLayer[Client & ApplicationConfig, Nothing, BitbucketClient] =
    ZLayer {
      for
        client <- ZIO.service[Client]
        config <- ZIO.service[ApplicationConfig]
      yield BitBucketClientLive(client, config.bitbucket)
    }
}
