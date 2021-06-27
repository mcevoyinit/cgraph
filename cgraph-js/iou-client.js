"use strict";

const express = require('express')
const Proxy = require('@cordite/braid-client').Proxy;
const cors = require('cors');
const bodyParser = require('body-parser');
const app = express()

const expressPort = process.argv[2];
const braidPort = process.argv[3];

app.use(cors());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.listen(expressPort, () => console.log('Express Node.js Server listening on port '+expressPort))

// Connects to Braid running on the node.
let braid = new Proxy({
    url: "http://localhost:"+braidPort+"/api", credentials: {
        username: 'sa', password: 'admin'
    }
}, onOpen, onClose, onError, {strictSSL: false});

// HTTP end-points.
app.post('/issue-currency', (req, res) => {
    const name = req.body.name
    const isoCode = req.body.isoCode
    const observerName = req.body.observerX500Name
    issueCurrencyFlow(name, isoCode, observerName)
        .then(result => res.send("Transaction Success: "+result))
        .catch(err => res.status(500).send(err));
});

app.post('/issue-balance', (req, res) => {
    const isoCode = req.body.isoCode
    const value = req.body.value
    issueBalanceFlow(isoCode, value)
        .then(result => res.send("Transaction Success: "+result))
        .catch(err => res.status(500).send(err));
});

app.post('/issue-iou', (req, res) => {
    const iouValue = req.body.iouValue
    const currencyName = req.body.currencyName
    const borrowerX500Name = req.body.borrowerX500Name
    issueIOUFlow(iouValue, currencyName, borrowerX500Name)
        .then(result => res.send("Transaction Success: "+result))
        .catch(err => res.status(500).send(err));
});

app.post('/post-graphql', (req, res) => {
    const graphql = req.body.graphql
    invokePostGraphQLFlow(graphql)
        .then(result => res.send("Post GraphQL Result: "+result))
        .catch(err => res.status(500).send(err));
});

//Braid client Corda Flow Functions
function issueCurrencyFlow(name, isoCode, observerName) {
    return braid.iouService.issueCurrencyFlow(name,isoCode, observerName)
}

function issueBalanceFlow(isoCode, value) {
    return braid.iouService.issueBalanceFlow(isoCode, value)
}

function issueIOUFlow(iouValue, currencyName, borrowerX500Name ) {
    return braid.iouService.issueCurrencyFlow(iouValue, currencyName, borrowerX500Name)
}

function invokePostGraphQLFlow(graphql) {
    return braid.iouService.postGraphQLFlow(graphql)
}

function invokeDropDataFlow(braid) {
    return braid.iouService.dropDataFlow()
}

function getNotaries() {
    return braid.network.notaryIdentities()
        .then(notaries => {
            console.log('Notaries', notaries);
        })
}

function onOpen() {
    console.log('Connected to nodes braid server on port '+braidPort);
}
function onClose() { console.log('Disconnected from node.'); }
function onError(err) { console.error(err); process.exit(); }


