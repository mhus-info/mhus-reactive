package de.mhus.cherry.reactive.model.engine;

import java.util.List;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.migrate.Migrator;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.util.MUri;

public interface EngineListener {

	void doFlowNode(PNode ready);

	void setScheduledToRunning(PNode ready);

	void saveCase(PCase caze, APool<?> aPool);

	void closeRuntime(PNode pNode);

	void closeCase(PCase caze, boolean hard);

	void doNodeErrorHandling(ProcessContext<?> context, PNode pNode, Throwable t);

	void saveRuntime(PNode pRuntime, RuntimeNode aRuntime);

	void doFlowNodeScheduled(PNode pNode);

	void error(Object ... objects);

	void closeFlowNode(PNode pNode, STATE_NODE state);

	void startCase(MUri originalUri, MUri uri, IProperties properties, EProcess process, EPool pool,
	        List<EElement> startPoints, MProperties options, String createdBy);

	void createStartPoint(PCase pCase, EElement start);

	void createRuntime(PCase pCase, EElement start, PNode runtime);

	void createStartNode(PCase pCase, EElement start, PNode flow);

	void createActivity(PCase pCase, PNode previous, EElement start, PNode flow);

	void doNodeLifecycle(EElement start, AActivity<?> activity, RuntimeNode runtime, PNode flow, boolean init);

	void saveFlowNode(PNode flow, AActivity<?> activity);

	void archiveCase(PCase caze);

	void doStep(String step);

	void suspendCase(PCase caze);

	void unsuspendCase(PCase caze);

	void cancelFlowNode(PNode node);

	void retryFlowNode(PNode node);

	void migrateCase(PCase caze, Migrator migrator);

	void restoreCase(PCase caze);

}
