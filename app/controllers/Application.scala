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

package controllers

import actors._
import models.Content
import play.api.libs.iteratee.{ Enumerator, _ }
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.WebSocket

object Application extends Controller {

  implicit val contentReads = Json.reads[Content]

  def posts: Action[JsValue] = Action(parse.json) { implicit request =>
    ContentSubscriber.ref ! AddPost(request.body.as[Content])
    Ok(Json.obj("status" -> "OK"))
  }

  def drafts: Action[JsValue] = Action(parse.json) { implicit request =>
    ContentSubscriber.ref ! AddDraft(request.body.as[Content])
    Ok(Json.obj("status" -> "OK"))
  }

  def content: WebSocket[String, String] = WebSocket.using[String] { request =>
    val out = Enumerator.empty[String]

    ContentSubscriber.ref ! AddClient(out)

    (Iteratee.ignore[String], out)
  }

  def health: Action[AnyContent] = Action {
    Ok(Json.obj("status" -> "OK"))
  }

}
