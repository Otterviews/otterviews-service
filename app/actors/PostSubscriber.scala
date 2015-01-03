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

import actors.firebase.SubscriberEventListener
import actors.messages.FirebaseMessages.FirebaseMessage
import akka.actor.{ Actor, ActorRef, Props }
import com.firebase.client.Firebase
import models.Post
import support.Global

class PostSubscriber(out: ActorRef, firebase: Firebase) extends Actor {

  private[this] val eventListener = SubscriberEventListener(self)

  override def receive: Receive = {
    case message: FirebaseMessage => broadcast(message)
    case other                    => unhandled(other)
  }

  override def preStart(): Unit = firebase.addChildEventListener(eventListener)

  override def postStop(): Unit = firebase.removeEventListener(eventListener)

  private[this] def broadcast(firebase: FirebaseMessage) =
    out ! firebase.asViewMessage[Post] {
      Post.fromDataSnapshot(firebase.dataSnapshot)
    }.toJson

}

object PostSubscriber {
  def props(out: ActorRef): Props = Props {
    new PostSubscriber(out, new Firebase(Global.config.getString("subscriber.uri")))
  }
}

