/**
 * This file is part of cherry-reactive.
 *
 *     cherry-reactive is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     cherry-reactive is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cherry-reactive.  If not, see <http://www.gnu.org/licenses/>.
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
