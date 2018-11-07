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

import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.feedbacksurveyfrontend.services.{OriginConfigItem, OriginService}
import uk.gov.hmrc.feedbacksurveyfrontend.utils.MockTemplateRenderer
import uk.gov.hmrc.play.binders.Origin
import uk.gov.hmrc.renderer.TemplateRenderer
import utils.UnitTestTraits


class HomeControllerTest extends UnitTestTraits {

  "HomeController start" should {

    def buildFakeHomeController: HomeController = new HomeController{

      override implicit val templateRenderer: TemplateRenderer = MockTemplateRenderer

      val originService = new OriginService {
        override lazy val originConfigItems = List(
          OriginConfigItem(Some("TOKEN1"), None, None),
          OriginConfigItem(Some("TOKEN2"), None, "BTA"),
          OriginConfigItem(Some("TOKEN3"), None, "OTHER"),
          OriginConfigItem(Some("TOKEN4"), None, "OLDSURVEY")
        )
      }
    }

    "give a status of OK, return error page if origin token not found" in {
      val controllerUnderTest = buildFakeHomeController
      val result = controllerUnderTest.start(Origin("INVALIDORIGIN")).apply(FakeRequest("GET", ""))
      status(result) shouldBe OK
      contentAsString(result) should include("Service unavailable")
    }

    "give a status of OK, if origin token found and does not have BTA taxAccount" in {
      val controllerUnderTest = buildFakeHomeController
      val result = controllerUnderTest.start(Origin("TOKEN1")).apply(FakeRequest("GET", ""))
      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some("/feedback-survey/TOKEN1/")
    }

    "redirect to start when origin is valid and has BTA taxAccount" in {
      val controllerUnderTest = buildFakeHomeController
      val result = controllerUnderTest.start(Origin("TOKEN2")).apply(FakeRequest("GET", ""))
      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some("/feedback-survey/TOKEN2/?taxAccount=BTA")
    }

    "redirect to start when origin is valid and has OTHER taxAccount" in {
      val controllerUnderTest = buildFakeHomeController
      val result = controllerUnderTest.start(Origin("TOKEN3")).apply(FakeRequest("GET", ""))
      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some("/feedback-survey/TOKEN3/?taxAccount=OTHER")
    }

    "redirect to abletoDo when origin is valid and has OLDSURVEY taxAccount" in {
      val controllerUnderTest = buildFakeHomeController
      val result = controllerUnderTest.start(Origin("TOKEN4")).apply(FakeRequest("GET", ""))
      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some("/feedback-survey/ableToDo/TOKEN4")
    }
  }
}
