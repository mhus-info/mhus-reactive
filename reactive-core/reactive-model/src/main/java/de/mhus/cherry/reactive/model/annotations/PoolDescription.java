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
package de.mhus.cherry.reactive.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.mhus.cherry.reactive.model.activity.AActor;
import de.mhus.cherry.reactive.model.util.EverybodyActor;

@Retention(RetentionPolicy.RUNTIME)
public @interface PoolDescription {

	String name() default "";
	String description() default "";
	String displayName() default "";
	String[] indexDisplayNames() default {};


	Class<? extends AActor> actorDefault() default EverybodyActor.class;
	Class<? extends AActor>[] actorInitiator() default EverybodyActor.class;
	Class<? extends AActor>[] actorRead() default {};
	Class<? extends AActor>[] actorWrite() default {};

	ExternalSource[] external() default {};
	
}
