import sbt._

object FrontendBuild extends Build with MicroService {
  val appName = "feedback-survey-frontend"
  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object AppDependencies {
    import play.core.PlayVersion
    import play.sbt.PlayImport._

    val compile = Seq(
      ws,
      "uk.gov.hmrc" %% "frontend-bootstrap" % "10.7.0",
      "uk.gov.hmrc" %% "play-partials" % "6.1.0",
      "uk.gov.hmrc" %% "http-caching-client" % "7.2.0",
      "uk.gov.hmrc" %% "play-language" % "3.4.0",
      "uk.gov.hmrc" %% "local-template-renderer" % "2.0.0",
      "uk.gov.hmrc" %% "play-ui" % "7.17.0"
    )

    trait TestDependencies {
      lazy val scope: String = "test"
      lazy val test: Seq[ModuleID] = ???
    }

    object Test {
      def apply() = new TestDependencies {
        override lazy val test = Seq(
          "uk.gov.hmrc" %% "hmrctest" % "3.2.0" % scope,
          "org.scalatest" %% "scalatest" % "3.0.0" % scope,
          "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % scope,
          "org.pegdown" % "pegdown" % "1.6.0" % scope,
          "org.jsoup" % "jsoup" % "1.10.2" % scope,
          "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
          "org.mockito" % "mockito-all" % "2.0.2-beta" % scope
        )
      }.test
    }

  def apply() = compile ++ Test()
}
