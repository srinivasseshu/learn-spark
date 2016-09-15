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

Transformation RDDs go through a "Lazy Evaluation", and get computed lazily only when you use them in an action.

Spark keeps track of different dependencies between RDDs using a "lineage graph". It uses this information to compute each RDD on demand and to recover lost data if part of a persistent RDD is lost.

take() to retrieve a small number of elements from an RDD
```
badLinesRDD.take(10).foreach(println)
```

collect() to retrieve an entire RDD. 
- Mind that the entire dataset should fit in memory on a single machine to use collect().
- Shouldn't be used on large datasets, rather save a large dataset using saveAsTextFile()





