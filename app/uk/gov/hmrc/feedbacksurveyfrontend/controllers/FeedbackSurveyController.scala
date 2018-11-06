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

import models.feedbackSurveyModels._
import play.api.Play
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.feedbacksurveyfrontend.services.OriginService
import uk.gov.hmrc.feedbacksurveyfrontend.views.html
import uk.gov.hmrc.feedbacksurveyfrontend.{FrontendAppConfig, LocalTemplateRenderer}
import uk.gov.hmrc.play.binders.Origin
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.renderer.TemplateRenderer
import utils.LoggingUtils


object FeedbackSurveyController extends FeedbackSurveyController {
  val originService = new OriginService
}

trait FeedbackSurveyController extends FrontendController with LoggingUtils with I18nSupport {

  implicit val messagesApi: MessagesApi = Play.current.injector.instanceOf[MessagesApi]
  implicit val templateRenderer: TemplateRenderer = LocalTemplateRenderer

  def originService: OriginService

  def survey(origin: String, taxAccount: Option[String]): Action[AnyContent] = Action { implicit request =>
    Ok(html.feedbackSurvey.survey(formMappings.surveyForm, origin, taxAccount))
  }

  def surveySubmit(origin: String): Action[Survey] = Action (parse.form(formMappings.surveyForm)) { implicit request =>
    val whatWasTheMainService = request.body.mainService.getOrElse("")
    val whatWasTheMainServiceOther = request.body.mainServiceOther.getOrElse("")
    val mainThing = request.body.mainThing.getOrElse("")
    val ableToDoWhatNeeded = request.body.ableToDoWhatNeeded.getOrElse("")
    val howEasyWasIt = request.body.howEasyWasIt.getOrElse("")
    val whyDidYouGiveThisScore = request.body.whyDidYouGiveThisScore.getOrElse("")
    val howDidYouFeel = request.body.howDidYouFeel.getOrElse("")

    var splunkElements = Map.empty[String, String]

    if (origin.nonEmpty)
      splunkElements += "origin" -> origin
    if (whatWasTheMainService.nonEmpty)
      splunkElements += "whatWasTheMainService" -> whatWasTheMainService
    if (whatWasTheMainServiceOther.nonEmpty)
      splunkElements += "whatWasTheMainServiceOther" -> whatWasTheMainServiceOther
    if (mainThing.nonEmpty)
      splunkElements += "mainThing" -> mainThing
    if (ableToDoWhatNeeded.nonEmpty)
      splunkElements += "ableToDoWhatNeeded" -> ableToDoWhatNeeded
    if (howEasyWasIt.nonEmpty)
      splunkElements += "howEasyWasIt" -> howEasyWasIt
    if (whyDidYouGiveThisScore.nonEmpty)
      splunkElements += "whyDidYouGiveThisScore" -> whyDidYouGiveThisScore
    if (howDidYouFeel.nonEmpty)
      splunkElements += "howDidYouFeel" -> howDidYouFeel

    audit(transactionName = "feedback-survey", detail = splunkElements, eventType = eventTypeSuccess)

    originService.customFeedbackUrl(Origin(origin)) match {
      case Some(x) => Redirect(x)
      case None => Redirect(routes.FeedbackSurveyController.thankYou(origin))
    }
  }

  def thankYou(origin: String): Action[AnyContent] = Action {
    implicit request =>
      if(originService.isValid(Origin(origin))) {
        Ok(html.feedbackSurvey.thankYou(FrontendAppConfig.urLinkUrl, origin))
      } else {
        Ok(html.error_template("global_errors.title", "global_errors.heading", "global_errors.message"))
      }
  }
}
