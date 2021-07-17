The types of data stored in Corda based organizations and systems can be put into two boxes

 **On-ledger** : highly structured, “rich”, protected, scrupulous domain data that is operated on and invokes behaviour. Smart contract data e.g states, contracts, e-money.

 **Off-ledger**: often referred to as “anaemic” data. This is static or reference data that is not operated on. e.g configuration data, calendars

There are critiques of this categorization and the debate continues on what should be on or off the ledger. 
From a commercial perspective 

```
more data →  more transactions → more monetization.
```
However, putting more data on the ledger than is necessary may decrease performance, 
limit scaling potential, 
is trickier to evolve, 
and may decrease developer productivity, if you are not careful.
 Regardless of the choice, a centralized, federated knowledge graph, per node, is imperative. 


>> Corda gives us guarentees about data coming from outside the firm. The majority of roles inside the firm do not care where the data came from. They care about data federation. 
