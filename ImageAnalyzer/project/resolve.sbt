logLevel := Level.Warn

lazy val root = (project in file("."))

resolvers ++= Seq(
  "Maven Repository" at " https://repo1.maven.org/maven2"
)
