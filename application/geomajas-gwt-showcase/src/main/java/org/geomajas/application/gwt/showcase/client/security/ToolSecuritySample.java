/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2011 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.geomajas.application.gwt.showcase.client.security;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import org.geomajas.application.gwt.showcase.client.i18n.ShowcaseMessages;
import org.geomajas.gwt.client.controller.PanController;
import org.geomajas.gwt.client.widget.MapWidget;
import org.geomajas.gwt.client.widget.Toolbar;
import org.geomajas.gwt.example.base.SamplePanel;
import org.geomajas.gwt.example.base.SamplePanelFactory;
import org.geomajas.plugin.staticsecurity.client.Authentication;

/**
 * <p>
 * Sample that tests security on toolbar tool level.
 * </p>
 * 
 * @author Pieter De Graef
 */
public class ToolSecuritySample extends SamplePanel {

	public static final String TITLE = "ToolSecurity";
	private static final ShowcaseMessages MESSAGES = GWT.create(ShowcaseMessages.class);


	private MapWidget map;

	private Toolbar toolbar;

	public static final SamplePanelFactory FACTORY = new SamplePanelFactory() {

		public SamplePanel createPanel() {
			return new ToolSecuritySample();
		}
	};

	public Canvas getViewPanel() {
		final VLayout layout = new VLayout();
		layout.setMembersMargin(10);
		layout.setWidth100();
		layout.setHeight100();

		// Map with ID duisburgMap is defined in the XML configuration. (mapDuisburg.xml)
		final VLayout mapLayout = new VLayout();
		mapLayout.setShowEdges(true);
		mapLayout.setHeight("60%");
		map = new MapWidget("mapToolbarSecurity", "gwtExample");
		map.setController(new PanController(map));
		toolbar = new Toolbar(map);
		mapLayout.addMember(toolbar);
		mapLayout.addMember(map);

		// Create horizontal layout for login buttons:
		HLayout buttonLayout = new HLayout();
		buttonLayout.setMembersMargin(10);

		// Create login handler that re-initializes the map on a successful login:
		final BooleanCallback initMapCallback = new BooleanCallback() {

			public void execute(Boolean value) {
				if (value) {
					toolbar.destroy();
					map.destroy();
					map = new MapWidget("mapToolbarSecurity", "gwtExample");
					toolbar = new Toolbar(map);
					mapLayout.addMember(toolbar);
					mapLayout.addMember(map);
				}
			}
		};

		// Create a button that logs in user "mark":
		IButton loginButtonMarino = new IButton(MESSAGES.securityLogInWith("mark"));
		loginButtonMarino.setWidth("50%");
		loginButtonMarino.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Authentication.getInstance().login("mark", "mark", initMapCallback);
			}
		});
		buttonLayout.addMember(loginButtonMarino);

		// Create a button that logs in user "luc":
		IButton loginButtonLuc = new IButton(MESSAGES.securityLogInWith("luc"));
		loginButtonLuc.setWidth("50%");
		loginButtonLuc.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Authentication.getInstance().login("luc", "luc", initMapCallback);
			}
		});
		buttonLayout.addMember(loginButtonLuc);

		layout.addMember(mapLayout);
		layout.addMember(buttonLayout);
		return layout;
	}

	public String getDescription() {
		return MESSAGES.toolSecurityDescription();
	}

	public String[] getConfigurationFiles() {
		return new String[] { "WEB-INF/security.xml",
				"WEB-INF/mapToolbarSecurity.xml",
				"classpath:org/geomajas/gwt/example/base/layerWmsBluemarble.xml" };
	}

	public String ensureUserLoggedIn() {
		return "luc";
	}
}
