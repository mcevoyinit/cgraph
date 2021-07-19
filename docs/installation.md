## CorDapp
### Gradle
Add to your Cordapp’s Gradle dependencies:
```groovy
dependencies{
    // CorDapp dependency
    cordaCompile "com.cgraph:cgraph-core:1.0.0"
} 
```   
Alternatively, you might want to fetch CGraph as a Cordapp fat JAR, in which case use compile instead of `cordaCompile` and skip the step below.

Add CGraph as Cordapps to your deployNodes task:

```groovy
cordapp("com.cgraph:cgraph-core:1.0.0")
```

### Config
You need to tell CGraph in CorDapp config where the node's corresponding GraphQL server lies. 
For development, you can configure this in both `DriverDSL` testing and in `Cordformation`.

**Cordformation**
```groovy
node {
        name "O=Lender,L=London,C=GB"
        cordapp(project(':cgraph-core')) {
            config '''
                graphQLUrl="<INSERT_GRAPHQL_SERVER_URL>"
                graphQLToken="<INSERT_GRAPHQL_SERVER_X_AUTH_TOKEN>"
                graphBraidServerPort=8080
             '''
        }
        cordapp(project("cgraph-example")) {
            config '''
                braidServerPort=9090
             '''
        }
       ...
    }
```
or **DriverDSL**
```kotlin
startNode(
    parameters = NodeParameters(
        providedName = DUMMY_LENDER_NAME,
        rpcUsers = listOf(user),
        additionalCordapps = setOf(
            CGraph.Cordapps.Example.withConfig(
                mapOf(
                    "braidServerPort" to 9090
                )
            ),
            CGraph.Cordapps.Core.withConfig(
                mapOf(
                    "graphQLUrl" to DGRAPH_URL_LENDER,
                    "graphQLToken" to DGRAPH_TOKEN_LENDER,
                    "graphBraidServerPort" to 8080
                )
            )
        )
    )
)
```

## Set up your graph
You need to specify the GraphQL server that CGraph will connect to. 
I chose DGraph as a first integration since the GraphQL server and KV store are in one process. However any GraphQL server should work. 
For each node you can either set up DGraph locally or remote.

###Remote
DGraph has a free, one click deployable, instance that's super easy to get set-up over at [DGraph Cloud](https://cloud.dgraph.io). I use two premium (lender, borrower) instances for the demo and test. 
I'll add a version that works with a single instance for developer convenience in the future.

###Locally 

`Golang` set up golang and fetch binaries at [DGraph Downloads](https://dgraph.io/downloads)

```bash
$GOPATH/bin/dgraph zero
$GOPATH/bin/dgraph alpha --port_offset 1 //notary clash
```
### Docker

```bash
docker run --rm -it -p "8080:8080" -p "9080:9080" -p "8000:8000" -v ~/dgraph:/dgraph "dgraph/standalone:v21.03.0"
```
  
 
Edit the URL and Auth Token in `deployCGraph` gradle task in root `build.gradle`
You can also edit the testing config in `CGraphIOUDriverTesting` to run the sample DriverDSL test.
This is the URL Corda will write to and, the URL clients will read from. 

### Running CGraph
Hit a terminal in the project root directory:

```bash
./gradlew clean deployCGraph TODO
./build/nodes/runnodes
```
This brings up 3 nodes (Lender, Borrower and a Notary)

You then need to connect each Nodejs client to its corresponding node
```
cd cgraph/cgraph-js/
node.js iou-client.js 3000 8080
// new terminal tab
node.js iou-client.js 3001 8081
```

- You can interact with this HTTP API by using the Insomnia HTTP client json script `CGraphInsomnia.json` in the `resources/scripts` folder. 
- The order in which you seed the data should go like this: Will provide mutations.
    - `Member` both goes directly into their own and each other's graph.
    - `Currency` written to ledger and then graph via lib. Hit node.js server over http.
    - `Balance` written to ledger. References currency and holder member, from above
    - `IOU` written to ledger. References all of above. IOU in both ledgers and graphs. 

In future versions, there could be a more modular architecture for extending and integrating with more graph technologies, for example:

    cgraph-cordapp 
    cgraph-client
    cgraph-schema-generator 
    cgraph-testing
    cgraph-sample-client
    cgraph-sample-cordapp