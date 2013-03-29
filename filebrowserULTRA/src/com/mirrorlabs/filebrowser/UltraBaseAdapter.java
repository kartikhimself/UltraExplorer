
package com.mirrorlabs.filebrowser;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.mirrorlabs.filebrowser.FilebrowserULTRAActivity.Fonts;


import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



class ViewHolder {
    public TextView name=null;
    public TextView info=null;
    public CheckBox select=null;
    public ImageView image=null;
    public LinearLayout rowlayout;
  
    ViewHolder(View row){
  	  name = (TextView)row.findViewById(R.id.label);
  	  name.setTypeface(Fonts.ICS);
  	  info = (TextView)row.findViewById(R.id.info);
 	  info.setTypeface(Fonts.ICS);
  	  
  	  image = (ImageView)row.findViewById(R.id.icon);
  	  select=(CheckBox)row.findViewById(R.id.select_icon);
  	  
  	  
    }
    void populateFrom(String s)
    {
    name.setText(s);
    }
  }

class ViewHolderGrid {
    public TextView name=null;
    public TextView info=null;
    public CheckBox select=null;
    public ImageView image=null;
  
    ViewHolderGrid(View row){
  	  name = (TextView)row.findViewById(R.id.label_grid);
  	  name.setTypeface(Fonts.SONY);
  	  image = (ImageView)row.findViewById(R.id.icon_grid);
  	  select=(CheckBox)row.findViewById(R.id.select_icon_grid);
  	  
    }
    void populateFrom(String s)
    {
    name.setText(s);
    }
  }


public class UltraBaseAdapter extends BaseAdapter {
	ViewHolderGrid holderGrid;
	ViewHolder holderList;
	// viewmode 
	public static final int VIEWMODE_LIST = 0;
	public static final int VIEWMODE_ICON = 1;

	private Context mcontext = null;
	private List<File> mfiles = null;

	private int mViewMode = VIEWMODE_ICON;

	public UltraBaseAdapter(Context context, List<File> files) {
		mcontext = context;
		mfiles = files;
	}

	@Override
	public int getCount() {
		int msize = 0;

		if(mfiles != null)
			msize = mfiles.size();

		return msize;
	}

	@Override
	public File getItem(int position) {

		if((position >= 0) && (position < this.getCount()))
			return mfiles.get(position);	

		return null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}


	@Override
	public void notifyDataSetChanged() {

		super.notifyDataSetChanged();

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		switch (mViewMode) {
		case VIEWMODE_LIST:
		{
			
		}
		break;
		case VIEWMODE_ICON:
		{
			

			
		}
		break;
		default:
			break;
		}

		return convertView;
	}

	

	
	 

}

