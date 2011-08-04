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
package org.geomajas.widget.searchandfilter.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.geomajas.command.CommandResponse;
import org.geomajas.gwt.client.command.CommandCallback;
import org.geomajas.gwt.client.command.Deferred;
import org.geomajas.gwt.client.command.GwtCommand;
import org.geomajas.gwt.client.command.GwtCommandDispatcher;
import org.geomajas.gwt.client.map.MapModel;
import org.geomajas.gwt.client.map.feature.Feature;
import org.geomajas.gwt.client.map.layer.VectorLayer;
import org.geomajas.gwt.client.spatial.geometry.Geometry;
import org.geomajas.gwt.client.util.GeometryConverter;
import org.geomajas.gwt.client.widget.MapWidget;
import org.geomajas.widget.searchandfilter.client.widget.search.ErrorHandler;
import org.geomajas.widget.searchandfilter.command.dto.FeatureSearchRequest;
import org.geomajas.widget.searchandfilter.command.dto.FeatureSearchResponse;
import org.geomajas.widget.searchandfilter.command.dto.GeometryUtilsRequest;
import org.geomajas.widget.searchandfilter.command.dto.GeometryUtilsResponse;
import org.geomajas.widget.searchandfilter.search.dto.Criterion;
import org.geomajas.widget.searchandfilter.search.dto.GeometryCriterion;

import com.google.gwt.core.client.GWT;

/**
 * Convenience class with helper methods for commands.
 * 
 * @author Kristof Heirwegh
 */
public final class SearchCommService {

	/**
	 * Utility class, hide constructor.
	 */
	private SearchCommService() {
	}

	/**
	 * The returned result will contain an unbuffered as well as buffered
	 * result.
	 * 
	 * @param geometries geometries
	 * @param buffer buffer size
	 * @param onFinished
	 *            callback contains two geometries, one unbuffered, one buffered
	 */
	public static void mergeAndBufferGeometries(List<Geometry> geometries, double buffer,
			final DataCallback<Geometry[]> onFinished) {
		GeometryUtilsRequest request = new GeometryUtilsRequest();
		request.setActionFlags(GeometryUtilsRequest.ACTION_BUFFER | GeometryUtilsRequest.ACTION_MERGE);
		request.setIntermediateResults(true);
		request.setBuffer(buffer);
		request.setGeometries(toDtoGeometries(geometries));

		GwtCommand command = new GwtCommand(GeometryUtilsRequest.COMMAND);
		command.setCommandRequest(request);
		GwtCommandDispatcher.getInstance().execute(command, new CommandCallback() {
			public void execute(CommandResponse response) {
				if (response instanceof GeometryUtilsResponse) {
					GeometryUtilsResponse resp = (GeometryUtilsResponse) response;
					if (onFinished != null) {
						Geometry[] geoms = new Geometry[2];
						geoms[0] = GeometryConverter.toGwt(resp.getGeometries()[0]);
						geoms[1] = GeometryConverter.toGwt(resp.getGeometries()[1]);
						onFinished.execute(geoms);
					}
				}
			}
		});
	}

	/**
	 * @param geometries geometries
	 * @param onFinished
	 *            callback returns one geometry
	 */
	public static void mergeGeometries(List<Geometry> geometries, final DataCallback<Geometry> onFinished) {
		GeometryUtilsRequest request = new GeometryUtilsRequest();
		request.setActionFlags(GeometryUtilsRequest.ACTION_MERGE);
		request.setGeometries(toDtoGeometries(geometries));

		GwtCommand command = new GwtCommand(GeometryUtilsRequest.COMMAND);
		command.setCommandRequest(request);
		GwtCommandDispatcher.getInstance().execute(command, new CommandCallback() {
			public void execute(CommandResponse response) {
				if (response instanceof GeometryUtilsResponse) {
					GeometryUtilsResponse resp = (GeometryUtilsResponse) response;
					if (onFinished != null) {
						onFinished.execute(GeometryConverter.toGwt(resp.getGeometries()[0]));
					}
				}
			}
		});
	}

	public static void bufferGeometry(Geometry geometry, double buffer, final DataCallback<Geometry> onFinished) {
		List<Geometry> geometries = new ArrayList<Geometry>();
		geometries.add(geometry);
		bufferGeometries(geometries, buffer, new DataCallback<Geometry[]>() {
			public void execute(Geometry[] result) {
				if (result != null && result.length > 0) {
					onFinished.execute(result[0]);
				} else {
					onFinished.execute(null);
				}
			}
		});
	}

	/**
	 * @param geometries geometries
	 * @param onFinished
	 *            callback returns buffered geometries
	 */
	public static void bufferGeometries(List<Geometry> geometries, double buffer,
			final DataCallback<Geometry[]> onFinished) {
		GeometryUtilsRequest request = new GeometryUtilsRequest();
		request.setActionFlags(GeometryUtilsRequest.ACTION_BUFFER);
		request.setGeometries(toDtoGeometries(geometries));
		request.setBuffer(buffer);

		GwtCommand command = new GwtCommand(GeometryUtilsRequest.COMMAND);
		command.setCommandRequest(request);
		GwtCommandDispatcher.getInstance().execute(command, new CommandCallback() {
			public void execute(CommandResponse response) {
				if (response instanceof GeometryUtilsResponse) {
					GeometryUtilsResponse resp = (GeometryUtilsResponse) response;
					if (onFinished != null) {
						Geometry[] geometriesArray = new Geometry[resp.getGeometries().length];
						for (int i = 0; i < geometriesArray.length; i++) {
							geometriesArray[i] = GeometryConverter.toGwt(resp.getGeometries()[i]);
						}
						onFinished.execute(geometriesArray);
					}
				}
			}
		});
	}

