object LearnScala {

  println("<<<<< Learn Scala >>>>>")

  // *************************************************
  // Declare Classes, Instance Variables, and methods
  // *************************************************

  // Below class, Point is an immutable classs - can't change the "val" fields
  // Mutable classes - Use var for mutable instance variables

  // Below line includes a "primary constructor"
  class Point(val x: Double = 0, val y: Double = 0) {

    // Not only constructors initialize instance variables, but also executes
    // any arbitrary code in class body as part of the constructor
    // Below line is printed whenever a new Point is constructed
    println(f"Welcome to (${x}, ${y})")

    // Auxiliary Constructor, called as a new Point()
    // Below auxiliary constructor could be eliminated by using default args, like
    // class Point(val x: Double = 0, val y: Double = 0) {
    def this() { this(0, 0) }

    // Methods
    def move(dx: Double, dy:Double) = new Point(x + dx, y + dy)
    def distanceFromOrigin = Math.sqrt(x * x + y * y)

    def *(factor: Double) = new Point(x * factor, y * factor)

    // Fills the value of the expression "x/y" into the string
    // Works with printf style formatters: f"${x}%8.2f"
    override def toString = f"(${x}, ${y})"

  }

  // Calling the primary constructor
  val p = new Point(3, 4)

  // Calling the auxiliary constructor
  val q = new Point()
  val r = new Point

  p.move(10, 20)

  // No () for parameter-less accessor methods
  p.distanceFromOrigin

  p.x
  p.y

  // Prefer immutable classes - sharing is safe, esp. in concurrent programs

  // Can also have instance variables declared: private var or vals
  class BankAccount(private var balance : Double)


  // There is no way to distinguish a method without parameter from the
  // instance variable, as they have the same sytnax.
  // Example from above:
  // p.x                    = instance variable
  // p.distanceFromOrigin   = method without parameter

  // "Uniform Access" means the class user doesn't know or care. You can start defining
  // a class one way, and later change the definition as required.
  // The implementor can switch between fields and methods without changing the interface.
  // No need to fear public instance variables, like in Java, as if you ever change
  // your mind to update your class definition, everyone else using the class should change.


  // Infix Notation
  // x op y is the same as x.op(y)
  1 to 10 map (3 * _) filter (_ % 5 == 2)

  p.*(2)
  p * 2 




}
