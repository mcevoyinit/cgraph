 
## Reading
 - In one approach, we replicate the data in real-time out of Corda, with a toleration threshold of consistency, into a graph database. 
 - We then provide a unified GraphQL API for both reading and writing to the system. 
 - We take advantage of the rich capabilities the latest tools offer to accelerate towards a more complete Corda Business API. 
 - We provide consistent access to the graph across the whole system i.e UIs, end-user consoles, peripheral services and inside CorDapp flows (via CGraph Corda Service).
 
## Modelling
 
 - We model all domain data as a graph. 
 - We represent states data as entities, model their relationships inline within a programmable GraphQL specification compliant schema. 
 - We index, employ rich annotation systems, and provide authorisation down to an attribute level. 
 - We enable schema composability where different actors in the firm own segments of a federated master schema e.g front end, backend, (CRM, ERM?) 
 
## Productivity

 - We enable frontend, backend, test, and data engineers to work in parallel against a single unified data contract DSL, the schema.
 - We avoid writing and maintaining CRUD APIs. Better yet, we generate CRUD APIs and even UI components based on the shape of your domain data, which is described by metadata in the schema (naked UI). 
 - We then leave room for custom logic and extension. 
 
 The following sections attempt to expand on these concepts further. 