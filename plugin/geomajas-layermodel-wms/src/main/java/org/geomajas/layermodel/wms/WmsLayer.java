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
package org.geomajas.layermodel.wms;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geomajas.layer.LayerException;
import org.geomajas.layer.RasterLayer;
import org.geomajas.layer.tile.RasterImage;
import org.geomajas.service.ApplicationService;
import org.geomajas.configuration.RasterLayerInfo;
import org.geomajas.geometry.Bbox;
import org.geomajas.global.ExceptionCode;
import org.geomajas.rendering.RenderException;
import org.geomajas.service.BboxService;
import org.geomajas.service.GeoService;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Layer model for accessing raster data from WMS servers.
 *
 * @author check subversion
 */
public class WmsLayer implements RasterLayer {

	private String layerId;

	protected List<Resolution> resolutions = new ArrayList<Resolution>();

	private final Logger log = LoggerFactory.getLogger(WmsLayer.class);

	private DecimalFormat decimalFormat = new DecimalFormat();

	private String baseWmsUrl, format, version, styles = "";

	@Autowired
	private ApplicationService runtime;

	@Autowired
	private BboxService bboxService;

	@Autowired
	private GeoService geoService;

	private RasterLayerInfo layerInfo;

	private CoordinateReferenceSystem crs;

	public RasterLayerInfo getLayerInfo() {
		return layerInfo;
	}

	public CoordinateReferenceSystem getCrs() {
		return crs;
	}

	private void initCrs() throws LayerException {
		try {
			crs = CRS.decode(layerInfo.getCrs());
		} catch (NoSuchAuthorityCodeException e) {
			throw new LayerException(ExceptionCode.LAYER_CRS_UNKNOWN_AUTHORITY, e, layerInfo.getId(),
					getLayerInfo().getCrs());
		} catch (FactoryException e) {
			throw new LayerException(ExceptionCode.LAYER_CRS_PROBLEMATIC, e, layerInfo.getId(),
					getLayerInfo().getCrs());
		}
	}

	public void setLayerInfo(RasterLayerInfo layerInfo) throws LayerException {
		this.layerInfo = layerInfo;
		initCrs();
		decimalFormat.setDecimalSeparatorAlwaysShown(false);
		decimalFormat.setGroupingUsed(false);
		decimalFormat.setMinimumFractionDigits(0);
		decimalFormat.setMaximumFractionDigits(100);
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		decimalFormat.setDecimalFormatSymbols(symbols);
		layerId = layerInfo.getId();
		List<Double> r = layerInfo.getResolutions();
		if (r != null) {
			calculatePredefinedResolutions(r);
		} else {
			calculateQuadTreeResolutions();
		}
	}

	private void calculateQuadTreeResolutions() {
		// based on quad tree created by subdividing the maximum extent
		Bbox bbox = getLayerInfo().getMaxExtent();
		double maxWidth = bbox.getWidth();
		double maxHeight = bbox.getHeight();
		for (int level = 0; level <= getTileLevel(); level++) {
			double width = maxWidth / Math.pow(2, level);
			double height = maxHeight / Math.pow(2, level);
			resolutions.add(new Resolution(Math.max(width / getTileWidth(), height / getTileHeight()), level,
					getTileWidth(), getTileHeight()));
		}
	}

	private void calculatePredefinedResolutions(List<Double> r) {
		// sort in decreasing order !!!
		Collections.sort(r);
		Collections.reverse(r);
		int level = 0;
		for (double resolution : r) {
			resolutions.add(new Resolution(resolution, level++, getTileWidth(), getTileHeight()));
		}
	}

