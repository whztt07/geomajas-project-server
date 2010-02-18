/*
 * This file is part of Geomajas, a component framework for building
 * rich Internet applications (RIA) with sophisticated capabilities for the
 * display, analysis and management of geographic information.
 * It is a building block that allows developers to add maps
 * and other geographic data capabilities to their web applications.
 *
 * Copyright 2008-2010 Geosparc, http://www.geosparc.com, Belgium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.geomajas.gwt.client.samples.security;

import org.geomajas.command.CommandResponse;
import org.geomajas.command.dto.SearchFeatureRequest;
import org.geomajas.command.dto.SearchFeatureResponse;
import org.geomajas.gwt.client.command.CommandCallback;
import org.geomajas.gwt.client.command.GwtCommand;
import org.geomajas.gwt.client.command.GwtCommandDispatcher;
import org.geomajas.gwt.client.map.feature.Feature;
import org.geomajas.gwt.client.map.layer.VectorLayer;
import org.geomajas.gwt.client.samples.base.SamplePanel;
import org.geomajas.gwt.client.samples.base.SamplePanelFactory;
import org.geomajas.gwt.client.samples.i18n.I18nProvider;
import org.geomajas.gwt.client.widget.FeatureAttributeWindow;
import org.geomajas.gwt.client.widget.MapWidget;
import org.geomajas.layer.feature.SearchCriterion;
import org.geomajas.plugin.springsecurity.client.Authentication;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * <p>
 * Sample that tests security on attribute level.
 * </p>
 * 
 * @author Pieter De Graef
 */
public class AttributeSecuritySample extends SamplePanel {

	public static final String TITLE = "AttributeSecurity";

	public static final SamplePanelFactory FACTORY = new SamplePanelFactory() {

		public SamplePanel createPanel() {
			return new AttributeSecuritySample();
		}
	};

	public Canvas getViewPanel() {
		final VLayout layout = new VLayout();
		layout.setMembersMargin(10);
		layout.setWidth100();
		layout.setHeight100();

		// Create horizontal layout for login buttons:
		HLayout buttonLayout = new HLayout();
		buttonLayout.setMembersMargin(10);
		buttonLayout.setHeight(20);

		// Create a button that logs in user "marino":
		IButton loginButtonMarino = new IButton(I18nProvider.getSampleMessages().securityLogInWith("marino"));
		loginButtonMarino.setWidth(150);
		loginButtonMarino.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Authentication.getInstance().login("marino", "marino", null);
			}
		});
		buttonLayout.addMember(loginButtonMarino);

		// Create a button that logs in user "luc":
		IButton loginButtonLuc = new IButton(I18nProvider.getSampleMessages().securityLogInWith("luc"));
		loginButtonLuc.setWidth(150);
		loginButtonLuc.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Authentication.getInstance().login("luc", "luc", null);
			}
		});
		buttonLayout.addMember(loginButtonLuc);

		// Features are bound to a map's model. So let's create a map first:
		final MapWidget map = new MapWidget("duisburgMap", "gwt-samples");
		map.initialize();

		// Get a single feature from the server, using the SearchFeaturesCommand:
		SearchFeatureRequest request = new SearchFeatureRequest();
		request.setBooleanOperator("AND");
		request.setCrs("EPSG:4326"); // Can normally be acquired from the MapModel.
		request.setLayerId("roads");
		request.setMax(1);
		request.setCriteria(new SearchCriterion[] { new SearchCriterion("ID", "=", "1") });
		final GwtCommand command = new GwtCommand("command.feature.Search");
		command.setCommandRequest(request);

		IButton editFeatureButton = new IButton("Show FeatureAttributeWindow widget");
		editFeatureButton.setWidth(150);
		editFeatureButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				final VectorLayer layer = (VectorLayer) map.getMapModel().getLayerByLayerId("roads");
				GwtCommandDispatcher.getInstance().execute(command, new CommandCallback() {

					public void execute(CommandResponse response) {
						if (response instanceof SearchFeatureResponse) {
							SearchFeatureResponse resp = (SearchFeatureResponse) response;
							for (org.geomajas.layer.feature.Feature dtoFeature : resp.getFeatures()) {
								Feature feature = new Feature(dtoFeature, layer);
								FeatureAttributeWindow editor = new FeatureAttributeWindow(feature, true);
								editor.setWidth(400);
								layout.addMember(editor);
							}
						}
					}
				});
			}
		});

		layout.addMember(buttonLayout);
		layout.addMember(editFeatureButton);
		return layout;
	}

	public String getDescription() {
		return I18nProvider.getSampleMessages().attributeSecurityDescription();
	}

	public String[] getConfigurationFiles() {
		return new String[] { "/org/geomajas/gwt/samples/security/security.xml" };
	}

	public String ensureUserLoggedIn() {
		return "luc";
	}
}
