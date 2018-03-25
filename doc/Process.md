== Process and Pool and Case

A package contains a process definition. It describes the process name and version.
The Process definition contains a set of pools. These pools are the base to create
a case.

The process is the meta definition of a package of pools.

The pool is the container for a running process, a process contains multiple pools.

A case is the instance of the pool.

== Process URI

=== Full syntax

The full syntax is used to send requests to the engine.

Default Busines Proces Machine schema:

bpm://user:password@process name:version;options/pool name#startpoint name?key=value&...

Options can be:
* customId=
* customerId=

This syntax is used to start a new process.

BPM Message schema:

bpmm://case id/message name?key=value

BPM Signal schema:

bpms:/signal name?key=value

BPM External event schema:

bpme://node id?key=value

BPM Start additional node (extend running case)

bpmx://case id#node name?key=value

BPM Query schema

bpmq://[case|node|process name:proces version]/...

bpmq://process name:proces version/pool name?query

bpmq://"case"?query

bpmq://"node"/case id?query

=== Limited syntax

If you see the uri of an case or node then the syntax is limited to the basic information

bpm://process name:process version/pool name

And optional 

bpm://process name:process version/pool name#task name

=== Examples

In the most cases you will not need to specify all the uri elements:

A common variant is

bpm://user@process name/pool name

to start a case.
