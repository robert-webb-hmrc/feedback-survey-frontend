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

package models.feedbackSurveyModels

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json


case class Survey(mainService: Option[String],
                  mainServiceOther: Option[String],
                  mainThing: Option[String],
                  ableToDoWhatNeeded: Option[String],
                  howEasyWasIt: Option[String],
                  whyDidYouGiveThisScore: Option[String],
                  howDidYouFeel: Option[String])

object Survey {
  implicit val format = Json.format[Survey]
}

case class AbleToDo(ableToDoWhatNeeded: Option[String])

object AbleToDo {
  implicit val format = Json.format[AbleToDo]
}

case class UsingService(beforeUsingThisService: List[String])

object UsingService {
  implicit val format = Json.format[UsingService]
}

case class AboutService(serviceReceived: Option[String])

object AboutService {
  implicit val format = Json.format[AboutService]
}

case class RecommendService(recommendRating: Option[String],
                            reasonForRating: Option[String])

object RecommendService {
  implicit val format = Json.format[RecommendService]
}

object formMappings {

  val surveyForm = Form(mapping(
    "mainService" -> optional(text),
    "mainServiceOther" -> optional(text),
    "mainThing" -> optional(text),
    "ableToDoWhatNeeded" -> optional(text),
    "howEasyWasIt" -> optional(text),
    "whyDidYouGiveThisScore" -> optional(text),
    "howDidYouFeel" -> optional(text)
  )(Survey.apply)(Survey.unapply))

  val ableToDoForm = Form(mapping(
    "ableToDoWhatNeeded" -> optional(text))(AbleToDo.apply)(AbleToDo.unapply))

  val usingServiceForm = Form(mapping(
    "beforeUsingThisService" -> list(text.verifying("required field", _.nonEmpty))
  )(UsingService.apply)(UsingService.unapply))

  val aboutServiceForm = Form(mapping(
    "serviceReceived" -> optional(text.verifying("required field", _.nonEmpty)))(AboutService.apply)(AboutService.unapply))

  val recommendServiceForm = Form(mapping(
    "recommendRating" -> optional(text.verifying("required field", _.nonEmpty)),
    "reasonForRating" -> optional(text.verifying("required field", _.nonEmpty)))(RecommendService.apply)(RecommendService.unapply))

  def validInputCharacters(field: String, regXValue: String) = {
    if (field.matches(regXValue)) true else false
  }
}

object fieldValidationPatterns {
  def addresssRegx = """^[A-Za-zÀ-ÿ0-9 &'(),-./]{0,}$"""
  def yesNoRegPattern = "^([1-2]{1})$"
}
