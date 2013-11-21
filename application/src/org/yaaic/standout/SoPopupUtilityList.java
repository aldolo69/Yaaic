package org.yaaic.standout;

import java.util.ArrayList;
import java.util.List;

import org.yaaic.R;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SoPopupUtilityList implements OnKeyListener, OnItemClickListener {
	ArrayAdapter<String> adapter = null;
	SoPopupUtility mPu = null;
	SoPopListOnClickListener cl = null;

	public interface SoPopListOnClickListener {
		public void onClick(String s);
	}

	public SoPopupUtilityList(Context context, View view, List<String> ls,
			SoPopListOnClickListener cl) {
		this.cl = cl;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.so_generic_list, null, false);
		v.setOnKeyListener(this);

		// LinearLayout list = (LinearLayout) v.findViewById(R.id.generic_list);

		// add items

	//	final ArrayList<String> array = new ArrayList<String>(ls);
		adapter = new ArrayAdapter<String>(context,
				R.layout.so_generic_list_item, R.id.generic_list_item_text,
				ls);
		ListView list = (ListView) v.findViewById(R.id.generic_list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		setListViewHeightBasedOnChildren(list);
		mPu = SoPopupUtility.getPopupUtilityScrollable(context, v);
				
				//new SoPopupUtilityScrollable(context, v);
		mPu.showPopup(view, SoPopupUtility.FROM_MIDDLE_X
				+ +SoPopupUtility.FROM_MIDDLE_Y);
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		int iMaxWidth = 0;
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;

		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			totalHeight += listItem.getMeasuredHeight();
			if (iMaxWidth < listItem.getMeasuredWidth()) {
				iMaxWidth = listItem.getMeasuredWidth();
			}

		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		params.width = iMaxWidth;
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	// what to do if the usar press a kay on keyboard? maybe esc? exit...
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			mPu.dismiss();
			return true;
		}

		return false;
	}

	// public void onClick(View vItem) {
	// int count = ((ViewGroup)vItem).getChildCount();
	// for (int i = 0; i <= count; i++) {
	// View vTxt = ((ViewGroup)vItem).getChildAt(i);
	// if (vTxt instanceof TextView) {
	// cl.onClick(((TextView)vTxt).getText().toString());
	// break;
	// }
	// }
	//
	//
	// mPu.dismiss();
	// }

	@Override
	public void onItemClick(AdapterView<?> list, View item, int position,
			long id) {
		cl.onClick(list.getItemAtPosition(position).toString());
		mPu.dismiss();
	}

}