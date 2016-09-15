# Learning Spark

## Introduction

RDDs created in two ways:
- by loading an external dataset
- by distributing a collection of objects (list, set, etc)
  - using existing collection using parallelize() method
  - not widely used since it requires entire dataset in memory on one machine
  - ``` val lines = sc.parallelize(List("pandas", "i like pandas")) ```

RDDs offer two kinds of operations:
- Transformations
- Actions

To reuse an RDD in multiple actions, you may persist the RDD using 
```
RDD.persist()
```

cache() is the same as persist() with the default storage level.

