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

package com.ee.midas.interceptor

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MessageTrackerSpecs extends Specification {

  "Message Tracker" should {
     "Track Id with Collection Name" in {
       //given
       val tracker = new MessageTracker()
       val ids = tracker.ids

       //when
       tracker.track(id = 1 , fullCollectionName = "midas")

       //then
       ids.get(key = 1) mustEqual Some("midas")
     }

    "Give Full Collection Name For Given ID" in {
      //given
      val tracker = new MessageTracker()

      //when
      tracker.track(id = 1 , fullCollectionName = "midas")

      //then
      tracker.fullCollectionName(id = 1) mustEqual Some("midas")
    }

    "Untrack a given ID" in {
      //given
      val tracker = new MessageTracker()
      val ids = tracker.ids
      tracker.track(id = 1 , fullCollectionName = "midas")

      ids.get(key = 1) mustEqual Some("midas")

      //when
      tracker.untrack(id = 1)

      //then
      ids.get(key = 1) mustEqual None
    }
  }

}
