package com.ee.midas.pipes

import org.slf4j.Logger
import com.ee.midas.utils.Loggable


trait PipesMonitorComponent extends Startable with Stoppable with Loggable {
  pipe: Pipe =>

  val checkEveryMillis: Long
  val pipesMonitor = new PipesMonitor

  abstract override def start: Unit = {
    log.info("Starting Pipe..." + pipe.name)
    //Start Target First
    super.start
    log.info("Starting PipesMonitor..." + pipesMonitor.toString)
    pipesMonitor.start
  }

  abstract override def stop: Unit = {
    log.info("Stopping Pipe..." + pipe.name)
    //Stop Target First
    super.stop
    log.info("Stopping PipesMonitor..." + pipesMonitor.toString)
    pipesMonitor.close
  }

  abstract override def forceStop: Unit = {
    log.info("Forcing Stop on Pipe..." + pipe.name)
    //ForceStop Target First
    super.forceStop
    log.info("Stopping PipesMonitor..." + pipesMonitor.toString)
    pipesMonitor.close
  }

  class PipesMonitor extends Thread(pipe.name + "-Monitor-Thread") {
    private var keepRunning = true

    def close : Unit = keepRunning = false

    override def run = {
      while (keepRunning) {
        try {
          pipe.inspect
          if (!pipe.isActive) {
            val threadName = Thread.currentThread().getName()
            log.error("[" + threadName + "] Detected Broken Pipe...Initiating Pipe Closure")
            pipe.forceStop
            keepRunning = false
            log.error("[" + threadName + "] Shutting down Monitor")
          }
          Thread.sleep(checkEveryMillis)
        } catch {
          case e: InterruptedException => log.error("Status Thread Interrupted")
            keepRunning = false
        }
      }
    }

    override def toString = getName
  }
}
