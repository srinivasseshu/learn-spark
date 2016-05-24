package util

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
 * Created by srinivaschippada on 5/3/16.
 */
class SearchFunctions(val query: String) {

  def isMatch(s: String): Boolean = {
    s.contains(query)
  }

//  def getMatchesFunctionReference(rdd: RDD[String]): RDD[String] = {
  def getMatchesFunctionReference(rdd: RDD[String]): RDD[Boolean] = {
    // Problem: "isMatch" means "this.isMatch", so we pass all of "this"
    rdd.map(isMatch)
  }

  def getMatchesFieldReference(rdd: RDD[String]): RDD[String] = {
    // Problem: "query" means "this.query", so we pass all of "this"
//    rdd.map(x => x.split(query))
    rdd.flatMap(x => x.split(query))
  }

  def getMatchesNoReference(rdd: RDD[String]): RDD[String] = {
    // Safe: extract just the field we need into a local variable
    val query_ = this.query
//    rdd.map(x => x.split(query_))
    rdd.flatMap(x => x.split(query_))
  }


}
