package com.cgraph.core.services;

/**
 * Class provides the bridge between the DGraph Go Process {@see DGraphService}
 */
class DGraphServiceFactory {

    /**
     * Starts the DGraph graph database in docker container beside the JVM. Standard port 8080
     */
    fun start(port: Int, method: DGraphInstanceMethod){
        try {
            if(method == DGraphInstanceMethod.DOCKER) {
                Runtime.getRuntime().exec("docker run -it -p "+port+":"+port+" dgraph/standalone:master");
            } else {
                Runtime.getRuntime().exec("\$GOPATH/bin/dgraph zero")
                Thread.sleep(20)
                Runtime.getRuntime().exec("\$GOPATH/bin/dgraph alpha --port_offset 2")
            }
            System.out.println("DGraph has been started on port "+port);
        } catch (e: Exception) {
            println("Failed to start DGraph "+e.toString());
        }
    }
}

enum class DGraphInstanceMethod { DOCKER, GOLANG }

fun main(args: Array<String>) {
    print("Start DGraph")
    DGraphServiceFactory().start(8080, DGraphInstanceMethod.GOLANG)
    print("DGraph started")
}
