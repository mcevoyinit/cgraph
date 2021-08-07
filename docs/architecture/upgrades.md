It's on my list to analyse upgrades paths for Graphable CorDapps.

### Rough Notes

It appears app upgrades on the Corda side can remain largely the same (*maybe* easier) when done with proper checks and balances.
 The absence of a `QueryableState` means we can replace jars and ensure logical and technical backwards compatibility with regression testing, etc. 
 
 For data type changes DGraph will maintain both the old and new versions in memory to avoid failures on historical, pre-upgrade, data.


**Q**: How are you handling version updates of DGraph? How do you handle breaking changes?

**A**: If they are API breaking changes, we’ll give the user plenty of notice and work with them to upgrade them to the new version — this might require code changes at their end, so we have to be more careful.

If there’re no API changes, but underlying data format changes, then we’d upgrade the user automatically based on the downtime slots the user chooses. 
Downtime for us means moving the existing backend to “read-only” for 15-30 mins, and upgrading them.

If there is no underlying data changes, then we can just do a rolling upgrade, with no noticeable impact on HA clusters (but perhaps a couple of mins of downtime for non-HA clusters).
