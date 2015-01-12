name := "distrimon"

scalaVersion := "2.11.4"

scalacOptions += "-feature"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-actor" % "2.3.8")
