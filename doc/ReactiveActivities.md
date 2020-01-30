

RExclusiveGateway:

* Decide between outputs using the Output Condition
* Define one Output without Condition to set it as default Output
* Define ACondition(s) to the Output(s) for the condition.
* RExclusiveGateway will follow the Output with the highest result. Or the first with the same result
* AConditions returning values lesser then zero will be ignored, maybe the default condition will be used
* For binary decisions use the constants TRUE and FALSE

RParallelGateway:

* Execute all Output(s) parallel


RJoinGateway:

* Wait for all inputs and then execute all Outputs in parallel.
* Waiting for all inputs is not easy. For every waiting input a PNode is created in state WAITING. The last one will
  CLOSE them all and finally start the outputs.
* If multiple threads in the same time using the join gateway this will fail because it's not possible to group the
  incoming threads in logical parts.

  
RTask/RServiceTask/RUserTask/... Output:
* Return null or DEFAULT_OUTPUT to execute the default output (without specified name)
* Return RETRY to execute doExecute again. Attention: Set the tryCount() in PNode, by default only ONE execution is allowed

TimerTrigger:

* Timer works only for activities in state WAITING

Event Triggers (Message, Signal, External):

* Trigger works NOT for SUSPENDED activities, it will not be queued
* The type of the activity must be the same as the event
* The send message will be stored in the new created node in PNode.getMessage()

RMessageEven:

* Wait for a message and then executes the default output
* Define the name of the message in ActivityDescription.event
* A message can be consumed only by ONE activity or trigger
* The send message will be stored in the PNode.getMessage()

RSignalEven:

* Wait for a signal and then executes the default output
* Define the name of the message in ActivityDescription.event
* A signal will be consumed by ALL activity and triggers waiting for it
* The send message will be stored in the PNode.getMessage()

RExternalEven:

* Wait for an external event and then executes the default output
* Define the name of the message in ActivityDescription.event
* A external event will be consumed by a defined (nodeId) node waiting for it
* The send message will be stored in the PNode.getMessage()

AProcess:

* Define this ONE time in the project or package to define the process name/display name/description and version
* Also migrator classes can be defined do transform older versions of the process to the current structure

RPool:

* A pool defines a executable process. All elements of the process must be bind to the pool. Exactly must have a template
definition of the pool. The element itself can't have a template definition. e.g. 'public class MyTask extends RTask<MyPool>'
binds the Task 'MyTask' to the poll 'MyPool'.

RTerminate:

* Will stop the hole case. You can set a exitCode (closeCode) and exitMessage (closeMessage)

REndPoint:

* Use it to start a runtime thread
* Every start point of the pool will be executed if a case will be started
* Mark a start point with 'InactiveStartPoint' to prevent automatic starting on case creation

Restricted Areas - REnterRestrictedArea and RLeaveRestrictedArea

This implements a singleton which can be entered by single cases/runtimes. To implement it start the restricted are with a
REnterRestrictedArea node. Set the name of the area in the @Description::event. Internal the restricted area is handled like 
a message event. To leave the area implement the RLeaveRestrictedArea. You also have to define the same @Description::event or 
if you leave empty all areas will be released.

Inside the both notes only one case will be active. This is also possible over several processes (the area is a engine wide lock).
You can administrate is with the 'pengine areas' commands.

Be aware of dead locks if you lock more then one area at once (https://en.wikipedia.org/wiki/Dining_philosophers_problem). This 
will not be handled by the engine.
