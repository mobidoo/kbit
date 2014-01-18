name := "kbitcoin"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.google" % "bitcoinj" % "0.10.3",
  "mysql" % "mysql-connector-java" % "5.1.21",
  "com.github.nscala-time" %% "nscala-time" % "0.6.0",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2"
)     

play.Project.playScalaSettings

resolvers ++= Seq(
  "Bitcoinj repository" at "http://distribution.bitcoinj.googlecode.com/git/releases/"
)

resolvers += Resolver.url("Objectify Play Repository", url("http://schaloner.github.io/releases/"))(Resolver.ivyStylePatterns)

resolvers += Resolver.url("Objectify Play Repository - snapshots", url("http://schaloner.github.io/snapshots/"))(Resolver.ivyStylePatterns)

