DGraph is the graph technology that interests me the most. 

Although the CGraph Library will be architected in future to support many graph databases, 
I chose DGraph as a first case study run. 
DGraph is my personal favourite graph database. 
The main reasons for the decision will become clear:

DGraph - “Distributed Graph Database” is a GraphQL native distributed graph database that is written from the disk to the top-level query interface by DGraph labs in California.
 
>>DGraph is a curve jumper. It does distributed transactions, low-latency arbitrary depth joins, traversals, provides synchronous replication and horizontal scalability — with a simple GraphQL-like API -  [DGraph Labs](https://www.dgraph.io)

###Graph Native
DGraph is unique in that it has a GraphQL server natively embedded in it, alongside its graph DB / KV store. This is interesting because:

Typically the GraphQL server lives separately from the underlying datastore, as illustrated below. 
Having an all-in-one service greatly speeds up development effort, lowers maintenance cost, and solution complexity, which is the primary reason DGraph is CGraphs first integration.

![alt text](../resources/images/GQLNative.png){height=500px width=500px}

Furthermore, this removes the need for writing and maintaining a GraphQL server, resolvers, and middlewares. 
Maintaining a separate graph server and database processes is heavy-duty!

Another example of this pairing is:

 - Lacinia - GraphQL server written in Clojure by Walmart Labs Lacinia - GraphQL for Clojure
 - Crux - Bitemporal Graph DB written in Clojure. Crux  

Pairings like the above will considered in future version. They may be better suited to larger Corda deployments.
The creators of Crux have written a Crux-Corda Connector CorDapp juxt/crux-corda  
