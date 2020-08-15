package de.mhus.cherry.reactive.engine.util;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.logging.ITracer;
import io.opentracing.References;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;

public class CaseLock extends MLog implements Closeable {

    protected Scope scope;
    private String operation;
    private Object[] tagPairs;
    private boolean scopeOwner;
    private boolean master;

    public CaseLock(boolean master, String operation, Object... tagPairs) {
        this.operation = operation;
        this.tagPairs = tagPairs;
        this.master = master;
    }

    public void startSpan(PCase caze) {
        if (operation == null) {
            scopeOwner = false;
            scope = ITracer.get().tracer().scopeManager().active();
            return;
        }
        if (master) {
            scopeOwner = true;
            scope = spanStart(caze, operation, tagPairs);
        } else {
            scopeOwner = true;
            scope = ITracer.get().enter(operation, tagPairs);
        }
    }

    public static Scope spanStart(PCase caze, String operation, Object... tagPairs) {
        SpanContext ctx =
                ITracer.get()
                        .tracer()
                        .extract(
                                Format.Builtin.TEXT_MAP,
                                new TextMap() {

                                    @Override
                                    public Iterator<Entry<String, String>> iterator() {
                                        HashMap<String, String> out = new HashMap<>();
                                        caze.getParameters()
                                                .forEach(
                                                        (k, v) -> {
                                                            if (k.startsWith("__tracer."))
                                                                out.put(
                                                                        k.substring(9),
                                                                        v.toString());
                                                        });
                                        return out.entrySet().iterator();
                                    }

                                    @Override
                                    public void put(String key, String value) {}
                                });

        Scope scope = null;
        if (ctx == null) scope = ITracer.get().tracer().buildSpan(operation).startActive(true);
        else {
            scope =
                    ITracer.get()
                            .tracer()
                            .buildSpan(operation)
                            .addReference(References.FOLLOWS_FROM, ctx)
                            .startActive(true);
        }
        return scope;
    }

    @Override
    public void close() {
        if (scope != null && scopeOwner) {
            scope.close();
            scope.span().finish();
        }
        scope = null;
    }

    public Span getSpan() {
        return scope.span();
    }
}
