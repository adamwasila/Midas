package com.ee.midas.pipes

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import java.net.Socket
import org.specs2.mock.Mockito
import com.ee.midas.pipes.SocketConnector._
import com.ee.midas.interceptor.MidasInterceptable

@RunWith(classOf[JUnitRunner])
class SocketConnectorSpecs extends Specification with Mockito {

   "Socket Connector" should {
       "Create simplex pipe from client to server " in {
         //given
         val client : Socket = mock[Socket]
         val server : Socket = mock[Socket]

         //when
         val pipe = client ==> server

         //then
         pipe.isInstanceOf[SimplexPipe]
       }

      "Create simplex pipe from server to client " in {
        //given
        val client : Socket = mock[Socket]
        val server : Socket = mock[Socket]

        //when
        val pipe = client <== server

        //then
        pipe.isInstanceOf[SimplexPipe]
      }

      "Create duplex pipe between client and server " in {
        //given
        val client : Socket = mock[Socket]
        val server : Socket = mock[Socket]

        //when
        val pipe = client <====> server

        //then
        pipe.isInstanceOf[DuplexPipe]
      }

      "Create request intercepted duplex pipe between client and server " in {
        //given
        val client : Socket = mock[Socket]
        val server : Socket = mock[Socket]
        val interceptable: MidasInterceptable = mock[MidasInterceptable]

        //when
        val pipe = client <===|> (server, interceptable)

        //then
        pipe.isInstanceOf[DuplexPipe]
      }

      "Create response intercepted duplex pipe between client and server " in {
        //given
        val client : Socket = mock[Socket]
        val server : Socket = mock[Socket]
        val interceptable: MidasInterceptable = mock[MidasInterceptable]

        //when
        val pipe = client <|===> (server, interceptable)

        //then
        pipe.isInstanceOf[DuplexPipe]
      }

      "Create intercepted duplex pipe between client and server " in {
        //given
        val client : Socket = mock[Socket]
        val server : Socket = mock[Socket]
        val interceptable: MidasInterceptable = mock[MidasInterceptable]

        //when
        val pipe = client <|==|> (server, interceptable, interceptable)

        //then
        pipe.isInstanceOf[DuplexPipe]
      }

  }
}
