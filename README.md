# Learning Spark

## Introduction of RDDs

RDDs created in two ways:
- by loading an external dataset
  - ```val lines = sc.textFile("/path/to/README.md") ```
- by distributing a collection of objects (list, set, etc)
  - use parallelize() method, not widely used since it requires entire dataset in memory on one machine
  - ```val lines = sc.parallelize(List("pandas", "i like pandas")) ```

To reuse an RDD in multiple actions, you may persist the RDD using 
```
RDD.persist()
```

cache() is the same as persist() with the default storage level.

RDDs offer two kinds of operations:
- Transformations
  - Returns a new RDD, such as map(), filter()
  - Return type: RDD
- Actions
  - Returns result to the driver, or write it to Storage, such as count(), first()
  - Return type: Datatype other than an RDD


### Transformations

Transformation RDDs go through a "Lazy Evaluation", and get computed lazily only when you use them in an action. Although transformations are lazy, you can force Spark to execute them by running an action, such as count()

Spark keeps track of different dependencies between RDDs using a "lineage graph". It uses this information to compute each RDD on demand and to recover lost data if part of a persistent RDD is lost.

take() to retrieve a small number of elements from an RDD
```
badLinesRDD.take(10).foreach(println)
```

collect() to retrieve an entire RDD. 
- Mind that the entire dataset should fit in memory on a single machine to use collect().
- Shouldn't be used on large datasets, rather save a large dataset using saveAsTextFile()


### Passing functions to Spark

- Scala can pass functions defined inline, references to methods, or static functions.
- The function we pass and the data referenced in it needs to be serializable (implementing Java’s Serializable interface).
- Passing a method or field of an object includes a reference (this) to that whole object, instead extract the fields as local variables and avoid passing the whole object containing them
- If NotSerializableException occurs in Scala, a reference to a method or field in a nonserializable class is usually the problem


### Common Transformations And Actions

#### Element-wise Transformations

- map()
  - The return type doesn't have to be the same as the input type
```
val input = sc.parallelize(List(1, 2, 3, 4))
val result = input.map(x => x * x)
println(result.collect().mkString(","))
```

- flatMap()
  -  produce multiple output elements for each input element
```
val lines = sc.parallelize(List("hello world", "hi"))
val words = lines.flatMap(line => line.split(" "))
words.first()  // returns "hello"
```

- sample(withReplacement, fraction, [seed])
  - Sample an RDD with or without replacement
  - rdd.sample(false, 0.5)
  - Result is non-deterministic

#### Pseudo Set Operations

- Require all the RDDs to be of the same data type
- RDD1.distinct()
  - This is an expensive operation, shuffles all data over the network to identify the distinct
- RDD1.union(RDD2)
  - If input RDDs contain duplicates, the union() will contain duplicates
- RDD1.intersection(RDD2)
  - Removes all duplicates (including duplicates from a single RDD)
  - Performance of intersection() is worse than union(), as it requires shuffle to identify common elements
- RDD1.substract(RDD2)
  - Performs shuffle like intersection(), hence expensive

- RDD1.cartesian(RDD2)
  - Both RDDs need not be of the same type
  - Useful to consider similarity between all possible pairs, such as every user's expected interest in each offer
  - Cartesian product of an RDD with itself, useful for tasks like user similarity
  - Very expensive for large RDDs


#### Actions

- reduce()
  - Operates on 2 elements of the type in your RDD, and returns a new element of the same type
```
val sum = rdd.reduce((x, y) => x + y)
```

- fold()
  - fold(zero)(func)
  - Similar to reduce() with the same signature and return type
  - In addition, it takes a "zero value" to be used for initial call on each partition
  - "zero value" provided should be an identity element for your operation, that is applying multiple times with your function should not change the value (e.g., 0 for +, 1 for *, or an empty list for concatenation).
  - **You can minimize object creation in fold() by modifying and returning the first of the two parameters in place. However, you should not modify the second parameter.**
  - Return type is same as that of elements in the RDD. Works well for operations like sum, but not when we return a different type, like computing a running average.
```
rdd.fold(0)((x, y) => x + y)
```

- aggregate()
  - aggregate(zeroValue)(seqOp, combOp) 
  - frees from the constraint of having the return be the same type as the RDD elements
  - need to supply initial "zero value" of the type we want to return, like fold()
  - then, supply a function to combine the elements from our RDD with the **accumulator**
  - then, supply a second function to **merge two accumulators**, given that each node accumulates its own results locally
  - Example, average of an RDD can be computed as
```
val result = input.aggregate((0, 0))(
               (acc, value) => (acc._1 + value, acc._2 + 1),
               (acc1, acc2) => (acc1._1 + acc2._1, acc1._2 + acc2._2))
val avg = result._1 / result._2.toDouble
```

- take(n)
  - Returns n elements from the RDD and attempts to minimize the number of partitions it access
  - May not return the elements in the order you might expect

- top()
  - If there is an ordering defined on the data, we can extract top elements using top()
  - uses default ordering, but can supply our own comparison function to extract top elements

- takeSample(withReplacement, num, seed)
  - take a sample of our data either with or without replacement
  - similar to sample(withReplacement, num, seed), but sample() returns an RDD, takeSample() returns the elements

- foreach()
  - this action performs computation on each element in the RDD without bringing it back locally

- count()
  - returns a count of elements

- countByValue()
  - returns a map of each unique value to its count
  - Number of times each element occurs in the RDD
```
If input RDD has {1, 2, 3, 3}
rdd.countByValue() returns {(1, 1), (2, 1), (3, 2)}
```

- takeOrdered(num)(ordering)
  - Return num elements based on provided ordering
  - rdd.takeOrdered(2)(myOrdering)


#### Converting between RDD types

Some functions are available only on certain types of RDDs.
- mean() and variance() on numeric RDDs
- join() on key/value pair RDDs

In Scala, the conversion to RDDs with special functions (e.g., to expose numeric functions on an RDD[Double]) is handled automatically using implicit conversions.
Need to import below for these conversions to work.
```
import org.apache.spark.SparkContext._ 
```

These implicits turn an RDD into various wrapper classes, such as 
- DoubleRDDFunctions (for RDDs of numeric data) 
- PairRDDFunctions (for key/value pairs), to expose additional functions such as mean() and variance().

Implicits can be confusing. There is no mean() on an RDD, the call manages to succeed because of implicit conversions between RDD[Double] and DoubleRDDFunctions
