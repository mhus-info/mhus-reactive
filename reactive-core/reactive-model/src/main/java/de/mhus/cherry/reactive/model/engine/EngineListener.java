package de.mhus.cherry.reactive.model.engine;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.util.MUri;

/*
 * Events will automatically forwarded to runtime nodes if
 * the first argument is RuntimeNode and the second is PNode.
 * 
 */
public interface EngineListener {

	void doFlowNode(PNode ready);

	void setScheduledToRunning(PNode ready);

	void saveCase(PCase caze, APool<?> aPool);

	void closeRuntime(PNode pNode);

	void closeCase(PCase caze, boolean hard);

	void doNodeErrorHandling(ProcessContext<?> context, PNode pNode, Throwable t);

	// Don't change !!!
	void saveRuntime(PNode pRuntime, RuntimeNode aRuntime);

	void doFlowNodeScheduled(PNode pNode);

	void error(Object ... objects);

	void closeFlowNode(PNode pNode, STATE_NODE state);

	void startCase(MUri originalUri, MUri uri, IProperties properties, EProcess process, EPool pool,
	        List<EElement> startPoints, MProperties options, String createdBy);

	void createStartPoint(PCase pCase, EElement start);

	void createRuntime(PCase pCase, EElement start, PNode runtime);

	// Don't change !!!
	void createStartNode(RuntimeNode runtime, PNode flow, PCase pCase, EElement start);

	// Don't change !!!
	void createActivity(RuntimeNode runtimeNode, PNode flow, PCase pCase, PNode previous, EElement start);

	// Don't change !!!
	void executeStart(RuntimeNode runtime, PNode flow, EElement start, AActivity<?> activity);

	void saveFlowNode(PNode flow, AActivity<?> activity);

	void archiveCase(PCase caze);

	void doStep(String step);

	void suspendCase(PCase caze);

	void unsuspendCase(PCase caze);

	void cancelFlowNode(PNode node);

	void retryFlowNode(PNode node);

	void migrateCase(PCase caze, String uri, String migrator);

	void restoreCase(PCase caze);

	void fireMessage(UUID caseId, String message, Map<String, Object> parameters);

	void fireExternal(UUID nodeId, Map<String, Object> parameters);

	void fireSignal(String signal, Map<String, Object> parameters);

	void setScheduledToWaiting(PNode node);

	// Don't change !!!
	void executeFailed(RuntimeNode runtime, PNode flow);

	// Don't change !!!
	void executeStop(RuntimeNode runtime, PNode flow);

	// Don't change !!!
	void closedActivity(RuntimeNode aRuntime, PNode flow);

	// Don't change !!!
	void initStart(RuntimeNode runtime, PNode flow, EElement start, AActivity<?> activity);

	void initFailed(RuntimeNode runtime, PNode flow);

	void initStop(RuntimeNode runtime, PNode flow);

}