	@SuppressWarnings("unchecked")
	public List<RasterImage> paint(String boundsCrs, Bbox orgBounds, double scale) throws RenderException {
		List<RasterImage> result = new ArrayList<RasterImage>();
		Bbox bounds = orgBounds;

		// We don't necessarily need to split into same crs and different crs
		// cases,
		// the latter implementation uses identity transform if crs's are equal
		// for map and layer
		// but might introduce bugs in rounding and/or conversions.
		String crsCode = getLayerInfo().getCrs();
		if (crsCode.equals(boundsCrs)) {
			bounds = clipBounds(bounds);
			if (bboxService.isNull(bounds)) {
				return Collections.EMPTY_LIST;
			}
			Resolution bestResolution = calculateBestResolution(scale);
			RasterGrid grid = getRasterGrid(bounds, bestResolution.getTileWidth(), bestResolution.getTileHeight(),
					scale);

			for (int i = grid.getXmin(); i < grid.getXmax(); i++) {
				for (int j = grid.getYmin(); j < grid.getYmax(); j++) {
					double x = grid.getLowerLeft().x + (i - grid.getXmin()) * grid.getTileWidth();
					double y = grid.getLowerLeft().y + (j - grid.getYmin()) * grid.getTileHeight();
					Bbox worldBox = new Bbox(x, y, grid.getTileWidth(), grid.getTileHeight());
					// lower-left becomes upper-left in inverted y-space !!!
					Bbox screenbox = new Bbox(
							Math.round(scale * worldBox.getX()),
							-Math.round(scale * worldBox.getMaxY()),
							Math.round(scale * worldBox.getMaxX()) - Math.round(scale * worldBox.getX()),
							Math.round(scale * worldBox.getMaxY()) - Math.round(scale * worldBox.getY())
					);

					RasterImage image = new RasterImage(screenbox, getLayerInfo().getId() +
							"." + bestResolution.getLevel() + "." + i + "," + j);

					image.setLevel(bestResolution.getLevel());
					image.setXIndex(i);
					image.setYIndex(j);
					resolveUrl(image, bestResolution.getTileWidthPx(), bestResolution.getTileHeightPx(), worldBox);
					result.add(image);
				}
			}
		} else {
			try {
				MathTransform layerToMap = geoService.findMathTransform(CRS.decode(getLayerInfo().getCrs()),
						runtime.getCrs(boundsCrs));
				MathTransform mapToLayer = layerToMap.inverse();

				// Translate the map coordinates to layer coordinates
				DirectPosition leftTop = new DirectPosition2D(bounds.getX(), bounds.getMaxY());
				DirectPosition rightBottom = new DirectPosition2D(bounds.getMaxX(), bounds.getY());
				mapToLayer.transform(leftTop, leftTop);
				mapToLayer.transform(rightBottom, rightBottom);
				Bbox layerBounds = new Bbox(leftTop.getCoordinates()[0], leftTop.getCoordinates()[1],
						rightBottom.getCoordinates()[0] - leftTop.getCoordinates()[0],
						rightBottom.getCoordinates()[1] - leftTop.getCoordinates()[1]);
				double layerScale = bounds.getWidth() * scale / layerBounds.getWidth();

				layerBounds = clipBounds(layerBounds);
				if (bboxService.isNull(layerBounds)) {
					return Collections.EMPTY_LIST;
				}

				// Grid is in layer coordinate space!
				Resolution bestResolution = calculateBestResolution(layerScale);
				RasterGrid grid = getRasterGrid(layerBounds, bestResolution.getTileWidth(), bestResolution
						.getTileHeight(), layerScale);

				for (int i = grid.getXmin(); i < grid.getXmax(); i++) {
					for (int j = grid.getYmin(); j < grid.getYmax(); j++) {
						double x = grid.getLowerLeft().x + (i - grid.getXmin()) * grid.getTileWidth();
						double y = grid.getLowerLeft().y + (j - grid.getYmin()) * grid.getTileHeight();
						Bbox worldBox = new Bbox(x, y, grid.getTileWidth(), grid.getTileHeight());

						// Rounding to avoid white space between raster tiles
						// lower-left becomes upper-left in inverted y-space !!!
						Bbox screenbox = new Bbox(
								Math.round(scale * worldBox.getX()),
								-Math.round(scale * worldBox.getMaxY()),
								Math.round(scale * worldBox.getMaxX()) - Math.round(scale * worldBox.getX()),
								Math.round(scale * worldBox.getMaxY()) - Math.round(scale * worldBox.getY())
						);

						RasterImage image = new RasterImage(screenbox, getLayerInfo().getId() + "." +
								bestResolution.getLevel() + "." + i + "," + j);

						image.setLevel(bestResolution.getLevel());
						image.setXIndex(i);
						image.setYIndex(j);
						resolveUrl(image, (int) screenbox.getWidth(), (int) screenbox.getHeight(), worldBox);
						result.add(image);
					}
				}
			} catch (MismatchedDimensionException e) {
				throw new RenderException(ExceptionCode.RENDER_DIMENSION_MISMATCH, e);
			} catch (TransformException e) {
				throw new RenderException(ExceptionCode.RENDER_TRANSFORMATION_FAILED, e);
			} catch (FactoryException e) {
				throw new RenderException(ExceptionCode.RENDER_TRANSFORMATION_FAILED, e);
			} catch (LayerException e) {
				throw new RenderException(ExceptionCode.RENDER_TRANSFORMATION_FAILED, e);
			}
		}
		return result;
	}

	protected void resolveUrl(RasterImage image, int width, int height, Bbox box) throws RenderException {
		String url = getBaseWmsUrl();
		if (null == url) {
			throw new RenderException(ExceptionCode.PARAMETER_MISSING, "baseWmsUrl");
		}
		int pos = url.lastIndexOf('?');
		if (pos > 0) {
			url += "&SERVICE=WMS";
		} else {
			url += "?SERVICE=WMS";
		}
		url += "&request=GetMap";
		url += "&layers=" + layerId;
		url += "&srs=" + getLayerInfo().getCrs();
		url += "&width=" + width;
		url += "&height=" + height;

		url += "&bbox=" + decimalFormat.format(box.getX()) + "," + decimalFormat.format(box.getY()) + ","
				+ decimalFormat.format(box.getMaxX()) + "," + decimalFormat.format(box.getMaxY());
		url += "&format=" + getFormat();
		url += "&version=" + getVersion();
		url += "&styles=" + getStyles();
		log.debug(url);
		image.setUrl(url);
	}

