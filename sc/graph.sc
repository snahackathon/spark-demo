import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.spark.graphx._

//This is only necessary for Idea syntax highlighting to word
@transient val ctx: SparkContext = sc

//Vertex payload is 0
val persons: RDD[(VertexId, Int)] = ctx.textFile("./data/persons").map(line => (line.toLong, 0))
persons.take(5)

//Edge payload is relation type
val t = ctx.textFile("./data/triplets").map(line => line.split(","))
val edges: RDD[Edge[Int]] = t.map(triplet => Edge(triplet(0).toLong, triplet(1).toLong, triplet(2).toInt))
val graph = Graph(persons, edges, 0)

//Find connected components
val components = graph.connectedComponents
components.vertices.count
