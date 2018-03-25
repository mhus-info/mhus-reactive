
== Engine

* Avoid using PCase too much. The mast process parameters stored in the case. Imagine there are 300 or more parameters they all the time need to be serialized. Loading 2000 or more cases for a request means a lot of de-serializing. Use CaseInfo instead and load the case object only if really needed
* To lock transactions use the caseLock objects. It's a dummy object acting as PCase. In this way it's not needed to load PCase objects all the time. And we have a good overview which cases are locked. (by the way, it's possible to kick a lock). If using locks remember the dead lock problem. Lock in ascending order.
* PCase and PNode are cached. You must use get getCase and getFlowNode methods to load these objects to avoid the existence of parallel instances. In this case data lost could happen (depends the order of saving). Only in special cases the engine is working with copies.

== Activities
* Implement 'IndexValuesProvider' to provider the index values. Return null or in the value null if you don't need to change the values. Return an empty string to reset the value.
 