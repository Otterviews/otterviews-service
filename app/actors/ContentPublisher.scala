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

import java.util.concurrent.TimeUnit

import akka.actor.{ Cancellable, Props }
import akka.stream.actor.{ ActorPublisher, ActorPublisherMessage }
import play.api.libs.concurrent.Akka

import scala.concurrent.duration.FiniteDuration

object ContentPublisher {
  val ref = Akka.system.actorOf(Props(new ContentPublisher(100)))
}

class ContentPublisher(interval: Int) extends ActorPublisher[String] {

  var schedule: Option[Cancellable] = None

  override def preStart(): Unit = {

    schedule = Some(context.system.scheduler.schedule(
      FiniteDuration(interval, TimeUnit.SECONDS),
      FiniteDuration(interval, TimeUnit.SECONDS),
      new Runnable {
        override def run(): Unit = {
          retrieveContent.foreach(onNext)
        }
      }
    ))
  }

  override def postStop(): Unit = schedule.foreach(_.cancel())

  override def receive: Receive = {
    case ActorPublisherMessage.Cancel => schedule.foreach(_.cancel())
  }

  private def retrieveContent: Seq[String] = ???
}
