package com.ee.midas.data

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import java.io.{ByteArrayOutputStream, ByteArrayInputStream}

@RunWith(classOf[JUnitRunner])
object DuplexPipeSpecs extends Specification with Mockito {

    "duplex channel" should {

      "send and receive data" in {
        // given
        val request = "Hello World Request".getBytes()
        val response = "Hello World Response".getBytes()
        val midasClientInputStream = new ByteArrayInputStream(request)
        val midasClientOutputStream = new ByteArrayOutputStream()
        val targetMongoInputStream = new ByteArrayInputStream(response)
        val targetMongoOutputStream = new ByteArrayOutputStream()

        val duplexPipe = new DuplexPipe(midasClientInputStream, midasClientOutputStream,
                                        targetMongoInputStream, targetMongoOutputStream)

        duplexPipe.transferData()

        targetMongoOutputStream.toByteArray must beEqualTo(request)
        midasClientOutputStream.toByteArray must beEqualTo(response)
      }
    }
}
