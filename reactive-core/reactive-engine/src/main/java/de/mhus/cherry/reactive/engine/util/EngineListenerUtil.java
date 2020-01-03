/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.engine.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.engine.EngineConfiguration;
import de.mhus.cherry.reactive.model.engine.EngineListener;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.RuntimeNode;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.console.Console;
import de.mhus.lib.core.console.Console.COLOR;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.logging.MLogUtil;

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
	
	public static EngineListener createAnsiListener() {
		return (EngineListener) Proxy.newProxyInstance(EngineListener.class.getClassLoader(), new Class[] {EngineListener.class} , new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if (Console.get().isAnsi()) Console.get().setColor(COLOR.MAGENTA, null);
				System.out.println(MSystem.toString(method.getName(), args));
				if (Console.get().isAnsi()) Console.get().cleanup();
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
				if (method.getName().equals("doStep"))
					log.i(method.getName(), args);
				else
					log.i(method.getName(), args);
				return null;
			}
		});
	}

	public static EngineListener createEngineEventProcessor(final EngineConfiguration config) {
		return (EngineListener) Proxy.newProxyInstance(EngineListener.class.getClassLoader(), new Class[] {EngineListener.class} , new InvocationHandler() {
//			Log log = Log.getLog(Engine.class);
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				
				// invoke runtime is necessary
				if (args != null && args.length >= 2 && args[0] != null && args[0] instanceof RuntimeNode && args[1] != null && args[1] instanceof PNode) {
					try {
						((RuntimeNode)args[0]).doEvent(method.getName(), (PNode)args[1], 2, args);
					} catch (Throwable t) {
						MLogUtil.log().e(method,args,t);
					}
				}
				
				// invoke cascaded listeners
				LinkedList<EngineListener> l = config.listener;
				if (l != null)
					for (EngineListener listener : l) {
						try {
							method.invoke(listener, args);
						} catch (Throwable t) {
							MLogUtil.log().t(method,args,t);
						}
					}
				return null;
			}
		});
	}
	
}
