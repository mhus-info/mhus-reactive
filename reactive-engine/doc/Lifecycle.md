== Case Lifecycle:
- start the new case
- constructor
- setContext()
- initializeCase() / checkInputParameters()
- exportParamters() - save
- destroy

Loop:
- constructor
- setContext()
- importParameters()
- do task
- exportParamters()
- destroy

Ending:
- constructor
- setContext()
- importParameters()
- closeCase()
- exportParamters()
- destroy
- archive case

FlowNode Lifecycle:
- start or retry new task
- constructor
- setContext()
- initializeTask()
- doExecute()
- exportParamters()
- destroy

Loop (if needed):
- constructor
- setContext()
- importParameters()
- doExecute()
- exportParamters()
- destroy

 
 