	public static org.geomajas.geometry.Geometry[] toDtoGeometries(List<Geometry> geometries) {
		org.geomajas.geometry.Geometry[] dtoGeometries = new org.geomajas.geometry.Geometry[geometries.size()];
		for (int i = 0; i < geometries.size(); i++) {
			dtoGeometries[i] = GeometryConverter.toDto(geometries.get(i));
		}
		return dtoGeometries;
	}

	public static Criterion buildGeometryCriterion(final Geometry geometry, final MapWidget mapWidget) {
		return new GeometryCriterion(getVisibleServerLayerIds(mapWidget.getMapModel()),
				GeometryConverter.toDto(geometry));
	}

	/**
	 * We return the request object used for this search, this can then be
	 * reused for the csv-export.
	 * 
	 * @param criterion
	 * @param mapWidget
	 * @param onFinished
	 * @param onError
	 *            callback to execute in case of error, optional use null if you
	 *            don't need it
	 */
	public static void searchByCriterion(final Criterion criterion, final MapWidget mapWidget,
			final DataCallback<Map<VectorLayer, List<Feature>>> onFinished, final ErrorHandler onError) {
		FeatureSearchRequest request = new FeatureSearchRequest();
		request.setMapCrs(mapWidget.getMapModel().getCrs());
		request.setCriterion(criterion);
		request.setLayerFilters(getLayerFiltersForCriterion(criterion, mapWidget.getMapModel()));
		request.setFeatureIncludes(GwtCommandDispatcher.getInstance().getLazyFeatureIncludesSelect());

		GwtCommand commandRequest = new GwtCommand(FeatureSearchRequest.COMMAND);
		commandRequest.setCommandRequest(request);
		Deferred def = GwtCommandDispatcher.getInstance().execute(commandRequest, new CommandCallback() {
			public void execute(CommandResponse commandResponse) {
				if (commandResponse instanceof FeatureSearchResponse) {
					FeatureSearchResponse response = (FeatureSearchResponse) commandResponse;
					onFinished.execute(convertFromDto(response.getFeatureMap(),
							mapWidget.getMapModel()));
				}
			}
		});
		if (onError != null) {
			def.addErrorCallback(onError);
		}
	}

	/**
	 * Builds a map with the filters for all layers that are used in the given criterion.
	 *
	 * @param critter
	 * @param mapModel
	 * @return
	 */
	public static Map<String, String> getLayerFiltersForCriterion(Criterion critter, MapModel mapModel) {
		Map<String, String> filters = new HashMap<String, String>();
		Set<String> serverLayerIds = new HashSet<String>();
		critter.serverLayerIdVisitor(serverLayerIds);

		for (VectorLayer vectorLayer : mapModel.getVectorLayers()) {
			if (serverLayerIds.contains(vectorLayer.getServerLayerId())) {
				if (vectorLayer.getFilter() != null && !"".equals(vectorLayer.getFilter())) {
					filters.put(vectorLayer.getServerLayerId(), vectorLayer.getFilter());
				}
			}
		}
		return filters;
	}

	// ----------------------------------------------------------

	/**
	 * This also adds the features to their respective layers, so no need to do
	 * that anymore.
	 * 
	 * @param dtoFeatures
	 * @param model
	 * @return
	 */
	private static Map<VectorLayer, List<Feature>> convertFromDto(
			Map<String, List<org.geomajas.layer.feature.Feature>> dtoFeatures, MapModel model) {
		Map<VectorLayer, List<Feature>> result = new LinkedHashMap<VectorLayer, List<Feature>>();
		for (Entry<String, List<org.geomajas.layer.feature.Feature>> entry : dtoFeatures.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				List<Feature> convertedFeatures = new ArrayList<Feature>();
				VectorLayer layer = convertFromDto(entry.getKey(), entry.getValue(), convertedFeatures, model);
				if (layer != null) {
					result.put(layer, convertedFeatures);
				} else {
					// TODO couldn't find layer client-side ?? maybe should throw an error here
					GWT.log("Couldn't find layer client-side ?? " + entry.getKey());
				}
			}
		}
		return result;
	}

	private static VectorLayer convertFromDto(String serverLayerId,
			List<org.geomajas.layer.feature.Feature> dtoFeatures, List<Feature> convertedFeatures, MapModel model) {
		List<VectorLayer> layers = model.getVectorLayersByServerId(serverLayerId);
		for (VectorLayer vectorLayer : layers) {
			for (org.geomajas.layer.feature.Feature dtoFeature : dtoFeatures) {
				org.geomajas.gwt.client.map.feature.Feature feature = new org.geomajas.gwt.client.map.feature.Feature(
						dtoFeature, vectorLayer);
				vectorLayer.getFeatureStore().addFeature(feature);
				convertedFeatures.add(feature);
			}
			return vectorLayer;
		}
		return null;
	}

	public static List<String> getVisibleServerLayerIds(MapModel mapModel) {
		List<String> layerIds = new ArrayList<String>();
		for (VectorLayer layer : mapModel.getVectorLayers()) {
			if (layer.isShowing()) {
				layerIds.add(layer.getServerLayerId());
			}
		}
		return layerIds;
	}
}
