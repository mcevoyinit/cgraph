![alt text](resources/images/cgraph.png)

#CGraph - Corda meets the Graph

CGraph is a library that allows you to pipe verified data from Corda into a GraphQL server, over HTTP. 

This means you can write to Corda for safe, secure and provable data but also,
 read via GraphQL for federation, flexibility and uniformity

###Modules

There are 3 notable modules within the library currently

####```cgraph-core```

This is the core lib. Inside that, there are various classes and services. 
This module aims to do as much work as possible for the cgraph user cordapp. It contains:

-`GraphableState` which extends `LinearState` and `QueryableState`. User states must extend this and implement the `buildEntityMap()` function.
 This function returns a property map which cgraph will use to generate mutations for the graph.
  Not all state properties need to be in here except the ones that are needed to be coherent with the state’s corresponding schema entity. 
  This is the translation point for the Corda LDM and the Graph LDM. 
  `GraphableSchema` : `QueryableSchema` is used solely by cgraph under the hood to offer two-phase commits to the graph.

    interface GraphableState : LinearState,  QueryableState {
        /**
         * @return [MapOfMaps] representation of the implementing contract state.
         * This map is used by CGraph to generate mutations in order to write persisted states to the graph db instance.
         * Future enhances may offer more niche annotation support to better track relationships across state model and graph entities.
         */
        fun buildEntityMap(): MapOfMaps

        override fun generateMappedObject(schema: MappedSchema): PersistentState {
            return when (schema) {
                is GraphableSchemaV1 -> GraphableSchemaV1.PersistentGraphable(
                    id = this.buildEntityMap()["id"] as String,
                    graphed = false
                )
                else -> throw IllegalArgumentException("Unrecognised schema $schema")
            }
        }
        override fun supportedSchemas(): Iterable<MappedSchema> = listOf(GraphableSchemaV1)

- `CGraphService` is the heart of cgraph. This service detects new ledger entries of type `GraphableState` and transforms them by passing the result of the `buildEntityMap()` function into the generator.

- `GraphQLMutationGenerator` generates a mutation based on the shape of the provided state property map. More details to be provided here but it can detect based on the presence of UUID type if a nested mutation is needed to write a separate entity.

- `GQLClient` is then used to write the mutation over HTTP to the graph.
 
#### `cgraph-example`

- This model takes CGraph beta “out for a spin” declaring it as a dependency.
- It extends the IOU CorDapp and making it “graphable” by using the SDK.
- You should follow this example to get your own CorDapp up and running.

#### `cgraph-js`

- This is the lambda server, the braid Corda js client, among other items. 
- The sample JS lives in here now also but future versions will seek to provide an NPM package to support user clients.

## Set up your graph
You need to specify the GraphQL server that CGraph will connect to. 
I chose DGraph as a first integration since the GraphQL server and KV store are in one process. However any GraphQL server should work. 
You can set up DGraph locally or remote, for each node.

###Remote
DGraph has a one click deployment free instance that's super easy to get set-up `https://cloud.dgraph.io/` 

 
###Locally 

`Golang binaries` fetch them at https://dgraph.io/downloads

 ```
$GOPATH/bin/dgraph zero
$GOPATH/bin/dgraph alpha --port_offset 1
```
#### Docker

```docker run --rm -it -p "8080:8080" -p "9080:9080" -p "8000:8000" -v ~/dgraph:/dgraph "dgraph/standalone:v21.03.0"```
  
 
Edit the URL and Auth Token in `deployCGraph` gradle task in root `build.gradle`
You can also edit the testing config in `CGraphIOUDriverTesting` to run the sample test.
This is the URL Corda will write to and, the URL the client will read from. 
### Running CGraph

Open a terminal and go to the project root directory and type: (to deploy the 
nodes using bootstrapper)
```
./gradlew clean deployCGraph
./build/nodes/runnodes
```
This should bring up 3 nodes (Lender, Borrower and a Notary)

You then need to connect each Nodejs client to its correpsonding node
```
cd cgraph/cgraph-js/
node.js iou-client.js 3000 8080
// new terminal tab
node.js iou-client.js 3001 8081
```

You can interact with this HTTP API by using the Insomnia HTTP client json script `CGraphInsomnia.json` in the `resources/scripts` folder.

In future versions, I'd like write a more modular architecture for extending and integrating with more graph types, for example:

    cgraph-cordapp 
    cgraph-client
    cgraph-schema-generator 
    cgraph-testing
    cgraph-sample-client
    cgraph-sample-cordapp
    
