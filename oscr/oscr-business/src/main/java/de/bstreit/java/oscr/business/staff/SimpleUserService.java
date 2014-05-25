/*
 * Open Source Cash Register
 * 
 * Copyright (C) 2013-2014 Bernhard Streit
 * 
 * This file is part of the Open Source Cash Register program.
 * 
 * Open Source Cash Register is free software: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 * 
 * Open Source Cash Register is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *  
 * --
 *  
 * See /licenses/gpl-3.txt for a copy of the GNU GPL.
 * See /README.txt for more information about the software and the author(s).
 * 
 */
package de.bstreit.java.oscr.business.staff;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import de.bstreit.java.oscr.business.staff.dao.IUserRepository;

/**
 * TODO: This needs to return the actual logged-in user, not just any user from
 * the database!
 * 
 * @author Bernhard Streit
 */
@Named
public class SimpleUserService implements IUserService {

	@Inject
	private IUserRepository userRepository;

	private User currentUser;

	@Override
	public User getCurrentUser() {
		if (currentUser == null) {
			loadCurrentUser();
		}
		return currentUser;
	}

	private void loadCurrentUser() {
		final String loginName = System.getenv("USER");

		if (StringUtils.isBlank(loginName)) {
			return;
		}

		currentUser = userRepository.findByLoginname(loginName);

		if (currentUser == null) {
			currentUser = userRepository.save(new User(loginName, loginName));
		}
	}

}
