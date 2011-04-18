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
package org.geomajas.widget.searchandfilter.search.dto;

import java.util.Set;

/**
 * Criterion with attribute criteria.
 * 
 * @author Kristof Heirwegh
 * 
 */
public class AttributeCriterion implements Criterion {

	private static final long serialVersionUID = 1L;

	private String serverLayerId;

	private String attributeName;

	// TODO use enums
	private static final String OPERATORS = "<,<=,=,<>,>,=>,like";
	/**
	 * <, <=, =, <>, >=, >, like.
	 */
	private String operator;

	private String value;

	// ----------------------------------------------------------

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getServerLayerId() {
		return serverLayerId;
	}

	public void setServerLayerId(String serverLayerId) {
		this.serverLayerId = serverLayerId;
	}

	public boolean isValid() {
		return (serverLayerId != null && attributeName != null && isValidOperator() && value != null);
	}

	public boolean isValidOperator() {
		if (operator != null) {
			return (OPERATORS.contains(operator) && !",".equals(operator));
		} else {
			return false;
		}
	}

	public void serverLayerIdVisitor(Set<String> layerIds) {
		layerIds.add(serverLayerId);
	}
}
