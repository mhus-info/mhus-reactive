package de.mhus.cherry.reactive.model.activity;

import de.mhus.cherry.reactive.model.util.CloseActivity;

/**
 * Interface to start other processes or methods as sub process.
 * @author mikehummel
 *
 * @param <P>
 */
public interface ASubProcess<P extends APool<?>> extends ATask<P>, CloseActivity {

}
