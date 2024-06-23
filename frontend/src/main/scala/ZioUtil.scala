package org.treemage

import zio.*

object ZioUtil:
  implicit val ec: scala.concurrent.ExecutionContext =
    scala.concurrent.ExecutionContext.global
  extension [R, E, A](self: ZIO[R & Scope, E, A])
    def withDefaultScope: ZIO[R, E, A] = self.provideSomeLayer[R](Scope.default)

  extension [R, E, A](self: ZIO[R, E, A])
    def unsafeRunToFuture(using
        R =:= Any,
        E <:< Throwable
    ) =
      Unsafe
        .unsafely(unsafe ?=>
          Runtime.default.unsafe
            .runToFuture(
              self.asInstanceOf[ZIO[Any, Throwable, A]]
            )
        )
    def unsafeAsyncRunToEitherWithRuntime(runtime: Runtime[R])(
        callback: Either[E, A] => Unit
    ): Unit =
      Unsafe
        .unsafely(unsafe ?=>
          runtime.unsafe
            .runToFuture(self.either)
        )
        .onComplete(either => callback(either.get))
