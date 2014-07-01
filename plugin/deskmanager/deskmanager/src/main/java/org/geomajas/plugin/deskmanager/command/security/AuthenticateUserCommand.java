/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2014 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */
package org.geomajas.plugin.deskmanager.command.security;

import org.geomajas.plugin.deskmanager.command.security.dto.AuthenticateUserRequest;
import org.geomajas.plugin.deskmanager.domain.security.GroupMember;
import org.geomajas.plugin.deskmanager.domain.security.Profile;
import org.geomajas.plugin.deskmanager.security.LoginSession;
import org.geomajas.plugin.deskmanager.security.ProfileService;
import org.geomajas.plugin.deskmanager.service.common.DtoConverterService;
import org.geomajas.plugin.staticsecurity.command.dto.LoginRequest;
import org.geomajas.plugin.staticsecurity.command.dto.LoginResponse;
import org.geomajas.plugin.staticsecurity.command.staticsecurity.LoginCommand;
import org.geomajas.plugin.staticsecurity.configuration.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Command that will retrieve the profiles of a user for a specific geodesk.
 *
 * @author Jan Venstermans
 */
@Component(AuthenticateUserRequest.COMMAND)
public class AuthenticateUserCommand extends LoginCommand {

	@Autowired
	private ProfileService profileService;

	@Autowired
	private DtoConverterService converterService;

	private final Logger log = LoggerFactory.getLogger(AuthenticateUserCommand.class);

	public void execute(LoginRequest request, LoginResponse response) throws Exception {
		super.execute(request, response);
		/*final String convertedPass = authenticationService.convertPassword(request.getLogin(), request.getPassword());
		UserInfo user = authenticationService.isAuthenticated(request.getLogin(), convertedPass);
		List<Profile> profileList = new ArrayList<Profile>();
		for (GroupMember member : user.getGroups()) {
			profileList.add(converterService.toProfile(member));
		}
		response.setToken(profileService.registerProfilesForUser(new LoginSession(profileList)));*/
	}
}