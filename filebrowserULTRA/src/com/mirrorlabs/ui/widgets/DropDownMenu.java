package com.mirrorlabs.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import com.mirrorlabs.filebrowser.R;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

public class DropDownMenu{
	
	private LayoutInflater mLayoutInflater;
	private Activity activity;
	private DropDownMenu menu;
	
	 private Context mContext;
	 private PopupWindow mPopup;
	 private ListAdapter mAdapter;
	  private View mDropDownAnchorView;

	
	 public DropDownMenu(Activity activity) {
	        this.activity = activity;
	    }
	
	protected class DropDownListItem {
		public int icon;
		public String description;
		public Runnable action;

		public DropDownListItem(int icon, String description, Runnable action) {
			super();
			this.icon = icon;
			this.description = description;
			this.action = action;
		}

		@Override
		public String toString() {
			return description;
		}
	}

	  public void setAnchorView(View anchor) {
	        mDropDownAnchorView = anchor;
	    }
	
	/**
	 * You probably want to leave this method alone and implement
	 * {@link #getDropDownItems(int)} instead. Only implement this method if you
	 * want more control over the drop down menu.
	 * 
	 * <p>
	 * Implement this method to set a custom drop down menu when the user clicks
	 * on the icon of the window corresponding to the id. The icon is only shown
	 * when {@link StandOutFlags#FLAG_DECORATION_SYSTEM} is set.
	 * 
	 * @param id
	 *            The id of the window.
	 * @return The drop down menu to be anchored to the icon, or null to have no
	 *         dropdown menu.
	 */
	protected PopupWindow getDropDown() {
		final List<DropDownListItem> items;

		List<DropDownListItem> dropDownListItems = getDropDownItems();
		if (dropDownListItems != null) {
			items = dropDownListItems;
		} else {
			items = new ArrayList<DropDownMenu.DropDownListItem>();
		}

		// add default drop down items
		

		// turn item list into views in PopupWindow
		LinearLayout list = new LinearLayout(activity);
		list.setOrientation(LinearLayout.VERTICAL);

		final PopupWindow dropDown = new PopupWindow(list,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

		for (final DropDownListItem item : items) {
			ViewGroup listItem = (ViewGroup) mLayoutInflater.inflate(
					R.layout.drop_down_list_item, null);
			list.addView(listItem);

			ImageView icon = (ImageView) listItem.findViewById(R.id.icon);
			icon.setImageResource(item.icon);

			TextView description = (TextView) listItem
					.findViewById(R.id.description);
			description.setText(item.description);

			listItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					item.action.run();
					dropDown.dismiss();
				}
			});
		}

		Drawable background = activity.getResources().getDrawable(
				android.R.drawable.editbox_dropdown_dark_frame);
		dropDown.setBackgroundDrawable(background);
		return dropDown;
	}

	/**
	 * Implement this method to populate the drop down menu when the user clicks
	 * on the icon of the window corresponding to the id. The icon is only shown
	 * when {@link StandOutFlags#FLAG_DECORATION_SYSTEM} is set.
	 * 
	 * @param id
	 *            The id of the window.
	 * @return The list of items to show in the drop down menu, or null or empty
	 *         to have no dropdown menu.
	 */
	protected List<DropDownListItem> getDropDownItems() {
		return null;
	}

	
}
