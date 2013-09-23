/*
 	Yaaic - Yet Another Android IRC Client

Copyright 2009-2013 Sebastian Kaspari

This file is part of Yaaic.

Yaaic is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Yaaic is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Yaaic.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.yaaic.activity;

import java.util.ArrayList;

import org.yaaic.R;
import org.yaaic.db.Database;
import org.yaaic.model.Status;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Small dialog to show an edittext for joining channels
 * 
 * @author Sebastian Kaspari <sebastian@yaaic.org>
 * 
 */
public class JoinActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener {
	private ArrayAdapter<String> adapter;
	private ArrayList<String> channels;
	private Database db = null;

	/**
	 * On create
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.join);

		((Button) findViewById(R.id.join)).setOnClickListener(this);

		((EditText) findViewById(R.id.channel)).setSelection(1);

		// add already know channels
		adapter = new ArrayAdapter<String>(this, R.layout.channelitem);
		channels = new ArrayList<String>();
		Bundle extras = getIntent().getExtras();
		db = new Database(this);
		this.channels = db.getFavorites();

		ListView list = (ListView) findViewById(R.id.channels);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);

		for (String channel : channels) {
			adapter.add(channel);
		}

	}

	/**
	 * On click
	 */
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		intent.putExtra("channel", ((EditText) findViewById(R.id.channel))
				.getText().toString());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * On item clicked
	 */
	@Override
	public void onItemClick(AdapterView<?> list, View item, int position,
			long id) {
		String channel = adapter.getItem(position);
		if (channel.compareTo("") != 0) {
			Intent intent = new Intent();
			intent.putExtra("channel", channel);
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	/**
	 * On item long clicked
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> list, View item,
			int position, long id) {
		final String channel = adapter.getItem(position);
		if (channel.compareTo("") != 0) {

			String[] items = { getResources().getString(R.string.action_remove) };

			// //////////////////////

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(channel);
			builder.setCancelable(true);
			builder.setPositiveButton(R.string.action_remove,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							adapter.remove(channel);
							channels.remove(channel);
							db.removeFavorite(channel);
						}
					});
			builder.setNegativeButton(R.string.action_cancel,null);
			// new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog,
			// // int id) {
			// // server.setMayReconnect(false);
			// reconnectDialogActive = false;
			// dialog.cancel();
			// }
			// });
			AlertDialog alert = builder.create();
			alert.show();
		}

		// /////////////////////

		// builder.setItems(items, new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int item) {
		// switch (item) {
		// // case 0: // Remove
		// adapter.remove(channel);
		// channels.remove(channel);
		// db.removeFavorite(channel);
		// break;
		// }
		// }
		// });

		// }
		return false;
	}

}
