package de.mhus.cherry.reactive.engine;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import de.mhus.cherry.reactive.model.engine.EngineListener;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.logging.Log;

public class EngineListenerUtil {

	public static EngineListener createStdErrListener() {
		return (EngineListener) Proxy.newProxyInstance(EngineListener.class.getClassLoader(), new Class[] {EngineListener.class} , new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				System.err.println(MSystem.toString(method.getName(), args));
				return null;
			}
		});
	}

	public static EngineListener createStdOutListener() {
		return (EngineListener) Proxy.newProxyInstance(EngineListener.class.getClassLoader(), new Class[] {EngineListener.class} , new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				System.out.println(MSystem.toString(method.getName(), args));
				return null;
			}
		});
	}
	
	public static EngineListener createQuietListener() {
		return (EngineListener) Proxy.newProxyInstance(EngineListener.class.getClassLoader(), new Class[] {EngineListener.class} , new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return null;
			}
		});
	}

	public static EngineListener createLogDebugListener() {
		return (EngineListener) Proxy.newProxyInstance(EngineListener.class.getClassLoader(), new Class[] {EngineListener.class} , new InvocationHandler() {
			Log log = Log.getLog(Engine.class);
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				log.d(method.getName(), args);
				return null;
			}
		});
	}

	public static EngineListener createLogInfoListener() {
		return (EngineListener) Proxy.newProxyInstance(EngineListener.class.getClassLoader(), new Class[] {EngineListener.class} , new InvocationHandler() {
			Log log = Log.getLog(Engine.class);
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				log.i(method.getName(), args);
				return null;
			}
		});
	}
	
}
