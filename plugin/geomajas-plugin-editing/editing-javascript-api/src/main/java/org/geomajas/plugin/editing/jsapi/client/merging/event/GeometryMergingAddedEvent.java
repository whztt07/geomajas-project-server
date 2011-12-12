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

package org.geomajas.plugin.editing.jsapi.client.merging.event;

import org.geomajas.annotation.Api;
import org.geomajas.geometry.Geometry;
import org.geomajas.plugin.jsapi.client.event.JsEvent;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 * Event that reports a geometry has been added to the list for merging.
 * 
 * @author Pieter De Graef
 * @since 1.0.0
 */
@Api(allMethods = true)
@Export
@ExportPackage("org.geomajas.plugin.editing.merging.event")
public class GeometryMergingAddedEvent extends JsEvent<GeometryMergingAddedHandler> implements Exportable {

	private final Geometry geometry;

	public GeometryMergingAddedEvent(Geometry geometry) {
		this.geometry = geometry;
	}

	public Class<GeometryMergingAddedHandler> getType() {
		return GeometryMergingAddedHandler.class;
	}

	protected void dispatch(GeometryMergingAddedHandler handler) {
		handler.onGeometryMergingAdded(this);
	}

	/**
	 * Get the geometry that has been added to the list for merging.
	 * 
	 * @return The geometry that has been added to the list for merging.
	 */
	public Geometry getGeometry() {
		return geometry;
	}
}