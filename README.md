# Learning Scala

## DataTypes

There are no primitive datatypes. Everything is a class/object.

```
1.to(10)
//res3: scala.collection.immutable.Range.Inclusive = Range(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```
- to() is defined in a class RichInt, a special type of Int
- String is augmented by >100 methods in the class StringOps -- str1.intersect(str2)
- BigInt is usable to make use of really big numbers

There are no ++ or -- operators, instead use +=
