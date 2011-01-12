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

package org.geomajas.gwt.client.action.toolbar;

import org.geomajas.gwt.client.action.ConfigurableAction;
import org.geomajas.gwt.client.action.ToolbarModalAction;
import org.geomajas.gwt.client.controller.editing.ParentEditController;
import org.geomajas.gwt.client.i18n.I18nProvider;
import org.geomajas.gwt.client.widget.MapWidget;

import com.smartgwt.client.widgets.events.ClickEvent;

/**
 * <p>
 * Toolbar modal action that enables or disables the general editing controller ({@link ParentEditingController}) on the
 * map.
 * </p>
 *
 * @author Pieter De Graef
 */
public class EditingModalAction extends ToolbarModalAction implements ConfigurableAction {

	private MapWidget mapWidget;
	
	private boolean maxBoundsDisplayed;

	public EditingModalAction(MapWidget mapWidget) {
		super("[ISOMORPHIC]/geomajas/osgeo/edit.png", I18nProvider.getToolbar().editingSelect());
		this.mapWidget = mapWidget;
	}

	public void onSelect(ClickEvent event) {
		ParentEditController controller = new ParentEditController(mapWidget);
		controller.setMaxBoundsDisplayed(maxBoundsDisplayed);
		mapWidget.setController(controller);
	}

	public void onDeselect(ClickEvent event) {
		mapWidget.setController(null);
	}

	public void configure(String key, String value) {
		if ("maxBoundsDisplayed".equals(key)) {
			maxBoundsDisplayed = Boolean.parseBoolean(value);
		}
	}
}
