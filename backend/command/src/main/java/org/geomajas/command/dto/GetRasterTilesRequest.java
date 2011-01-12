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

package org.geomajas.command.dto;

import org.geomajas.command.LayerIdCommandRequest;
import org.geomajas.geometry.Bbox;
import org.geomajas.global.Api;

/**
 * Request object for {@link org.geomajas.command.render.GetRasterTilesCommand}.
 *
 * @author Joachim Van der Auwera
 * @since 1.6.0
 */
@Api(allMethods = true)
public class GetRasterTilesRequest extends LayerIdCommandRequest {

	private static final long serialVersionUID = 151L;

	private String crs;

	private Bbox bbox;

	private double scale;

	public GetRasterTilesRequest() {
	}

	/**
	 * Crs which is used for the bounding box coordinates.
	 *
	 * @return crs
	 */
	public String getCrs() {
		return crs;
	}

	/**
	 * Set the crs which should be used for the bounding box coordinates.
	 *
	 * @param crs crs
	 */
	public void setCrs(String crs) {
		this.crs = crs;
	}

	/**
	 * Bounding box for which you need the raster data.
	 *
	 * @return bounding box
	 */
	public Bbox getBbox() {
		return bbox;
	}

	public void setBbox(Bbox bbox) {
		this.bbox = bbox;
	}

	/**
	 * The scale of the view in pixel/unit of coordinate system.
	 *
	 * @return scale
	 */
	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

}
