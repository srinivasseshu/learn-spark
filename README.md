# Learning Spark

## RDDs

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


## Transformations

Transformation RDDs go through a "Lazy Evaluation", and get computed lazily only when you use them in an action. Although transformations are lazy, you can force Spark to execute them by running an action, such as count()

Spark keeps track of different dependencies between RDDs using a "lineage graph". It uses this information to compute each RDD on demand and to recover lost data if part of a persistent RDD is lost.

take() to retrieve a small number of elements from an RDD
```
badLinesRDD.take(10).foreach(println)
```

collect() to retrieve an entire RDD. 
- Mind that the entire dataset should fit in memory on a single machine to use collect().
- Shouldn't be used on large datasets, rather save a large dataset using saveAsTextFile()


## Passing functions to Spark

- Scala can pass functions defined inline, references to methods, or static functions.
- The function we pass and the data referenced in it needs to be serializable (implementing Java’s Serializable interface).
- Passing a method or field of an object includes a reference (this) to that whole object, instead extract the fields as local variables and avoid passing the whole object containing them
- If NotSerializableException occurs in Scala, a reference to a method or field in a nonserializable class is usually the problem


## Common Transformations And Actions

### Element-wise Transformations

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

### Pseudo Set Operations

- Require all the RDDs to be of the same data type
- RDD1.distinct()
  - This is an expensive operation, shuffles all data over the network to identify the distinct
- RDD1.union(RDD2)
  - If input RDDs contain duplicates, the union() will contain duplicates
- RDD1.intersection(RDD2)
  - Removes all duplicates (including duplicates from a single RDD)
  - Performance of intersection() is worse than union(), as it requires shuffle to identify common elements
- RDD1.subtract(RDD2)
  - Performs shuffle like intersection(), hence expensive
- RDD1.cartesian(RDD2)
  - Both RDDs need not be of the same type
  - Useful to consider similarity between all possible pairs, such as every user's expected interest in each offer
  - Cartesian product of an RDD with itself, useful for tasks like user similarity
  - Very expensive for large RDDs


## Actions

- reduce()
  - Operates on 2 elements of the type in your RDD, and returns a new element of the same type
```
val sum = rdd.reduce((x, y) => x + y)
```

- fold(zero)(func)
  - Similar to reduce() with the same signature and return type
  - In addition, it takes a "zero value" to be used for initial call on each partition
  - "zero value" provided should be an identity element for your operation, that is applying multiple times with your function should not change the value (e.g., 0 for +, 1 for *, or an empty list for concatenation).
  - **You can minimize object creation in fold() by modifying and returning the first of the two parameters in place. However, you should not modify the second parameter.**
  - Return type is same as that of elements in the RDD. Works well for operations like sum, but not when we return a different type, like computing a running average.
```
rdd.fold(0)((x, y) => x + y)
```

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


## Converting between RDD types

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


## Persistence (Caching)

To avoid computing an RDD multiple times, we can persiste the data in Spark

While persisting, the nodes that compute the RDD store their partitions. If a node that has data persisted on it fails, Spark will recompute the lost partitions of the data when needed. 

Persistence levels:
- MEMORY_ONLY
- MEMORY_ONLY_SER
- MEMORY_AND_DISK
- MEMORY_AND_DISK_SER
- DISK_ONLY

```
val result = input.map(x => x * x)
result.persist(StorageLevel.DISK_ONLY)
println(result.count())
println(result.collect().mkString(","))
```

- Call persist() on the RDD before the first action
- Call unpersist() to manually remove from the cache
- Call cache() to use the default storage level, MEMORY_ONLY

If you attempt to cache too much data to fit in memory, Spark will automatically evict old partitions using a Least Recently Used (LRU) cache policy.


## Transformations on Pair RDDs

Spark lets users control the layout of pair RDDs across nodes using "partitioning". 

Pair RDDs 
- RDDs containing key/value pairs, called tuples
- There are number of ways to get pair RDDs
- Can create by calling SparkContext.parallelize() on a collection of pairs
```
val pairs = lines.map(x => (x.split(" ")(0), x))
```
In Scala, for the functions on keyed data to be available, we also need to return tuples. An implicit conversion on RDDs of tuples exists to provide the additional key/value functions.


```
var l = scala.collection.mutable.ListBuffer[(String, Int)]()
l += (("x", 1))
l += (("x", 1),("x",2))
l += (("y", 1))
val pairs = sc.parallelize(l)
val agg = pairs.reduceByKey((x,y) => x+y)

scala> agg.collect
res8: Array[(String, Int)] = Array((x,4), (y,1))
```

### Transformations on One Pair RDD

For example, assume pair RDD input is: {(1, 2), (3, 4), (3, 6)}
- reduceByKey(func)
  - Combine values with the same key.
```
rdd.reduceByKey((x, y) => x + y)
Result is {(1, 2), (3, 10)}
```

- groupByKey()
  - Group values with the same key.
```
rdd.groupByKey()
Result is {(1, [2]), (3, [4, 6])}
```

