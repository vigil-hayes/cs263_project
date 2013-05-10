package cn.lzu.edu.webdav;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SardineActivity extends Activity {
	
	private static final int aboutMenu = Menu.FIRST;
	private static final int exitMenu = Menu.FIRST + 1;
	
	private ListView mList;
	
	private ArrayAdapter<String> mAdapter; 
	
	private List<String> mListData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		this.setTitle(this.getResources().getString(R.string.app_full_name));
		
		mList = (ListView)findViewById(R.id.mNasList);
        initListData();
        
        mAdapter = new ArrayAdapter<String>(this, R.layout.item, mListData);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new ClickListListener());
	}
	
	 @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 final Resources localResources = this.getResources();
		 menu.add(Menu.NONE, aboutMenu, 1, localResources.getString(R.string.about)).setIcon(
				 android.R.drawable.ic_menu_info_details);
		 menu.add(Menu.NONE, exitMenu, 2, localResources.getString(R.string.exit)).setIcon(
				 android.R.drawable.ic_menu_close_clear_cancel);
		 return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case aboutMenu:
			Intent Intent2 = new Intent();
			Intent2.setClass(SardineActivity.this, AboutActivity.class);
			startActivity(Intent2);
			break;
		case exitMenu:
			dialog();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initListData(){
	    	Resources localResource = this.getResources();
	    	mListData = new ArrayList<String>();
	    	mListData.add(localResource.getString(R.string.nas_file));
	    	mListData.add(localResource.getString(R.string.local_file));
	    }
	    
	    class ClickListListener implements OnItemClickListener{

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent localIntent = new Intent();
				switch(arg2){
				case 0:
					localIntent.setClass(SardineActivity.this, NasStorageNasList.class);
					break;
				case 1:
					localIntent.setClass(SardineActivity.this, NasStorageLocalList.class);
					break;
				}
				SardineActivity.this.startActivity(localIntent);
				SardineActivity.this.finish();
			}
	    	
	    }
	    
	    private void dialog() {
	    	final Resources localResources = this.getResources();
			AlertDialog.Builder builder = new Builder(SardineActivity.this);
			builder.setMessage(localResources.getString(R.string.finish));
			builder.setTitle(localResources.getString(R.string.prompt));
			builder.setPositiveButton(localResources.getString(R.string.ok),
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							SardineActivity.this.finish();
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
}