

RExclusiveGateway:

* Decide between outputs using the Output Condition
* Define one Output without Condition to set it as default Output
* Define ACondition(s) to the Output(s) for the condition.
* RExclusiveGateway will follow the Output with the highest result. Or the first with the same result
* For binary decisions use the constants TRUE and FALSE

RParallelGateway:

* Execute all Output(s) parallel


RJoinGateway:

* Wait for all inputs and then execute all Outputs in parallel.
* Waiting for all inputs is not easy. For every waiting input a PNode is created in state WAITING. The last one will
  CLOSE them all and finally start the outputs.
* If multiple threads in the same time using the join gateway this will fail because it's not possible to group the
  incoming threads in logical parts.
