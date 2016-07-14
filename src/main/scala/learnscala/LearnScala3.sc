object LearnScala {

  println("<<<<< Learn Scala >>>>>")

  // *************************************************
  // Arrays & ArrayBuffers
  // *************************************************

  // Below, all values initialized to 0
  val nums = new Array[Int](10)
  for(i <- 0 until nums.length) nums(i) = i * i
  nums

  // Use Array without "new" to specify initial values
  // They type is inferred
  val a = Array("hello", "world")

  // Use parantheses to access the elements
  a(0) = "goodbye"

  // Traverses array
  for(element <- a) println(element)

  // Traverses array indexes
  for(i <- 0 until a.length) println(a(i))

  // Scala arrays map to the Java arrays in the JVM
  // Scala Array[Int] is converted to Java int[], to offer performance

  // "ArrayBuffer" as analog to Java's ArrayList
  // "ArrayBuffer" is a variable size array
  import scala.collection.mutable.ArrayBuffer
  val b = new ArrayBuffer[Int]

  // Use += to add at the end of ArrayBuffer
  b += 1
  b += (1, 2, 3, 4)
  b ++= Array(5)

  val s = ArrayBuffer("Mary", "had", "a", "little", "lamb")
  s += "its"
  s += ("fleece", "was", "white")
  s ++= Array("as", "snow")
  s

  s.remove(3)
  s

  s.insert(3, "large", "hefty")
  s.trimEnd(6)
  s

  // Conversion between arrays and buffers
  val x = s.toArray
  val y = x.toBuffer

  // Easy to transform an array/arraybuffer/collection with "for/yield",
  // the result is a new array/arraybuffer/collection with transformed values
  val z = Array(2, 3, 5, 7, 11)
  var result = for(elem <- z) yield elem * 2
  result = for(elem <- z if elem % 2 != 0) yield elem * 2

  // Common computations are easy
  Array(1, 7, 2, 9).sum
  ArrayBuffer("Mary", "had", "a", "little", "lamb").max
  ArrayBuffer(1, 7, 2, 9).sorted
  Array(1, 7, 2, 9).reverse
  Array(1, 7, 2, 9).sorted.reverse

  Array(1,2,3).toString
  Array(1,2,3).mkString("|")
  Array(1,2,3).mkString("[", "|", "]")


  // *************************************************
  // Maps
  // *************************************************

  // By default Map is immutable,
  var scores = Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)

  val mscores = scala.collection.mutable.Map("Alice" -> 10)

  // Map is a collection of pairs
  // The -> is an operator that constructs pair
  "Alice" -> 10

  val bobsScore = scores("Bob")

  // Throws exception on looking up non-existing key, hence use "getOrElse" method
  // scores("Fred")

  scores.getOrElse("Bob", -1)
  scores.getOrElse("Fred", -1)

  mscores("Bob") = 20
  mscores("Bob")

  // You can append or remove keys
  mscores += ("Bob" -> 13, "Fred" -> 7)
  mscores -= "Alice"
  mscores -= ("Alice", "David")

  // Scala encourages immutable Maps although it creates a new Map everytime,
  // as they share most of the elements, there is structural sharing happening on the elements
  // behind the scenes. The advantage of immutability comes when you've concurrent programs
  // or multiple threads that operate on the same Map, the mutation is dangerous or expensive
  // as you've to lock the Map for each concurrent operation. When immutable datastructures are
  // used, these issues doesn't arise at all.

  // With immutable maps, use + or - and you get a new Map always
  // So, use a "var" for immutable map, make operations and reassign to the same variable
  scores
  scores = scores + ("David" -> 5)
  scores = scores + ("Srini" -> 5)
  scores = scores - "Alice"


  for((k,v) <- scores)
    println(k + " has a value " + v)

  for((k,v) <- scores) yield (v,k)

  scores.keySet
  scores.values

  scores.mkString("|")
  scores.toString

  // *************************************************
  // Tuples
  // *************************************************

  // Tuples collect values of different types, whereas arrays collects values of the same type
  // Tuple always has a fixed size

  val t = (5, 3.14, "Fred")

  // Tuple position starts with 1.
  t._1
  t._2
  t._3

  // Pattern matching, "_" means I don't care, I don't want to capture that variable
  val (_, second, third) = t

  // *************************************************
  // Exercise 1 - Removing all but the first negative number
  // *************************************************

  val input = ArrayBuffer(1, 2, -3, 4, -5, 6, -7, 8)

  def removeAllButFirstNegative(buf: ArrayBuffer[Int]) = {
    val indexes = for(i <- 0 until buf.length if(buf(i) < 0)) yield i
    val indexesToRemove = indexes.drop(1)

    //As  the indexes are removed, it updates the indexes immediately within the array
    //So, reverse the indexes and remove from the reverse
    for(i <- indexesToRemove.reverse) buf.remove(i)
  }
  removeAllButFirstNegative(input)
  input

  val input1 = ArrayBuffer(1, 2, -3, 4, -5, 6, -7, 8)
  def removeAllButFirstNegative2(buf: ArrayBuffer[Int]) = {
    val indexesToRemove = (for(i <- 0 until buf.length if(buf(i) < 0)) yield i).drop(1)
    for(i <- 0 until buf.length if(!indexesToRemove.contains(i))) yield buf(i)
  }
  removeAllButFirstNegative2(input1)

  // *************************************************
  // Exercise 2 - Word Count
  // *************************************************

  val in = new java.util.Scanner(new java.net.URL(
    "http://www.tothenew.com/blog/wp-content/uploads/2015/01/Hello-World.txt").openStream)

  // Using Mutable Map
  val count = scala.collection.mutable.Map[String, Int]()
  while(in.hasNext) {
    val word = in.next()
    count(word) = count.getOrElse(word, 0) + 1
  }
  count.toString()

  val inp = new java.util.Scanner(new java.net.URL(
    "http://www.tothenew.com/blog/wp-content/uploads/2015/01/Hello-World.txt").openStream)

  // Using Immutable Map
  var immutable_count = scala.collection.immutable.Map[String, Int]()
  while(inp.hasNext) {
    val word = inp.next()
    immutable_count += word -> (immutable_count.getOrElse(word, 0) + 1)
  }
  immutable_count.toString()

  // *************************************************
  // Exercise 3 - Grouping
  // *************************************************

  val words = Array("Mary", "had", "a", "little", "lamb", "its", "fleece", "was", "white",
  "as", "snow", "and", "everywhere", "that", "Mary", "went", "the", "lamb", "was",
  "sure", "to", "go")

  val res = words.groupBy(_.substring(0,1))
  for((k,v) <- res) {
    println(k + " " + v.mkString("|"))
  }

  val result1 = words.groupBy(_.length)
  for((k,v) <- result1) {
    println(k + " " + v.mkString("|"))
  }

  // *************************************************
  // Exercise 4 - Partitions & Zips
  // *************************************************

  // "partition" - partitions a collection into 2 collections based on the predicate condition
  "New York".partition(_.isUpper)

  val input2 = ArrayBuffer(1, 2, -3, 4, -5, 6, -7, 8)

  def removeAllButFirstNegative3(buf: ArrayBuffer[Int]) = {
    val (positive, negative) = input2.partition(_ > 0)
    positive += negative(0)
  }
  removeAllButFirstNegative3(input2)

  // "zip" method - zips two collections of the same length into a collection of tuples
  val symbols = Array("<", "-", ">")
  val counts = Array(2, 10, 2)
  val pairs = symbols.zip(counts)

  for((s,n) <- pairs) print(s * n)



}
