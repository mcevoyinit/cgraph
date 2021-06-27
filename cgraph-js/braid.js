const Proxy = require('@cordite/braid-client').Proxy;

let braid = new Proxy({
    url: "http://localhost:8080/api", credentials: {
        username: 'sa', password: 'admin'
    }
}, onOpen, onClose, onError, {strictSSL: false});

function onOpen() {}

function getNotaries() {
    return braid.network.notaryIdentities()
        .then(notaries => {
            console.log('Notaries', notaries);
            notary = notaries[0];
        })
}

function onClose() {
    console.log("disconnected");
}

function onError(err) {
    console.error("error", err);
}

self.addGraphQLResolvers({
    "Mutation.invokeFlow": invokeFlow,
});