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
package org.geomajas.gwt.client.map.layer;

import org.geomajas.configuration.client.ClientLayerInfo;
import org.geomajas.gwt.client.gfx.PainterVisitor;
import org.geomajas.gwt.client.map.MapModel;
import org.geomajas.gwt.client.map.event.HasLayerChangedHandlers;
import org.geomajas.gwt.client.map.event.LayerChangedHandler;
import org.geomajas.gwt.client.map.event.LayerLabeledEvent;
import org.geomajas.gwt.client.map.event.LayerShownEvent;
import org.geomajas.gwt.client.map.event.LayerStyleChangedHandler;
import org.geomajas.gwt.client.spatial.Bbox;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * <p>
 * An abstract layer implements common behavior and data for all layers. Since we will want to paint layers on the map,
 * it must implement the <code>Paintable</code> interface.
 * </p>
 * 
 * @param <T>
 *            layer info {@link ClientLayerInfo}
 * 
 * @author Pieter De Graef
 * @author Jan De Moerloose
 * @author Frank Wynants
 */
public abstract class AbstractLayer<T extends ClientLayerInfo> implements Layer<T>, HasLayerChangedHandlers {

	private T layerInfo;

	/** The model behind a map. Every layer belongs to such a model. */
	protected MapModel mapModel;

	private boolean showing;

	private boolean selected;

	private boolean labeled;

	private boolean visible;

	protected HandlerManager handlerManager;

	// -------------------------------------------------------------------------
	// Constructor:
	// -------------------------------------------------------------------------

	protected AbstractLayer(MapModel mapModel, T layerInfo) {
		this.mapModel = mapModel;
		this.layerInfo = layerInfo;
		this.visible = layerInfo.isVisible();
		this.updateShowing();
		handlerManager = new HandlerManager(this);
	}

	// -------------------------------------------------------------------------
	// Public methods:
	// -------------------------------------------------------------------------

	/**
	 * Add a handler that registers changes in layer status.
	 * 
	 * @param handler
	 *            The new handler to be added.
	 */
	public HandlerRegistration addLayerChangedHandler(LayerChangedHandler handler) {
		return handlerManager.addHandler(LayerChangedHandler.TYPE, handler);
	}

	/**
	 * Add a handler that registers changes in layer style.
	 * 
	 * @param handler
	 *            The new handler to be added.
	 */
	public HandlerRegistration addLayerStyleChangedHandler(LayerStyleChangedHandler handler) {
		return handlerManager.addHandler(LayerStyleChangedHandler.TYPE, handler);
	}

	/**
	 * Return the layer information.
	 * 
	 * @return the layer info
	 */
	public T getLayerInfo() {
		return layerInfo;
	}

	/**
	 * The PainterVisitor accept function, that determines how this object should be rendered.
	 */
	public abstract void accept(PainterVisitor visitor, Object group, Bbox bounds, boolean recursive);

	/**
	 * Is this layer currently selected or not?
	 * 
	 * @return true if selected, false otherwise
	 */
	public boolean isSelected() {
		return selected;
	}

	public void updateShowing() {
		double scale = mapModel.getMapView().getCurrentScale();
		if (visible) {
			if (scale >= layerInfo.getMinimumScale().getPixelPerUnit()
					&& scale <= layerInfo.getMaximumScale().getPixelPerUnit()) {
				showing = true;
			} else {
				showing = false;
			}
		} else {
			showing = false;
		}
	}

	// -------------------------------------------------------------------------
	// Getters and setters:
	// -------------------------------------------------------------------------

	/**
	 * Return this layer's client ID as defined in the configuration.
	 * 
	 * @return id of the client layer
	 */
	public String getId() {
		return layerInfo.getId();
	}

	/**
	 * Return this layer's server ID as defined in the configuration.
	 * 
	 * @return id of the server layer
	 */
	public String getServerLayerId() {
		return layerInfo.getServerLayerId();
	}

	/**
	 * Returns a nice name for the group to use in the DOM, not necessarily unique.
	 * 
	 * @return name
	 */
	public String getGroupName() {
		return layerInfo.getId();
	}

	public MapModel getMapModel() {
		return mapModel;
	}

	public String getLabel() {
		return layerInfo.getLabel();
	}

	public boolean isLabeled() {
		return showing && labeled;
	}

	public double getMinimumScale() {
		return layerInfo.getMinimumScale().getPixelPerUnit();
	}

	public double getMaximumScale() {
		return layerInfo.getMaximumScale().getPixelPerUnit();
	}

	public void setLabeled(boolean labeled) {
		this.labeled = labeled;
		handlerManager.fireEvent(new LayerLabeledEvent(this));
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isShowing() {
		return showing;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible
	 *            the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
		updateShowing();
		handlerManager.fireEvent(new LayerShownEvent(this));
	}
}