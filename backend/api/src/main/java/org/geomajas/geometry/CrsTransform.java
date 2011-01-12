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

package org.geomajas.geometry;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.geomajas.global.Api;
import org.opengis.referencing.operation.MathTransform;

/**
 * Extension of a {@link MathTransform} which allows getting the source and target CRSs and a
 * {@link com.vividsolutions.jts.geom.Geometry} with the transformable area.
 *
 * @author Joachim Van der Auwera
 * @since 1.8.0
 */
@Api(allMethods = true)
public interface CrsTransform extends MathTransform {

	/**
	 * Get id or code for this CrsTransform. For example "EPSG:4326->EPSG:900913".
	 *
	 * @return crs id
	 */
	String getId();

	/**
	 * Source {@link Crs} for transformation.
	 *
	 * @return source crs
	 */
	Crs getSource();

	/**
	 * Target {@link Crs} for transformation.
	 *
	 * @return target crs
	 */
	Crs getTarget();

	/**
	 * Geometry of the area which can be transformed using this transformation.
	 *
	 * @return transformable area (in source crs)
	 */
	Geometry getTransformableGeometry();

	/**
	 * Envelope of the area which can be transformed using this transformation.
	 *
	 * @return transformable area (in source crs)
	 */
	Envelope getTransformableEnvelope();

	/**
	 * Bbox of the area which can be transformed using this transformation.
	 *
	 * @return transformable area (in source crs)
	 */
	Bbox getTransformableBbox();

}
