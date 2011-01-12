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

package org.geomajas.puregwt.client.map.event;

import org.geomajas.global.Api;
import org.geomajas.global.UserImplemented;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;

/**
 * Handler for the addition and removal of layers within a map.
 * 
 * @author Jan De Moerloose
 * @since 1.0.0
 */
@Api(allMethods = true)
@UserImplemented
public interface MapCompositionHandler extends EventHandler {

	Type<MapCompositionHandler> TYPE = new Type<MapCompositionHandler>();

	/**
	 * Called when a new layer has been added to the map model.
	 * 
	 * @param event
	 *            The event.
	 */
	void onLayerAdded(LayerAddedEvent event);

	/**
	 * Called when a existing layer has been removed from the map model.
	 * 
	 * @param event
	 *            The event.
	 */
	void onLayerRemoved(LayerRemovedEvent event);
}