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

package views.feedbackSurvey

import controllers.FeedbackSurveyController
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.feedbacksurveyfrontend.FrontendAppConfig
import uk.gov.hmrc.feedbacksurveyfrontend.services.{OriginConfigItem, OriginService}
import uk.gov.hmrc.feedbacksurveyfrontend.utils.MockTemplateRenderer
import uk.gov.hmrc.renderer.TemplateRenderer
import utils.{HtmlUtils, UnitTestTraits}

class pageTests extends UnitTestTraits with HtmlUtils {
  val lookupFailure = Json.parse(input = """{"reason": "Generic test reason"}""")

  def testRequest(page: String): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, "/feedback-survey/" + s"$page")

  object TestLookupController extends FeedbackSurveyController {

    override implicit val templateRenderer: TemplateRenderer = MockTemplateRenderer

    val originService = new OriginService {
      override lazy val originConfigItems = List(
        OriginConfigItem(Some("VALID_ORIGIN"), None, None)
      )
    }
  }

  "FeedbackSurvey Controller" should {

    "render survey page correctly" in {

      val document: Document = TestLookupController.survey(origin = "VALID_ORIGIN", taxAccount= None)(testRequest(page = "survey"))

      document.title shouldBe
        s"${Messages("give_feedback")} - ${Messages("gov_uk")}"

      document.getElementById("introduction").text shouldBe
        Messages("mainService.we_use_your_feedback_to_improve_our_services_")
      document.getElementById("privacyPolicy").html() shouldBe
        Messages("mainService.see_the_hmrc_privacy_notice_", FrontendAppConfig.hmrcPrivacyNoticeUrl)
      document.getElementById("information").text shouldBe
        Messages("mainService.the_survey_takes_about_1_minute_to_complete_5")

      document.getElementById("ableToDoWhatNeeded").text should
        include(Messages("ableToDo.were_you_able_to_do_what_you_needed_to_do_today"))
      document.getElementById("ableToDoWhatNeededLegend").text should
        include(Messages("ableToDo.were_you_able_to_do_what_you_needed_to_do_today"))
      document.getElementById("ableToDoWhatNeededYes").siblingElements().text should include(Messages("generic.yes"))
      document.getElementById("ableToDoWhatNeededYes").text shouldBe ""
      document.getElementById("ableToDoWhatNeededNo").siblingElements().text should include(Messages("generic.no"))
      document.getElementById("ableToDoWhatNeededNo").text shouldBe ""

      document.getElementById("howEasywasIt").text should
        include(Messages("howEasyWasIt.how_easy_was_it_for_you_to_"))
      document.getElementById("howEasyWasItLegend").text should
        include(Messages("howEasyWasIt.how_easy_was_it_for_you_to_"))
      document.getElementById("howEasyWasIt5").siblingElements().text should include("5")
      document.getElementById("howEasyWasIt4").siblingElements().text should include("4")
      document.getElementById("howEasyWasIt3").siblingElements().text should include("3")
      document.getElementById("howEasyWasIt2").siblingElements().text should include("2")
      document.getElementById("howEasyWasIt1").siblingElements().text should include("1")

      document.getElementById("whyDidYouGiveThisScore").text should
        include(Messages("howEasyWasIt.why_did_you_give_this_score"))
      document.getElementById("whyDidYouGiveThisScoreLegend").text should
        include(Messages("howEasyWasIt.why_did_you_give_this_score"))

      document.getElementById("howDidYouFeelLegend").text should
        include(Messages("howDidYouFeel.overall_how_did_you_feel_about_the_service_you_received_today"))

      document.getElementById("howDidYouFeel5").siblingElements().text should include("5")
      document.getElementById("howDidYouFeel4").siblingElements().text should include("4")
      document.getElementById("howDidYouFeel3").siblingElements().text should include("3")
      document.getElementById("howDidYouFeel2").siblingElements().text should include("2")
      document.getElementById("howDidYouFeel1").siblingElements().text should include("1")
    }

    "render PTA survey page correctly" in {

      val document: Document = TestLookupController.survey(origin = "VALID_ORIGIN", taxAccount = None)(testRequest(page = "survey"))

      document.getElementById("mainThing").text shouldBe
        Messages("mainThing.what_was_the_main_thing_you_needed_to_do_")
      document.getElementById("mainThingLegend").text should
        include(Messages("mainThing.what_was_the_main_thing_you_needed_to_do_"))
    }

    "render BTA survey page correctly" in {

      val document: Document = TestLookupController.survey(origin = "VALID_ORIGIN", taxAccount = "BTA")(testRequest(page = "survey"))

      document.getElementById("mainService").text shouldBe
        Messages("mainService.what_was_the_main_service_you_used_today")
      document.getElementById("mainServiceLegend").text should
        include(Messages("mainService.what_was_the_main_service_you_used_today"))

      document.getElementById("mainServiceSelfAssessment").siblingElements().text should
        include(Messages("mainService.self_assessment"))
      document.getElementById("mainServicePayeForEmployers").siblingElements().text should
        include(Messages("mainService.PAYE_for_employers"))
      document.getElementById("mainServiceVat").siblingElements().text should
        include(Messages("mainService.vat"))
      document.getElementById("mainServiceCorporationTax").siblingElements().text should
        include(Messages("mainService.corporation_tax"))
      document.getElementById("mainServiceCIS").siblingElements().text should
        include(Messages("mainService.construction_industry_scheme_cis"))
      document.getElementById("mainServiceEcSales").siblingElements().text should
        include(Messages("mainService.ec_sales"))
      document.getElementById("mainServiceOther").siblingElements().text should
        include(Messages("mainService.other_please_specify"))
    }

    "render thankYou page correctly with valid origin" in {
      val document: Document = TestLookupController.thankYou("VALID_ORIGIN").apply(testRequest(page = "thankYou"))
      document.getElementById("thankYou").text shouldBe Messages("thankYou.thank_you_for_your_feedback")
    }

    "render error page correctly with invalid origin" in {
      val document: Document = TestLookupController.thankYou("INVALID_ORIGIN").apply(testRequest(page = "thankYou"))
      document.body.getElementsByClass("heading-large").text should include("Service unavailable")
    }
  }

  "render ableToDo page correctly" in {
    val document: Document = TestLookupController.ableToDo("VALID_ORIGIN")(testRequest(page = "ableToDo"))
    document.getElementById("intro").text shouldBe Messages("feedbackSurvey.page1.para1")
    document.getElementById("gdpr").text shouldBe Messages("feedbackSurvey.page1.para2")
    document.getElementById("ableToDoWhatNeeded_legend").text should include(Messages("feedbackSurvey.page1.question1"))
    document.getElementById("ableToDoWhatNeeded-yes").text shouldBe ""
    document.getElementById("ableToDoWhatNeeded-no").text shouldBe ""
  }

  "render usingService page correctly" in {
    val document: Document = TestLookupController.usingService("VALID_ORIGIN")(testRequest(page = "usingService"))
    document.getElementById("beforeUsingThisService").text shouldBe Messages("feedbackSurvey.page2.question1")
  }

  "render aboutService page correctly" in {
    val document: Document = TestLookupController.aboutService("VALID_ORIGIN")(testRequest(page = "aboutService"))
    document.getElementById("serviceReceived").text shouldBe Messages("feedbackSurvey.page3.question1")
  }

  "render recommendService page correctly" in {
    val document: Document = TestLookupController.recommendService("VALID_ORIGIN")(testRequest(page = "recommendService"))
    document.getElementById("reasonForRatingHeader").text shouldBe Messages("feedbackSurvey.page4.question2")
  }

  "render thankYou page correctly with valid origin" in {
    val document: Document = TestLookupController.thankYou("VALID_ORIGIN").apply(testRequest(page = "thankYou"))
    document.getElementById("thankYou").text shouldBe Messages("feedbackSurvey.page5.title")
  }

  "render error page correctly with invalid origin" in {
    val document: Document = TestLookupController.thankYou("INVALID_ORIGIN").apply(testRequest(page = "thankYou"))
    document.body.getElementsByClass("heading-large").text should include("Service unavailable")
  }
}
