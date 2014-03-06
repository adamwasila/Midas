/******************************************************************************
* Copyright (c) 2014, Equal Experts Ltd
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are
* met:
*
* 1. Redistributions of source code must retain the above copyright notice,
*    this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in the
*    documentation and/or other materials provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
* PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
* OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
* EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
* PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
* LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
* The views and conclusions contained in the software and documentation
* are those of the authors and should not be interpreted as representing
* official policies, either expressed or implied, of the Midas Project.
******************************************************************************/

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

    val os = System.getProperty("os.name")

    val separator = if (os.contains("Win"))  File.separator + File.separator else File.separator

    "return list of files in the directory specified" in {
      //Given
      val rootDir = Paths.get(classLoader.getResource("com/ee/midas").toURI)
      val fileVisitor = new FileVisitor(rootDir, Pattern.compile(".*\\.class$"))

      //When
      val files = fileVisitor.visit

      //Then
      files must not be empty
      files must containMatch("com"   + separator
                            + "ee"    + separator
                            + "midas" + separator
                            + "utils" + separator
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
