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

package org.geomajas.gwt.client.spatial.snapping;

import org.geomajas.configuration.SnappingRuleInfo;
import org.geomajas.geometry.Coordinate;
import org.geomajas.gwt.client.map.cache.tile.TileFunction;
import org.geomajas.gwt.client.map.cache.tile.VectorTile;
import org.geomajas.gwt.client.spatial.Bbox;

/**
 * <p>
 * The base class for a tile-function that handles snapping on the features of a single tile. Why a tile? Because the
 * general {@link org.geomajas.gwt.client.spatial.snapping.Snapper} uses the {@link org.geotools.data.FeatureStore}'s
 * <code>query</code> method to search for features within certain bounds and then apply a callback on them (namely
 * implementations of this class).
 * </p>
 * <p>
 * This abstract class acts a a setter for basic values that are needed before a snapping operation can begin. Most of
 * these settings are automatically done, by calling the "setCoordinate" method. This will not only set the original
 * coordinate that needs snapping, but also all sorts of initializers.
 * </p>
 * 
 * @author Pieter De Graef
 */
public abstract class SnappingMode implements TileFunction<VectorTile> {

	/**
	 * The snapping rules that we're acting on.
	 */
	protected SnappingRuleInfo rule;

	/**
	 * The original coordinates that needs snapping.
	 */
	protected Coordinate coordinate;

	/**
	 * A bounding box created around the coordinate when it's set. This box extends from the coordinate by the snapping
	 * rule's distance factor.
	 */
	protected Bbox bounds;

	/**
	 * The snapped coordinates (after snapping).
	 */
	protected Coordinate snappedCoordinate;

	/**
	 * The distance found for the snapped coordinate.
	 */
	protected double distance;

	// -------------------------------------------------------------------------
	// Constructor:
	// -------------------------------------------------------------------------

	/**
	 * The only constructor. Immediately sets a snapping rule.
	 */
	protected SnappingMode(SnappingRuleInfo rule) {
		this.rule = rule;
	}

	// -------------------------------------------------------------------------
	// Public methods:
	// -------------------------------------------------------------------------

	/** The callback's execution function. This is where the actual search for a snapping points happens. */
	public abstract void execute(VectorTile tile);

	/** Set a new coordinate ready to be snapped. */
	public void setCoordinate(Coordinate coordinate) {
		if (coordinate == null) {
			throw new IllegalArgumentException("Can't snap to a null coordinate.");
		}
		this.coordinate = coordinate;
		bounds = new Bbox(coordinate.getX() - rule.getDistance(), coordinate.getY() - rule.getDistance(), rule
				.getDistance() * 2, rule.getDistance() * 2);
		distance = Double.MAX_VALUE;
		snappedCoordinate = coordinate;
	}

	/** The snapped coordinates (after snapping). */
	public Coordinate getSnappedCoordinate() {
		return snappedCoordinate;
	}

	/** The distance found for the snapped coordinate. */
	public double getDistance() {
		return distance;
	}

	/**
	 * A bounding box created around the coordinate when it's set. This box extends from the coordinate by the snapping
	 * rule's distance factor.
	 */
	public Bbox getBounds() {
		return bounds;
	}
}
