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
package org.geomajas.gwt.client.widget.attribute;

import org.geomajas.configuration.AssociationAttributeInfo;
import org.geomajas.layer.feature.attribute.AssociationValue;
import org.geomajas.layer.feature.attribute.ManyToOneAttribute;

import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Default implementation of a {@link ManyToOneItem}. This item shows a {@link SelectItem} with the displayAttribute of
 * the possible values.
 * 
 * @author Jan De Moerloose
 * 
 */
public class DefaultManyToOneItem implements ManyToOneItem<SelectItem> {

	private SelectItem selectItem;

	public DefaultManyToOneItem() {
		selectItem = new SelectItem();
	}

	public SelectItem getItem() {
		return selectItem;
	}

	public void toItem(ManyToOneAttribute attribute) {
		selectItem.setValue(attribute.getValue().getId().getValue());
	}

	public void fromItem(ManyToOneAttribute attribute) {
		ListGridRecord record = selectItem.getSelectedRecord();
		if (record != null) {
			Object v = record.getAttributeAsObject(ManyToOneDataSource.ASSOCIATION_ITEM_VALUE_ATTRIBUTE);
			if (v != null && v instanceof AssociationValue) {
				attribute.setValue((AssociationValue) v);
			}
		}
	}

	public void init(AssociationAttributeInfo attributeInfo, AttributeProvider attributeProvider) {
		selectItem.setOptionDataSource(new ManyToOneDataSource(attributeInfo, attributeProvider));
	}

}
