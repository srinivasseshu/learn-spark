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




