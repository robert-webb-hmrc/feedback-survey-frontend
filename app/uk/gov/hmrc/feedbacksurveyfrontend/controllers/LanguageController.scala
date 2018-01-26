/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import play.api.{Configuration, Play}
import uk.gov.hmrc.play.frontend.controller.{FrontendController, UnauthorisedAction}

import scala.concurrent.Future

object LanguageController extends LanguageController

trait LanguageController extends FrontendController with I18nSupport {

  implicit val messagesApi: MessagesApi = Play.current.injector.instanceOf[MessagesApi]

  def enGb(redirectUrl: String): Action[AnyContent] = UnauthorisedAction.async { request =>
    Future.successful(Redirect(redirectUrl).withLang(Lang("en")))
  }

  def cyGb(redirectUrl: String): Action[AnyContent] = UnauthorisedAction.async { request =>
    Future.successful(Redirect(redirectUrl).withLang(Lang("cy")))
  }

}
