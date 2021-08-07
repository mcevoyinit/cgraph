# Future work
 - Formal CAP theorem resiliency analysis.
 - HTTP network hops upper limits, idempotency
 - Comparative study of Graph databases, support more graph technology.
 - DGraph trumps ATM IMO but there are neat JVM parings of GQL/GraphDB as mentioned.
     Lacinia, Crux, Apollo, Relay, etc.=
 - What is manual vs. automated?
 - Support for native Corda Types as Graphable: Token SDK, Account SDK, Party,  etc
 - Concrete typing comparisons on JVM v Go/Graph 
 - UUID v Strings for graph ids, Dates 
 - Add persistence before rawUpdates, queue off back of flow.
 - Introduce raw maps state i.e GraphMapState
 - Shared ownership of “master” schema. Frontend and backend schema segments.
 - Upgrade paths analysis with examples
 - Add metering. Want to avoid deep, taxing user traversal queries.
 - Fully automated “low partition” same machine of Corda and Graph.
 - Go process inside Corda? Crazy talk, probably not!
 - Strategy for UI development with samples. Server first UIs
 - Schema metadata-driven programming
 - Full-text search, analytics, subscriptions
 - Fully-fledged lambda js server in the cloud with braid.js client, with analysis
 
# Code
 
 -  `CGraphReconciler` -> Corda-Graph reconciliation process w/ availability potential
 -  `CGraphSchemaGenerator` -> Schema generation tool for user CorDapp states
 -  `DGraphServiceFactory` -> Start DGraph/Go lang from Corda →
 -  `DGraphGRPCClient` - from JVM to Go Graph process (DQL CorDapp API)

## Contact me

Feel free to raise and MR or contact me directly over at `eric.mcevoy@r3.com`

