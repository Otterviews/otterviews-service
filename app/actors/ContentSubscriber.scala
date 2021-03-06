// Copyright (C) 2011-2012 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package actors

import akka.actor.{ Actor, Props }
import play.api.Play
import play.api.libs.concurrent.Akka
import play.api.libs.iteratee.Enumerator

class ContentSubscriber extends Actor {

  var outs = Seq[Enumerator[String]]()
  var cached = Seq[String]()

  override def receive: Receive = {
    case AddPost(content)  => broadcastAndCache(content.toJson)
    case AddDraft(content) => broadcastAndCache(content.toJson)
    case AddClient(e)      => addAndFlush(e)
    case other             => unhandled(other)
  }

  private[this] def addAndFlush(e: Enumerator[String]) {
    e >>> Enumerator(cached: _*)
    outs ++= Seq(e)
  }

  private[this] def broadcastAndCache(content: String) {
    if (!cached.contains(content)) {
      outs.foreach(_ >>> Enumerator(content))
      cached ++= Seq(content)
    }
  }
}

object ContentSubscriber {
  val ref = Akka.system(Play.current).actorOf(Props(new ContentSubscriber()))
}

