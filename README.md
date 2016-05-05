# fasqlitate
[![Build Status](https://travis-ci.org/davidpeklak/fasqlitate.svg?branch=master)](https://travis-ci.org/davidpeklak/fasqlitate)

A micro-library that fascilitates building java.sql.PreparedStatements in scala.
## Example
```scala
import fasqlitate._
import ConnectionSyntax._
import Default._

val connection: java.sql.Connection
val i = 3
val ps = connection.prepareStatement(sql"select * from FOO where BAR = $i")
```
This has the effect of
```scala
val connection: java.sql.Connection
val i = 3
val ps = connection.prepareStatement("select * from FOO where BAR =  ? ")
ps.setInt(1, i)
```

## Get Started
Add the following to your `build.sbt`
```scala
Resolvers += Resolver.bintrayRepo("dpeklak", "maven")

libraryDependencies += "com.github.davidpeklak" % "fasqlitate_2.10" % "0.1.1"
```
