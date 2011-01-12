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
package org.geomajas.configuration.client;

import org.geomajas.global.Api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * Representation of a configured toolbar.
 * 
 * @author Joachim Van der Auwera
 * @since 1.6.0
 */
@Api(allMethods = true)
public class ClientToolbarInfo implements Serializable {

	private static final long serialVersionUID = 151L;

	@NotNull
	private String id;

	private List<ClientToolInfo> tools = new ArrayList<ClientToolInfo>();

	/**
	 * Get the list of tools for this toolbar.
	 * 
	 * @return list of {@link ClientToolInfo}
	 */
	public List<ClientToolInfo> getTools() {
		return tools;
	}

	/**
	 * Set the list of tools for this toolbar.
	 * 
	 * @param tools
	 *            list of tools
	 */
	public void setTools(List<ClientToolInfo> tools) {
		this.tools = tools;
	}

	/**
	 * Get the toolbar id.
	 * 
	 * @return toolbar id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the toolbar id.
	 * 
	 * @param value
	 *            toolbar id
	 */
	public void setId(String value) {
		this.id = value;
	}

}
