scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
//  "com.github.nscala-time" %% "nscala-time" % "0.6.0",
//  "com.google.code.findbugs" % "jsr305" % "2.0.1",
  "org.apache.hadoop" % "hadoop-client" % "2.2.0" excludeAll(
    ExclusionRule(organization = "org.codehaus.jackson"),
    ExclusionRule(organization = "org.jboss.netty"),
    ExclusionRule(organization = "asm")
    ),
  "org.apache.spark" %% "spark-core" % "0.9.0-incubating",
  "org.apache.spark" %% "spark-graphx" % "0.9.0-incubating"
)
