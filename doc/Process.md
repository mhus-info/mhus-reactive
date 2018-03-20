== Process and Pool and Case

A package contains a process definition. It describes the process name and version.
The Process definition contains a set of pools. These pools are the base to create
a case.

The process is the meta definition of a package of pools.

The pool is the container for a running process, a process contains multiple pools.

A case is the instance of the pool.

== Process URI

Full syntax:

reactive://user:password@process name:version/pool name#startpoint name?key=value&...

In the most cases you will not need to specify all the uri elements:

A common variant is

reactive://user@process name/pool name

to start a case.
