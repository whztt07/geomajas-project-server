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
package org.geomajas.plugin.deskmanager.client.gwt.manager.datalayer;

import org.geomajas.gwt.client.util.WidgetLayout;
import org.geomajas.plugin.deskmanager.client.gwt.geodesk.widget.infowindow.NotificationWindow;
import org.geomajas.plugin.deskmanager.client.gwt.manager.events.EditSessionEvent;
import org.geomajas.plugin.deskmanager.client.gwt.manager.events.EditSessionHandler;
import org.geomajas.plugin.deskmanager.client.gwt.manager.events.Whiteboard;
import org.geomajas.plugin.deskmanager.client.gwt.manager.i18n.ManagerMessages;
import org.geomajas.plugin.deskmanager.client.gwt.manager.service.CommService;
import org.geomajas.plugin.deskmanager.client.gwt.manager.service.DataCallback;
import org.geomajas.plugin.deskmanager.domain.dto.LayerConfiguration;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * @author Kristof Heirwegh
 */
public class Datalayers extends VLayout implements EditSessionHandler {

	private static final ManagerMessages MESSAGES = GWT.create(ManagerMessages.class);
	
	private IButton buttonNew;

	private DatalayerGrid grid;

	private DatalayerDetail detail;

	private static final int MARGIN = 20;

	public Datalayers() {
		super(MARGIN);

		detail = new DatalayerDetail();

		VLayout topContainer = new VLayout(5);
		topContainer.setShowResizeBar(true);
		topContainer.setMinHeight(200);
		topContainer.setHeight("60%");
		topContainer.setLayoutBottomMargin(5);

		buttonNew = new IButton(MESSAGES.datalayersNewDatalayerButtonText());
		buttonNew.setIcon(WidgetLayout.iconAdd);
		buttonNew.setAutoFit(true);
		buttonNew.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Window w = new NewLayerModelWizardWindow(new DataCallback<LayerConfiguration>() {

					public void execute(LayerConfiguration result) {
						if (result != null && !"".equals(result)) {
							NotificationWindow.showInfoMessage(MESSAGES.datalayersNewLayerIsBeingSaved());
							CommService.createNewLayerModel(result);
						}
					}
				});
				w.show();
			}
		});

		grid = new DatalayerGrid();
		grid.addSelectionChangedHandler(detail);

		topContainer.addMember(buttonNew);
		topContainer.addMember(grid);

		// ----------------------------------------------------------

		detail.setMinHeight(200);
		detail.setHeight("40%");
		detail.setLayoutTopMargin(5);

		addMember(topContainer);
		addMember(detail);

		Whiteboard.registerHandler(this);
	}

	public void destroy() {
		Whiteboard.unregisterHandler(this);
		super.destroy();
	}

	// -- EditSessionHandler--------------------------------------------------------

	public void onEditSessionChange(EditSessionEvent ese) {
		if (ese.isParentOfRequestee(detail)) {
			grid.setDisabled(ese.isSessionStart());
			buttonNew.setDisabled(ese.isSessionStart());
			// detail handles event itself so no need to do it here
		}
	}
}
