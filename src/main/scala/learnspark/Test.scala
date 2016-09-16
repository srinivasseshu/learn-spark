package learnspark

import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by srinivaschippada on 4/27/16.
 */
object Test {

  def main(args : Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("WordCount");
//    val conf = new SparkConf().setMaster("local[2]").setAppName("WordCount");

    val sc = new SparkContext(conf)

    //  val input = sc.textFile("README.md")
    val input = sc.textFile("/Users/schipp200/Documents/dev/team-learn/learn-spark/src/main/resources/README.md")
    val words = input.flatMap(line => line.split(" "))
    val counts = words.map(word => (word, 1)).reduceByKey((x, y) => x+y)
//    val counts = words.map(word => (word, 1)).reduceByKey{case(x, y) => x+y}

    counts.saveAsTextFile("/Users/schipp20/Documents/dev/team-learn/learn-spark/src/main/resources/sparktest")

    val data = Array(1,2,3,4,5)
    val distdata = sc.parallelize(data)

    // Calculate average of numbers
    val in = sc.parallelize(List(1,2,3,4))
    val res = in.aggregate((0, 0))(
      (acc, value) => (acc._1 + value, acc._2 + 1),
      (acc1, acc2) => (acc1._1 + acc2._1, acc1._2 + acc2._2))
    val avg = res._1 / res._2.toDouble

    val input1 = sc.parallelize(List(1, 2, 3, 4))
    val result = input1.map(x => x * x)
    println(result.collect().mkString(","))


    sc.stop()
    System.exit(0)

  }



}
