package com.ee.midas


import com.ee.midas.pipes.{SocketConnector, DuplexPipe}
import java.net._
import com.ee.midas.utils.{DirectoryWatcher, Accumulator, Loggable}
import com.ee.midas.interceptor.{Transformer, MessageTracker, RequestInterceptor, ResponseInterceptor}
import java.nio.file.WatchEvent
import com.ee.midas.hotdeploy.DeltaFilesProcessor
import com.ee.midas.dsl.generator.ScalaGenerator
import com.ee.midas.dsl.interpreter.Reader
import com.ee.midas.dsl.Translator
import java.io.{PrintWriter, File}


object Main extends App with Loggable {

  val maxClientConnections = 50

  override def main(args:Array[String]): Unit = {

    val (midasHost,midasPort,mongoHost,mongoPort) = (args(0), args(1).toInt, args(2), args(3).toInt)

    val deltasDirURI = "deltas/"
    val srcScalaTemplateURI = "templates/Transformations.scala.template"
    val srcScalaDirURI = "generated/scala/"
    val srcScalaFilename = "Transformations.scala"
    val binDirURI = "generated/scala/bin/"
    val clazzName = "com.ee.midas.transform.Transformations"

    val deltasProcessor = new DeltaFilesProcessor(new Translator(new Reader(), new ScalaGenerator()))

    log.info(s"Processing Delta Files...")
    val loader = Main.getClass.getClassLoader
    val srcScalaDir = loader.getResource(srcScalaDirURI)
    log.info(s"Source Scala Dir = $srcScalaDir")
    val srcScalaFile = new File(srcScalaDir.getPath + srcScalaFilename)
    val srcScalaWriter = new PrintWriter(srcScalaFile, "utf-8")
    deltasProcessor.process(deltasDirURI, srcScalaTemplateURI, srcScalaWriter, srcScalaFile, binDirURI, clazzName)
    log.info(s"Completed...Processing Delta Files!")

    val deltasDir = loader.getResource(deltasDirURI)
    log.info(s"Setting up Directory Watcher...")
    val watcher = watch(deltasDir) { watchEvent =>
      log.info(s"Received ${watchEvent.kind()}, Context = ${watchEvent.context()}")
      val writer = new PrintWriter(srcScalaFile, "utf-8")
      deltasProcessor.process(deltasDirURI, srcScalaTemplateURI, writer, srcScalaFile, binDirURI, clazzName)
      writer.close()
    }

    log.info(s"Starting Midas Server...")
    val midasSocket = new ServerSocket(midasPort, maxClientConnections, InetAddress.getByName(midasHost))
    val accumulate = Accumulator[DuplexPipe](Nil)

    sys.ShutdownHookThread {
      watcher.stopWatching
      val pipes = accumulate(null)
      log.info("User Forced Stop on Midas...Closing Open Connections = ")
      pipes filter(_.isActive) map(_.forceStop)
    }

    import SocketConnector._
    while (true) {
      val application = waitForNewConnectionOn(midasSocket)
      log.info("New connection received...")
      //TODO: do something if Mongo is not available
      try{
        val mongoSocket = new Socket(mongoHost, mongoPort)
        val tracker = new MessageTracker()
        val requestInterceptable = new RequestInterceptor(tracker)
        val responseInterceptable = new ResponseInterceptor(tracker, new Transformer())

        val duplexPipe = application  <|==|> (mongoSocket, requestInterceptable, responseInterceptable)
        duplexPipe.start
        log.info("Setup DataPipe = " + duplexPipe.toString)
        accumulate(duplexPipe)
      }
      catch {
        case e: ConnectException  => println("Error : Mongo is not available")
                                     application.close()
      }
    }
  }

  private def waitForNewConnectionOn(serverSocket: ServerSocket) = {
    log.info("Listening on port " + serverSocket.getLocalPort() + " for new connections...")
    serverSocket.accept()
  }

  private def watch(dir: URL)(onEvent: WatchEvent[_] => Unit): DirectoryWatcher = {
    val watcher = new DirectoryWatcher(dir.getPath)
    new Thread(new Runnable() {
      def run() = watcher watch onEvent
    }).start()
    watcher
  }
}
