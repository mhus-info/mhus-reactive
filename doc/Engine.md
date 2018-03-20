
== Class-Model

EElement (E* classes)
- Subclasses: EProcess, EPool, ECase
- E stands for Engine
- provided from process provider to describe and provide a model AElement
- Used only in Engine/EngineContext

AElement (A* interfaces)
- Interfaces to describe the process model
- Sub interfaces: ATask, AGateway, APoint ...
- A stands for Activity
- Description of the process model
- Provided by the developer to implement the process. Use R* classes from reactive-util 

R* classes
- Extends and utilize A* interfaces
- Engine is only working with A* interfaces
- Implements the behavior of model elements

P* classes
- PCase, PNode (FlowNode), PEngine
- P stands for Persistent
- These are the classes they are persisted with StorageProviders



== Parameters

process:<name>:active = default process version if not defined
process"<name>:versions = comma separated list of allowed process versions
engine.execute.parallel=true or false, can activities be executed in parallel 
engine.execute.max.threads=integer, maximum parallel threads. Expect long running threads they will be unplugged

== PCase State
- NEW: Just created, will be set to RUNNING after creation of start points
- RUNNING: Nodes will be executed
- SUSPENDED: Suspended by the user
- CLOSED: Closed with or without error, if closeCode is less 0 the case is failed

== PNode State
- NEW: Just created and not initialized (should not be saved in storage)
- RUNNING: Will be executed
- SCHEDULED: Waiting for the scheduled time
- WAITING: Waiting for something, e.g. message or signal
- FAILED: The execution of the activity failed
- SUSPENDED: Suspended by the user
- STOPPED: Stopped by the engine because of internal problems (e.g. storage errors)
- CLOSED: Successful executed
- ZOMBIE: The activity is waiting but the case can be closed even



