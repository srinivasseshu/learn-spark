package learnspark.util

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
 * Created by srinivaschippada on 5/3/16.
 */

// Passing Functions to Spark

// Scala can pass functions defined inline, references to methods, or static functions.
// The function we pass and the data referenced in it needs to be serializable (implementing Java’s Serializable interface).

// Passing a method or field of an object includes a reference (this) to that whole object, instead extract the fields as local variables and avoid passing the whole object containing them

class SearchFunctions(val query: String) {

  def isMatch(s: String): Boolean = {
    s.contains(query)
  }

  def getMatchesFunctionReference(rdd: RDD[String]): RDD[Boolean] = {
    // Problem: "isMatch" means "this.isMatch", so we pass all of "this"
    rdd.map(isMatch)
  }

  def getMatchesFieldReference(rdd: RDD[String]): RDD[String] = {
    // Problem: "query" means "this.query", so we pass all of "this"
    rdd.flatMap(x => x.split(query))
  }

  def getMatchesNoReference(rdd: RDD[String]): RDD[String] = {
    // Safe: extract just the field we need into a local variable
    val query_ = this.query
    rdd.flatMap(x => x.split(query_))
  }


}
