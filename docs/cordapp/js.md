This is the lambda server, the braid Corda js client, among other items. 

The sample JS lives in here now too but future versions will seek to provide an NPM package to support user clients.

We could declare flow invocation mutations in the schema. More on this in the `Solution` section.
```
type Mutation {
    invokeFlow(flowName: String!, params: Any!): ID! @lambda
}
```