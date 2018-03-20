
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

process.name.enabled = default process version if not defined
process.name.versions = comma separated list of allowed process versions






