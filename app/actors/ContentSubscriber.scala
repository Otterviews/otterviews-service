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

import akka.actor.Props
import akka.stream.actor.{ ActorSubscriber, ActorSubscriberMessage, WatermarkRequestStrategy }
import messages.AddClient
import play.api.libs.concurrent.Akka
import play.api.libs.iteratee.Enumerator

object ContentSubscriber {
  val ref = Akka.system.actorOf(Props(new ContentSubscriber(10)))
}

class ContentSubscriber(limit: Int) extends ActorSubscriber {

  var outs = Seq[Enumerator[String]]()
  var cached = Seq[String]()

  override val requestStrategy = WatermarkRequestStrategy(limit)

  override def receive: Receive = {
    case ActorSubscriberMessage.OnNext(message) => message match {
      case Some(msg) if msg.isInstanceOf[String] =>
        val content: String = msg.asInstanceOf[String]
        outs.foreach(_ >>> Enumerator(content))
        cached ++= Seq(content)
      case None => unhandled()
    }
    case AddClient(e) =>
      e >>> Enumerator(cached: _*)
      outs ++= Seq(e)
  }

}
