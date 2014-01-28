package com.ee.midas.utils

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification
import java.nio.file.Paths
import java.util.regex.Pattern
import java.io.File

@RunWith(classOf[JUnitRunner])
class FileVisitorSpecs extends Specification {
  "FileVisitor" should {

    val classLoader = FileVisitorSpecs.this.getClass.getClassLoader


    "return list of files in the directory specified" in {
      //Given
      val rootDir = Paths.get(classLoader.getResource("com/ee/midas").toURI)
      val fileVisitor = new FileVisitor(rootDir, Pattern.compile(".*\\.class$"))

      //When
      val files = fileVisitor.visit

      //Then
      files must not be empty
      files must containMatch("com"   + File.separator
                            + "ee"    + File.separator
                            + "midas" + File.separator
                            + "utils" + File.separator
                            + "FileVisitor")
    }

    "return empty list when the directory does not contain any files" in {
      //Given
      val rootDir = Paths.get(classLoader.getResource("com/ee/midas").toURI)
      val fileVisitor = new FileVisitor(rootDir, Pattern.compile(".*\\.deltaFile$"))

      //When
      val files = fileVisitor.visit

      //Then
      files must be empty
    }
  }
}
