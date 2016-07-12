// import statements can be mentioned anywhere in scala
import scala.math._

object LearnScala {

  println("<<<<< Learn Scala >>>>>")

  // Reassigning done through "var" not "val"
  // val:immutable, var:mutable
  var greeting : String = null
  greeting = "hello"

  // Everything is an object in scala
  // Example: to() method is applied to an integer 1
  // to() is defined in RichInt. An Int is automatically converted to RichInt when one of it's methods are needed.
  1.to(10)
  1.to(10).map(sqrt(_))
  // Infix notation: Can ignore the dots, works only on methods with 1 parameter
  1 to 10 map (sqrt(_))
  val num = 1.+(10)

  // ++, -- operators does not exist in Scala

  // Over 100 methods defined in StringOps class
  "hello".intersect("world")

  val x : BigInt = 1234567890
  x * x * x * x * x


  // *************************************************
  // Calling Functions and Methods
  // *************************************************

  // Scala Functions don't operate on objects
  sqrt(3) // A Function (In Java, we would have to use a static method for this)
  BigInt.probablePrime(10, scala.util.Random) // Method

  val b: BigInt = 6 * 7
  b.pow(1000)

  // method - accessor vs mutator
  // () is only required for the mutators
  // accessor - () can be ommitted after the method name
  // mutator - () is required after the method name
  "hello".distinct
  "hello".length

  // Common to use (arg) for methods that are similar to function calls
  "hello"(4)
  "hello".apply(4) // same as "hello"(4), provided by apply() method

  "Rhine".permutations
  val c = "Rhine".permutations.toArray
  c.foreach(println(_))

  "ABC".sum
  "ABC".sum.toInt
  198.toChar
  'A'.toInt

  // *************************************************
  // Control Structures and Functions
  // *************************************************


  val y = 4
  val result1 = if(y > 0) 1 else -1 // Return type is Int
  val result2 = if(y > 0) "something" else -1 // Return type is "Any". Any is an equivalent of "Object" in Java
  val result3 = if(y < 0) "something"         // Returns ()
  // Scala "Unit" is similar to "void" in java, but it has one value denoted ()
  val result4 = ()
  result4 == ()       // returns True
  result4 == Unit     // returns False

  // Missing "else" has a value Unit
  if(y > 0) 1
  // is the same as
  if(y > 0) 1 else ()


  // Value of block is the value of the last statement
  val distance = {
    val x = 3
    val y = 4
    sqrt(x * x + y * y)
  }

  // If the last expression of block is an assignment, the block value is ()
//  while (n > 0) {
//    i+=1
//    n = n / 2
//  }




}
