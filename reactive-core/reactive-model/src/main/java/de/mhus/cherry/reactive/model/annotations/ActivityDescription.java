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

import de.mhus.cherry.reactive.model.activity.ASwimlane;
import de.mhus.cherry.reactive.model.engine.RuntimeNode;
import de.mhus.cherry.reactive.model.util.DefaultSwimlane;

@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityDescription {

	public Output[] outputs() default {};
	public Trigger[] triggers() default {};
	public Class<? extends ASwimlane<?>> lane() default DefaultSwimlane.class;
	
	String description() default "";
	String displayName() default "";
	String[] indexDisplayNames() default {};
	
	String caption() default "";
	public Class<? extends RuntimeNode> runtime() default RuntimeNode.class;
	public String name() default "";
	String event() default "";
		
}
