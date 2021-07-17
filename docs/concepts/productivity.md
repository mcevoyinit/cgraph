From a Corda solutions engineering perspective, we must consider the productivity of various actors in the Corda software engineering life cycle in order to increase the likelihood of project success. 
From a Corda perspective, we typically see an extended 4 tier business programming architecture. 
Many community thought leaders have sought to automate this to improve the lives of CorDapp developers. 
We saw this succinctly summarised in a blog post by Bond 180 Corda integration options in 2020. 

## Classic tier qualms

This paradigm frequently comes at a high cost.

- Even though it’s Corda, the pattern we all use is more or less CRUD, which can be tedious for accessing simple data points. 
CRUD is clunky and requires special effort to give me the data I exactly want in a query.  

- Often I just want to display what’s in the database on my screen. 
- Why do I need to write, deploy a maintain intermediate, “middle man” relay layers? 
- Could the web service be left for custom business logic? 
- Why does the database have to be in the bottom layer? 

- We certainly don’t want to stick DSL SQL in our client.  It’s not within frontend vernacular to talk to  Postgres/MySQL/Vault Queries.

- I am constantly writing modifying and maintaining CRUD layers yet making changes in this 4 tier setup is troublesome. 

- Stressful for backend team be “one sprint ahead” of other roles e.g front end, test, data engineers.

- Backend changes often lead to cascading breakages on other teams components.

- UIs break, web-server endpoints suddenly don’t work, packages, like RPC clients, need to be recompiled. There’s way more to it. 

- These teams need a common data contract of reference. 

```
The question every new developer asks themselves can finally be raised again; why can’t my view layer talk directly to my data layer? 

For that there would need to be an API in the database. 
```


## Schema driven programming
- DGraph flips the 3/4 classic tiered model around which could yield high productivity gains.

- Once we define the data model via the schema, DGraph can consume this and generate the CRUD API for us.  This CRUD API is adjusted as the schema is adjusted. Front end and backend developers get to go to work straight away, in parallel against a common contract of reference. This alone is a key differentiator of DGraph.

- We move the DB from layer 3 to layer 2. One thing this means is we need a DSL that is native to browser/clients. This is where GraphQL comes in. GraphQL is the future DSL of the internet that is easy to work with and use in the browser/mobile environment. It enables us to talk to DB directly. 

- It gets even better. From the client, we don’t have to worry about knowing whether to talk to the DB or the webserver. 
Custom GraphQL queries and mutations, also defined on the schema, can inform the database server when to call them versus queries when to talk directly to the DB. 
DGraph lambdas provide Nodejs (JVM support coming this summer) to call externally. 
This is where we can start Corda flows over JSON RPC. The sample uses Cordite Braid at the moment but will integrate with Corda 5 embedded server. 
I expand on this in the solution architecture section but keep the “new” layer 3 in mind.