	protected Resolution calculateBestResolution(double scale) {
		double screenResolution = 1.0 / scale;
		if (screenResolution >= resolutions.get(0).getResolution()) {
			return resolutions.get(0);
		} else if (screenResolution <= resolutions.get(resolutions.size() - 1).getResolution()) {
			return resolutions.get(resolutions.size() - 1);
		} else {
			for (int i = 0; i < resolutions.size() - 1; i++) {
				Resolution upper = resolutions.get(i);
				Resolution lower = resolutions.get(i + 1);
				if (screenResolution <= upper.getResolution() && screenResolution >= lower.getResolution()) {
					if ((upper.getResolution() - screenResolution) > 2 * (screenResolution - lower.getResolution())) {
						return lower;
					} else {
						return upper;
					}
				}
			}
		}
		// should not occur !!!!
		return resolutions.get(resolutions.size() - 1);
	}

	protected RasterGrid getRasterGrid(Bbox bounds, double width, double height, double scale) {
		// slightly adjust the width and height so it becomes integer for the
		// current scale
		double realWidth = ((int) (width * scale)) / scale;
		double realHeight = ((int) (height * scale)) / scale;

		Bbox bbox = getLayerInfo().getMaxExtent();
		int ymin = (int) Math.floor((bounds.getY() - bbox.getY()) / realHeight);
		int ymax = (int) Math.floor((bounds.getMaxY() - bbox.getY()) / realHeight) + 1;
		int xmin = (int) Math.floor((bounds.getX() - bbox.getX()) / realWidth);
		int xmax = (int) Math.floor((bounds.getMaxX() - bbox.getX()) / realWidth) + 1;
		// same adjustment for corner
		double realXmin = ((int) (bbox.getX() * scale)) / scale;
		double realYmin = ((int) (bbox.getY() * scale)) / scale;
		Coordinate lowerLeft = new Coordinate(realXmin + xmin * realWidth, realYmin + ymin * realHeight);
		return new RasterGrid(lowerLeft, xmin, ymin, xmax, ymax, realWidth, realHeight);
	}

	protected int getTileWidth() {
		return getLayerInfo().getTileWidth();
	}

	protected int getTileHeight() {
		return getLayerInfo().getTileHeight();
	}

	protected int getTileLevel() {
		return getLayerInfo().getMaxTileLevel();
	}

	private Bbox clipBounds(Bbox bounds) {
		return bboxService.intersection(bounds, getLayerInfo().getMaxExtent());
	}

	public String getBaseWmsUrl() {
		return baseWmsUrl;
	}

	public void setBaseWmsUrl(String baseWmsUrl) {
		this.baseWmsUrl = baseWmsUrl;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStyles() {
		return styles;
	}

	public void setStyles(String styles) {
		this.styles = styles;
	}

	/**
	 * ???
	 */
	public class RasterGrid {

		private Coordinate lowerLeft;

		private int xmin;

		private int ymin;

		private int xmax;

		private int ymax;

		private double tileWidth;

		private double tileHeight;

		public RasterGrid(Coordinate lowerLeft, int xmin, int ymin, int xmax, int ymax, double tileWidth,
				double tileHeight) {
			super();
			this.lowerLeft = lowerLeft;
			this.xmin = xmin;
			this.ymin = ymin;
			this.xmax = xmax;
			this.ymax = ymax;
			this.tileWidth = tileWidth;
			this.tileHeight = tileHeight;
		}

		public Coordinate getLowerLeft() {
			return lowerLeft;
		}

		public double getTileHeight() {
			return tileHeight;
		}

		public double getTileWidth() {
			return tileWidth;
		}

		public int getXmax() {
			return xmax;
		}

		public int getXmin() {
			return xmin;
		}

		public int getYmax() {
			return ymax;
		}

		public int getYmin() {
			return ymin;
		}

	}

	/**
	 * ???
	 */
	class Resolution {

		private double resolution;

		private int level;

		private int tileWidth;

		private int tileHeight;

		public Resolution(double resolution, int level, int tileWidth, int tileHeight) {
			this.resolution = resolution;
			this.level = level;
			this.tileWidth = tileWidth;
			this.tileHeight = tileHeight;
		}

		public int getLevel() {
			return level;
		}

		public int getTileHeightPx() {
			return tileHeight;
		}

		public int getTileWidthPx() {
			return tileWidth;
		}

		public double getTileHeight() {
			return tileHeight * resolution;
		}

		public double getTileWidth() {
			return tileWidth * resolution;
		}

		public double getResolution() {
			return resolution;
		}
	}
}
