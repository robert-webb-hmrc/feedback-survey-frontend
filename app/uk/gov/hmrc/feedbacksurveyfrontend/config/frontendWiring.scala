/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.feedbacksurveyfrontend

import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.audit.http.config.LoadAuditingConfig
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector}
import uk.gov.hmrc.play.config.{AppName, RunMode, ServicesConfig}
import uk.gov.hmrc.play.frontend.auth.connectors.AuthConnector
import uk.gov.hmrc.play.frontend.filters.SessionCookieCryptoFilter
import uk.gov.hmrc.play.http.ws._
import uk.gov.hmrc.play.partials.FormPartialRetriever


trait AuthenticationConnectors {
  lazy val auditConnector = FrontendAuditConnector
  lazy val authConnector = FrontendAuthConnector
}

trait ControllerConnectors {
  protected def auditConnector: AuditConnector
  protected def authConnector: AuthConnector
}

object FrontendAuditConnector extends AuditConnector with AppName {
  override lazy val auditingConfig = LoadAuditingConfig(s"auditing")
}

object WSHttp extends WSGet with WSPut with WSPost with WSDelete with AppName with HttpAuditing with RunMode  {
  override val auditConnector = FrontendAuditConnector
  override val hooks = Seq(AuditingHook)
}

object FrontendAuthConnector extends AuthConnector with ServicesConfig {
  val serviceUrl = baseUrl("auth")
  lazy val http = WSHttp
}

object LocalPartialRetriever extends FormPartialRetriever {
  override def crypto = SessionCookieCryptoFilter.encrypt
  override val httpGet = WSHttp
}
