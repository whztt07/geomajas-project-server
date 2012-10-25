/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2012 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */
package org.geomajas.plugin.deskmanager.domain.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.geomajas.configuration.client.ScaleInfo;

/**
 * @author Kristof Heirwegh
 */
public class LayerModelDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private boolean active;

	/**
	 * Mag deze laag getoond worden in een publiek loket?
	 */
	private boolean publiek;

	/**
	 * Een meer userfriendly name than clientLayerId, default = clientlayer.label
	 */
	private String name;

	private String layerType;

	private String clientLayerId; // referenced Bean name

	private String owner;

	private ScaleInfo minScale;

	private ScaleInfo maxScale;

	private boolean defaultVisible;

	private boolean showInLegend;

	private boolean readOnly;

	private LayerConfiguration layerConfiguration;

	private List<MailAddressDto> mailAddresses = new ArrayList<MailAddressDto>();

	// ------------------------------------------------------------------

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isPublic() {
		return publiek;
	}

	public void setPublic(boolean publiek) {
		this.publiek = publiek;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClientLayerId() {
		return clientLayerId;
	}

	public void setClientLayerId(String clientLayerId) {
		this.clientLayerId = clientLayerId;
	}

	public ScaleInfo getMinScale() {
		return minScale;
	}

	public void setMinScale(ScaleInfo minScale) {
		this.minScale = minScale;
	}

	public ScaleInfo getMaxScale() {
		return maxScale;
	}

	public void setMaxScale(ScaleInfo maxScale) {
		this.maxScale = maxScale;
	}

	// ----------------------------------------------------------

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientLayerId == null) ? 0 : clientLayerId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		LayerModelDto other = (LayerModelDto) obj;
		if (clientLayerId == null) {
			if (other.clientLayerId != null) {
				return false;
			}
		} else if (!clientLayerId.equals(other.clientLayerId)) {
			return false;
		}
		return true;
	}

	public boolean isDefaultVisible() {
		return defaultVisible;
	}

	public void setDefaultVisible(boolean defaultVisible) {
		this.defaultVisible = defaultVisible;
	}

	public boolean isShowInLegend() {
		return showInLegend;
	}

	public void setShowInLegend(boolean showInLegend) {
		this.showInLegend = showInLegend;
	}

	public String getLayerType() {
		return layerType;
	}

	public void setLayerType(String layerType) {
		this.layerType = layerType;
	}

	public LayerConfiguration getLayerConfiguration() {
		return layerConfiguration;
	}

	public void setLayerConfiguration(LayerConfiguration layerConfiguration) {
		this.layerConfiguration = layerConfiguration;
	}

	public List<MailAddressDto> getMailAddresses() {
		return mailAddresses;
	}

	public void setMailAddresses(List<MailAddressDto> mailAddresses) {
		this.mailAddresses = mailAddresses;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	// -------------------------------------------------

	public String getParameterValue(String key) {
		if (key == null || "".equals(key) || layerConfiguration == null) {
			return null;
		} else {
			return layerConfiguration.getParameterValue(key);
		}
	}
}
