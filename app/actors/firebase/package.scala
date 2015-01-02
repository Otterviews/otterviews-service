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

import actors.firebase.ActorFirebaseMessages._
import akka.actor.ActorRef
import com.firebase.client.{ ChildEventListener, DataSnapshot, FirebaseError }

package object firebase {

  object ActorFirebaseMessages {

    case class AddedDataSnapshot(dataSnapshot: DataSnapshot)

    case class RemovedDataSnapshot(dataSnapshot: DataSnapshot)

    case class ChangedDataSnapshot(dataSnapshot: DataSnapshot)

    case class MovedDataSnapshot(dataSnapshot: DataSnapshot)

    case class CancelledDataSnapshot(firebaseError: FirebaseError)

  }

  object SubscriberEventListener {
    def apply(actor: ActorRef): ChildEventListener = {
      new ChildEventListener {
        override def onChildRemoved(dataSnapshot: DataSnapshot): Unit =
          actor ! RemovedDataSnapshot(dataSnapshot)

        override def onChildAdded(dataSnapshot: DataSnapshot, s: String): Unit =
          actor ! AddedDataSnapshot(dataSnapshot)

        override def onChildChanged(dataSnapshot: DataSnapshot, s: String): Unit =
          actor ! ChangedDataSnapshot(dataSnapshot)

        override def onChildMoved(dataSnapshot: DataSnapshot, s: String): Unit =
          actor ! MovedDataSnapshot(dataSnapshot)

        override def onCancelled(firebaseError: FirebaseError): Unit =
          actor ! CancelledDataSnapshot(firebaseError)
      }
    }
  }

}
