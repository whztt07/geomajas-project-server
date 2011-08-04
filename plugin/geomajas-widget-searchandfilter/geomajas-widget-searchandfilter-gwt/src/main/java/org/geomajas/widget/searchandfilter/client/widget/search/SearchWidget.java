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
package org.geomajas.widget.searchandfilter.client.widget.search;

import org.geomajas.widget.searchandfilter.client.widget.search.FavouritesController.FavouriteEvent;
import org.geomajas.widget.searchandfilter.search.dto.Criterion;

/**
 * Interface with common methods needed to build a search widget.
 *
 * @see {@link SearchWidgetRegistry}.
 * @author Kristof Heirwegh
 */
public interface SearchWidget {

	String getSearchWidgetId();

	/**
	 * Name of the search widget (generally I18N name).
	 *
	 * @return name of search (to be used on buttons and such).
	 */
	String getName();

	/**
	 * Show your widget for user interaction, with a search button.
	 */
	void showForSearch();

	/**
	 * Show your widget for user interaction, with a save button.
	 * @param handler A one-off callback to use when the user presses Save.
	 */
	void showForSave(SaveRequestHandler handler);

	void addSearchRequestHandler(SearchRequestHandler handler);

	void removeSearchRequestHandler(SearchRequestHandler handler);

	void addSaveRequestHandler(SaveRequestHandler handler);

	void removeSaveRequestHandler(SaveRequestHandler handler);

	void addFavouriteRequestHandler(FavouriteRequestHandler handler);

	void removeFavouriteRequestHandler(FavouriteRequestHandler handler);

	/**
	 * Convenience method so you don't have to add an event listener to the search system.
	 * <p>
	 * Called when search starts.
	 * <p>
	 * This allows you to show some interaction in the gui. (for example show a
	 * spinner animation)
	 */
	void onSearchStart();

	/**
	 * Convenience method so you don't have to add an event listener to the search system.
	 * <p>
	 * Called when search finished and result was processed.
	 * <p>
	 * This allows you to show some interaction in the gui.
	 */
	void onSearchEnd();

	/**
	 * Configure the widget with previously saved settings.
	 *
	 * @param settings settings
	 */
	void initialize(Criterion settings);

	/**
	 * This method allows you to manually start the search, it's like the user clicked te search button.
	 * (Handy in case you have/want to add multiple ways to start the search (for instance on doubleclick in a list))
	 */
	void startSearch();

	/**
	 * Clean up the widget to its initial state.
	 */
	void reset();

	// ----------------------------------------------------------

	/**
	 * Handler to be notified when the user presses the save button.
	 * 
	 * @author Kristof Heirwegh
	 */
	public interface SaveRequestHandler {
		void onSaveRequested(SaveRequestEvent event);
	}

	/**
	 * Handler to be notified when the user presses the search button.
	 * 
	 * @author Kristof Heirwegh
	 */
	public interface SearchRequestHandler {
		void onSearchRequested(SearchRequestEvent event);
	}

	/**
	 * Handler to be notified when the user presses the favourites button.
	 * 
	 * @author Kristof Heirwegh
	 */
	public interface FavouriteRequestHandler {
		void onAddRequested(FavouriteEvent event);
		void onDeleteRequested(FavouriteEvent event);
		void onChangeRequested(FavouriteEvent event);
	}

	/**
	 * Event used in {@link SearchWidget} handlers.
	 * 
	 * @author Kristof Heirwegh
	 */
	public static class SearchWidgetEvent {
		private final SearchWidget source;
		private final Criterion criterion;

		public SearchWidgetEvent(SearchWidget source, Criterion criterion) {
			this.source = source;
			this.criterion = criterion;
		}

		public SearchWidget getSource() {
			return source;
		}

		public Criterion getCriterion() {
			return criterion;
		}
	}

	/**
	 * Event used in {@link SearchWidget} handlers.
	 * 
	 * @author Kristof Heirwegh
	 */
	public static class SaveRequestEvent extends SearchWidgetEvent {
		public SaveRequestEvent(SearchWidget source, Criterion criterion) {
			super(source, criterion);
		}
	}

	/**
	 * Event used in {@link SearchWidget} handlers.
	 * 
	 * @author Kristof Heirwegh
	 */
	public static class SearchRequestEvent extends SearchWidgetEvent {
		public SearchRequestEvent(SearchWidget source, Criterion criterion) {
			super(source, criterion);
		}
	}
}