- combineByKey(createCombiner, mergeValue, mergeCombiners, partitioner)
  - Combine values with the same key using a different result type

- mapValues(func)
  - Apply a function to each value of a pair RDD without changing the key
```
rdd.mapValues(x => x+1)
Result is {(1, 3), (3, 5), (3, 7)}
```

- flatMapValues(func)
  - Apply a function that returns an iterator to each value of a pair RDD, and for each element returned, produce a key/value entry with the old key. Often used for tokenization.
```
rdd.flatMapValues(x => (x to 5))
Result is {(1, 2), (1, 3), (1, 4), (1, 5), (3, 4), (3, 5)}
```

- keys()
  - Return an RDD of just the keys.
```
rdd.keys()
Result is {1, 3, 3}
```

- values()
  - Return an RDD of just the values.
```
rdd.values()
Result is {2, 4, 6}
```

- sortByKey()
  - Return an RDD sorted by the key.
```
rdd.sortByKey()
Result is {(1, 2), (3, 4), (3, 6)}
```


### Transformations on Two Pair RDDs


For example, assume two pair RDDs input is: (rdd = {(1, 2), (3, 4), (3, 6)} other = {(3, 9)})
- subtractByKey()
  - Remove elements with a key present in the other RDD.
```
rdd.subtractByKey(other)
Result is {(1, 2)}
```

- join()
  - Perform an inner join between two RDDs
```
rdd.join(other)
Result is {(3, (4, 9)), (3, (6, 9))}
```

- rightOuterJoin()
  - Perform a join between two RDDs where the key must be present in the first RDD
```
rdd.rightOuterJoin(other)
Result is {(3,(Some(4),9)), (3,(Some(6),9))}
```

- leftOuterJoin()
  - Perform a join between two RDDs where the key must be present in the other RDD
```
rdd.leftOuterJoin(other)
Result is {(1,(2,None)), (3,(4,Some(9))), (3,(6,Some(9)))}
```

- cogroup()
  - Group data from both RDDs sharing the same key
```
rdd.cogroup(other)
Result is {(1,([2],[])), (3,([4, 6],[9]))}
```


#### Similarities between RDD functions and Pair RDD functions

Pair RDDs are still RDDs, and hence support the same functions as RDDs
```
pairs.filter{case (key, value) => value.length < 20}
```

Also,
- mapValues(func) is the same as map{case (x,y) => (x, func(y)) }
- reduceByKey() is quite similar to reduce()
  - reduceByKey() runs several parallel reduce operations, one for each key in the dataset
- foldByKey() is quite similar to fold()
  -  both use a zero value of the same type of the data in our RDD and combination function


#### Average Example
Average of an RDD can be calculated using map() and fold() functions
```
val input = sc.parallelize(List(1, 2, 3, 4))
scala> val result = input.map(x => (x,1)).fold(0,0)((x,y) => (x._1+y._1,x._2+y._2))
res22: (Int, Int) = (10,4)
val avg = result._1 / result._2.toDouble
```

Similarly, per-key average of a Pair RDD can be calculated using mapValues() and reduceByKey() functions
```
rdd.mapValues(x => (x,1)).reduceByKey((x,y) => (x._1 + y._1, x._2 + y._2))
```

Example for above:
```
key,  value                         key,    value                           key,    value
panda   0                           panda   (0,1)                           panda   (1,2)
pink    3         mapValues()       pink    (3,1)       reduceByKey()       pink    (7,2)
pirate  3       -------------->     pirate  (3,1)     ----------------->    pirate  (3,1)
panda   1                           panda   (1,1)
pink    4                           pink    (4,1)
```

reduceByKey() and foldByKey() will automatically perform combining locally on each machine before computing global totals for each key. The user does not need to specify a combiner. The more general combineByKey() interface allows you to customize combining behavior.

#### Word Count Example

Can be implemented as:
```
val input = sc.textFile("s3://...")
val words = input.flatMap(x => x.split(" "))
val result = words.map(x => (x, 1)).reduceByKey((x, y) => x + y)
```
Also, this can be done faster by:
```
input.flatMap(x => x.split(" ")).countByValue()
```

### Transformations on Two Pair RDDs

- combineByKey()
  - Most general of the per-key aggregation functions
  - Most of the other per-key combiners are implemented using it
  - Like aggregate(), combineByKey() need not return the same type as the input data
  - As combineByKey() goes through the elements in a partition, each element either has a key it hasn’t seen before or has the same key as a previous element.
    - If it’s a new element, combineByKey() uses a function we provide, called **createCombiner()**, to create the initial value for the accumulator on that key. This happens the first time a key is found in each partition, rather than only the first time the key is found in the RDD.
    - If it is a key we have seen before while processing that partition, it will instead use the provided function, **mergeValue()**, with the current value for the accumulator for that key and the new value.
  - Since each partition is processed independently, we can have multiple accumulators for the same key. When merging the results from all partitions, if two or more partitions have an accumulator for the same key, we merge the accumulators using the user-supplied **mergeCombiners()** function.







