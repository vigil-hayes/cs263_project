package com.example.opportunisticowncloud;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

	private static final int aboutMenu = Menu.FIRST;
	private static final int exitMenu = Menu.FIRST + 1;
	
	private ListView mList;
	private Button mUpload;
	private Button mDownload;
	
	private ArrayAdapter<String> mAdapter; 
	
	private List<String> mListData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		this.setTitle(this.getResources().getString(R.string.app_title));
		
		mList = (ListView)findViewById(R.id.mOCList);
		mUpload = (Button)findViewById(R.id.button_upload);
        mDownload = (Button)findViewById(R.id.button_download);
        initListData();
        
        mAdapter = new ArrayAdapter<String>(this, R.layout.item, mListData);
        mList.setAdapter(mAdapter);
 
        // Set listeners
        mUpload.setOnClickListener(new UploadListener());
        mDownload.setOnClickListener(new DownloadListener());
	}
	
	/*
	 * =====================================
	 * BEGIN LISTENER FUNCTIONS
	 * =====================================
	 */
	class UploadListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			Intent localIntent = new Intent();
			localIntent.setClass(MainActivity.this, LocalFileManagerActivity.class);
			MainActivity.this.startActivity(localIntent); 
			MainActivity.this.finish();
		}
    	
    }
	
	class DownloadListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			Intent localIntent = new Intent();
			localIntent.setClass(MainActivity.this, RemoteFileManagerActivity.class);
			MainActivity.this.startActivity(localIntent); 
			MainActivity.this.finish();
		}
    	
    }
	/*
	 * =====================================
	 * END LISTENER FUNCTIONS
	 * =====================================
	 */
	/*
	 * =====================================
	 * BEGIN HELPER FUNCTIONS
	 * =====================================
	 */
	private void dialog() {
    	final Resources localResources = this.getResources();
		AlertDialog.Builder builder = new Builder(MainActivity.this);
		builder.setMessage(localResources.getString(R.string.finish));
		builder.setTitle(localResources.getString(R.string.prompt));
		builder.setPositiveButton(localResources.getString(R.string.ok),
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						MainActivity.this.finish();
					}
				});
		builder.setNegativeButton(localResources.getString(R.string.cancel),
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();

	}
	private void initListData(){
    	Resources localResource = this.getResources();
    	mListData = new ArrayList<String>();
    	mListData.add(localResource.getString(R.string.oc_file));
    	mListData.add(localResource.getString(R.string.local_file));
    }
	
	/*
	 * =====================================
	 * END HELPER FUNCTIONS
	 * =====================================
	 */
}
