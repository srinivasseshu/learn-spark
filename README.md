# Learning Spark

## Introduction

RDDs created in two ways:
- by loading an external dataset
- by distributing a collection of objects (list, set, etc)

RDDs offer two kinds of operations:
- Transformations
- Actions

To reuse an RDD in multiple actions, you may persist the RDD using 
```
RDD.persist()
```
