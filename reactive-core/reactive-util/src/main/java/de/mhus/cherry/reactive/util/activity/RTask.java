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
package de.mhus.cherry.reactive.util.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.mhus.cherry.reactive.model.util.ActivityUtil;
import de.mhus.lib.core.pojo.PojoAttribute;
import de.mhus.lib.core.pojo.PojoModel;

public abstract class RTask<P extends RPool<?>> extends RAbstractTask<P> {

	private PojoModel pojoModel;

	@Override
	public Map<String, Object> exportParamters() {
		HashMap<String,Object> out = new HashMap<>();
		for( PojoAttribute<?> attr : getPojoModel()) {
			try {
				Object value = attr.get(this);
				if (value != null)
					out.put(attr.getName(), value);
			} catch (IOException e) {
				log().d(attr,e);
			}
		}
		return out;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void importParameters(Map<String, Object> parameters) {
		for(PojoAttribute attr : getPojoModel()) {
			try {
				Object value = parameters.get(attr.getName());
				if (value != null)
					attr.set(this, value);
			} catch (IOException e) {
				log().d(attr,e);
			}
		}
	}

	@Override
	public synchronized PojoModel getPojoModel() {
		if (pojoModel == null)
			pojoModel = ActivityUtil.createPojoModel(this.getClass());
		return pojoModel;
	}

}
