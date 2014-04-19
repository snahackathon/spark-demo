import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext

//This is only necessary for Idea syntax highlighting to word
@transient val ctx: SparkContext = sc

//*** Load RDD from file ***//
val lines: RDD[String] = ctx.textFile("./data/part-r-00000")

//Show sample
lines.take(5)

//*** Parse lines of source file ***//
//Function to parse string in the form "{(x1,y1),...,(xn,yn)}"
def parseRelations(rel: String): IndexedSeq[(Int, Int)] = {
  if (rel.startsWith("{(") && rel.endsWith(")}")) {
    val edges = rel.substring(2, rel.length - 2).split("""\),\(""").map(_.split(",")).map(l => (l(0).toInt, l(1).toInt))
    edges.toVector
  } else {
    throw new IllegalArgumentException
  }
}

//Function to parse a string of source file into the person's id & sequence of edges
def parseLine(line: String): (Int, IndexedSeq[(Int, Int)]) = {
  val t = line.trim.split("\t", 2)
  return (t(0).toInt, parseRelations(t(1)))
}

//Try it
val testLine = lines.take(1)(0)
parseLine(testLine)

val personRecords = lines.map(parseLine)
personRecords.take(1)

//*** Extract list of related persons ***//
val t = personRecords.flatMap(record => record._2)
val relatedPersons = t.map(pair => pair._1)
//Try it
relatedPersons.take(10)
//Join with original persons
val allPersons = personRecords.map(pair => pair._1) ++ relatedPersons
allPersons.saveAsTextFile("./data/persons")
allPersons.count

//*** Aggregation: Check if there are common relations ***//
val commonsRDD = relatedPersons.groupBy(id => id).map(pair => (pair._1, pair._2.size)).filter(pair => pair._2 > 1)
commonsRDD.collect.toMap
commonsRDD.count
//The same, but non-distributed
val commons = relatedPersons.countByValue().filter(p => p._2 > 1)
commons.size

//*** Build list of triplets in the form of a class ***//
case class Triplet(from: Int, to: Int, rel: Int) extends Serializable {
  override def toString = s"$from,$to,$rel"
}

def buildTriplets(record: (Int, IndexedSeq[(Int, Int)])) = {
  for (pair <- record._2) yield new Triplet(record._1, pair._1, pair._2)
}

val triplets = personRecords.flatMap(buildTriplets)
//Try it
triplets.take(5)

//Save into text file
//Actually, the RDD is written into hadoop-compatible miltipart file
triplets.saveAsTextFile("./data/triplets")
