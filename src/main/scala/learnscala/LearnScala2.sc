object LearnScala {

  println("<<<<< Learn Scala >>>>>")

  // *************************************************
  // Loops & Control Structures
  // *************************************************

  // No val/var before i is required
  val n = 10
  for(i <- 1 to n) println(i)
  for(c <- "Hello") println(c)

  // Multiple "generators" can be used as well
  // for loop using the variables i & j, they're called as "generators"
  for(i <- 1 to 3; j <- 1 to 3) println(10 * i + j)

  // "Guards" = if conditions within the for loop
  for(i <- 1 to 3; j <- 1 to 3  if(i!=j) ) println(10 * i + j)

  // Collecting results, "yield" feature
  // "yield" returns all the values as a collection
  val result = for(i <- 1 to 10) yield i % 3


  // *************************************************
  // Functions
  // *************************************************

  // Methods are defined in Classes, Functions can be defined anywhere.
  // Takes the parameters that you put into the function declaration

  // Return type is assigned automatically
  def abs(x : Double) = if(x >= 0) x else -x

  // Return type is inferred unless the function is recursive
  // For a recursive function, you must specify a return type, for all others it's optional
  def fact(n : Int): Int = if(n <= 0) 1 else n * fact(n-1)
  fact(4)

  // If you omit the =, the function doesn't return a value
  // This is a "procedure"
  def box(s : String) {
    val border = "-" * s.length + "--\n"
    println(border + "|" + s + "|\n" + border)
  }
  box("hello")

  // *************************************************
  // named, default and variable function arguments
  // *************************************************

  // "named" parameters can be used to specify the parameters in anyorder,
  // and Scala will figure out to set those parameters automatically

  // "default" arguments let you omit argument values
  def decorate(str: String, left: String = "[", right: String = "]") =
    left + str + right

  decorate("Hello")
  decorate("Hello", ">>>[")
  decorate("Hello", right = "]<<<")

  // Variable number of arguments indicated with * after type
  def sum(args: Int*) = {     // args is a Seq[Int]
    var result = 0
    for(arg <- args) result += arg
    result
  }

  sum(10, 20, 30, 40)

  // If you already have an argument Seq[Int], need decoration to pass it
  val s = sum(1 to 5: _*)   // _* means a Sequence of something

  // _* is needed in a recursive call
  def recursiveSum(args : Int*): Int = {
    if(args.length == 0) return 0
    else  args.head + recursiveSum(args.tail : _*)
  }


  // *************************************************
  // Exercise
  // *************************************************

//  def isVowel(ch : Char) = ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u'
  def isVowel(ch : Char) = "aeiou".contains(ch)

  isVowel('x')
  isVowel('o')

  def vowels(s : String) = {
    var result = ""
    for(ch <- s) {
      if(isVowel(ch)) result += ch
    }
    result
  }
  vowels("hello")

  def vowels2(s : String) = {
    for(ch <- s if(isVowel(ch))) yield ch
  }
  vowels2("Nicaragua")

  def vowels3(s : String) = {
    var result = ""
    var i = 0
    while(i < s.length) {
      val ch = s(i)
      if(isVowel(s(i))) result += ch
      i += 1
    }
    result
  }
  vowels3("Nicaragua")

  def recursiveVowels(s : String): String = {
    if(s.length == 0) ""
    else {
      if (isVowel(s.head))
          s.head + recursiveVowels(s.tail)
      else
          recursiveVowels(s.tail)
    }
  }
  recursiveVowels("Nicaragua")

  def vowels4(s : String, vowelChars : String = "aeiou", ignoreCase : Boolean = true): String = {
    for(ch <- (if(ignoreCase) s.toLowerCase else s) if(vowelChars.contains(ch))) yield ch
  }
  vowels4("August", ignoreCase = true)
  vowels4("August", ignoreCase = false)


}
