package de.mhus.cherry.reactive.engine.util;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.logging.ITracer;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;

public class CaseLock extends MLog implements Closeable {

	protected Scope scope;
	private String operation;
	private Object[] tagPairs;

	public CaseLock(String operation, Object ... tagPairs) {
		this.operation = operation;
		this.tagPairs = tagPairs;
	}
	
	public void startSpan(PCase caze) {
        scope = spanStart(caze, operation, tagPairs);
	}

    public static Scope spanStart(PCase caze, String operation, Object ... tagPairs ) {
		SpanContext ctx = ITracer.get().tracer().extract(Format.Builtin.TEXT_MAP, new TextMap() {

			@Override
			public Iterator<Entry<String, String>> iterator() {
				HashMap<String,String> out = new HashMap<>();
				caze.getParameters().forEach((k,v) -> {if (k.startsWith("__trace.")) out.put(k.substring(8), v.toString()); } );
				return out.entrySet().iterator();
			}

			@Override
			public void put(String key, String value) {
			}
			
		});
		
		
		Scope scope = null;
		if (ctx == null) 
			scope = ITracer.get().start(operation, null, tagPairs);
		else {
			scope = ITracer.get().tracer().buildSpan(operation).asChildOf(ctx).startActive(true);
		}
		return scope;
	}

	@Override
	public void close() {
		if (scope != null)
			scope.close();
		scope = null;
	}
	
	
}
