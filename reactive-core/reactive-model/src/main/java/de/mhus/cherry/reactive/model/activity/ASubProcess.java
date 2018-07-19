package de.mhus.cherry.reactive.model.activity;

import de.mhus.cherry.reactive.model.util.CloseActivity;

public interface ASubProcess<P extends APool<?>> extends ATask<P>, CloseActivity {

}
