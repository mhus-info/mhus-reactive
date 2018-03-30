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
package de.mhus.cherry.reactive.examples.test;

import de.mhus.cherry.reactive.model.engine.AaaProvider;

public class SimpleAaaProvider implements AaaProvider {

	@Override
	public String getCurrentUserId() {
		return "me";
	}

	@Override
	public boolean hasAdminAccess(String user) {
		return false;
	}

	@Override
	public boolean hasGroupAccess(String user, String group) {
		return true;
	}

	@Override
	public boolean validatePassword(String user, String pass) {
		return true;
	}

	@Override
	public boolean isUserActive(String user) {
		return true;
	}

	@Override
	public boolean hasUserGeneralActorAccess(String uri, String canonicalName, String user) {
		return true;
	}

}
