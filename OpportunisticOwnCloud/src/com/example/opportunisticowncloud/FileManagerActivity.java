package com.example.opportunisticowncloud;

// TODO: import com.googlecode.sardine.Sardine;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

// TODO: import cn.lzu.edu.webdav.NasStorageLocalList.UpdateUiHandler;


// TODO: import com.googlecode.sardine.SardineFactory;
/* TODO import cn.lzu.edu.webdav.NasStorageLocalList;
import cn.lzu.edu.webdav.NasStorageLocalList.FileChooserListener;
import cn.lzu.edu.webdav.NasStorageLocalList.LongClickListener;
*/

import com.example.opportunisticowncloud.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class FileManagerActivity extends Activity {
	public static final int UPLOAD_TOAST = 1;
	
	private String ROOT = "http://128.111.52.223/owncloud/files/webdav.php/";
	private String USERNAME="testuser";
	private String PASSWORD="test";
// TODO:	private Sardine sardine = null;
	
	Resources localResources = null;
	
	private final int CREATE_FOLDER = 1;
	private int mPresentClick = 0;

	private int mPictures[];
	
	private String tag = "ownClient-ClientSide";
	private String OCRootPath;
	
	private ListView mFileDirList;
	
	private File mPresentFile;
	private List<File> list = null;
	private ArrayList<HashMap<String, Object>> recordItem;
	private File[] localFiles;
	
	BroadcastReceiver mExternalStorageReceiver;
	// TODO: static UpdateUiHandler mUpdateUiHandler;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initVariable();
        createDirectory();    
        listFile();
        localResources = this.getResources();
	}

	private void listFile() {
		File localPath = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath());
    	localFiles = localPath.listFiles();
    	Arrays.sort(localFiles, new Comparator<File>() {

			@Override
			public int compare(File f1, File f2) {
				int result = 0;
				if(Invalid(f1) == 0 && Invalid(f2) == 0) {
					if(f1.getName().compareTo(f2.getName()) > 0) {
						result = 1;
					} else {
						result = -1;
					}
				} else if(Invalid(f1) == 0 || Invalid(f2) == 0) {
					if(Invalid(f1) == 0) {
						result = -1;
					} else {
						result = 1;
					}
				} else {
					if(f1.getName().compareTo(f2.getName()) > 0) {
						result = 1;
					} else {
						result = -1;
					}
				}
				return result;
			}
    		
    	});
    	setTitle(localPath.getAbsolutePath());
    	fillFile(localFiles);
		
	}

	/**
	 * Creates /sdcard/owncloud/ directory if it does not already exist
	 */
	private void createDirectory() {
		File localPath = android.os.Environment.getExternalStorageDirectory();
    	
    	if(localPath.exists()){
    		Log.i(tag, "localPath.exists");
    		OCRootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/owncloud".trim();
    		localPath = new File(OCRootPath);
    		mPresentFile = localPath;
    		Log.i(tag, "path = " + android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/owncloud");
    		if(!localPath.exists()){
    			Log.i(tag, "mkdir");
    			localPath.mkdirs();
    		}
    	}
		
	}

	private void initVariable() {
		// TODO mFileDirList = (ListView)findViewById(R.id.mServerList);
    	// TODO mPictures = new int[]{R.drawable.back, R.drawable.dir, R.drawable.doc};
    	// TODO: mUpdateUiHandler = new UpdateUiHandler();
    	// TODO: sardine = SardineFactory.begin(USERNAME, PASSWORD);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	/**
	 * Handles the Upload calls.
	 */
	public void onUploadClick(View view) {
		/* TODO: Handle Upload click */
	}
	
	/**
	 * Handles the Download calls.
	 */
	public void onDownloadClick(View view) {
		/* TODO: Handle Download click */
	}
	
	/*
	 * Helper Functions
	 */
	/**
	 * Checks to see if a file is invalid or not
	 * @param f
	 * @return
	 */
    private int Invalid(File f){
		if(f.isDirectory()){
			return 0;
		}
		else{
			return 1;
		}
	}
    
    /**
     * Add a file to the view
     * @param paramFiles
     */
    public void fillFile(File[] paramFiles){
    	SimpleAdapter adapter = null;
		recordItem = new ArrayList<HashMap<String, Object>>();
		list = new ArrayList<File>();
		for(File f: paramFiles){
			if(Invalid(f) == 0){
				list.add(f);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("picture", mPictures[1]);
				map.put("name", f.getName());
				recordItem.add(map);
			}
			if(Invalid(f) == 1){
				list.add(f);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("picture", mPictures[2]);
				map.put("name", f.getName());
				recordItem.add(map);
			}
		}
		// TODO adapter = new SimpleAdapter(this, recordItem, R.layout.local_item, new String[]{"picture", "name"}, new int[]{R.id.local_picture, R.id.local_text});
		mFileDirList.setAdapter(adapter);
		mFileDirList.setOnItemClickListener(new FileChooserListener());
		mFileDirList.setOnItemLongClickListener(new LongClickListener());
    }
    
    /**
     * Create dialogs for creating new folders, 
     * @return
     */
    public Dialog createDialog(){
	    AlertDialog.Builder builder = new Builder(this);
		// TODO final View layout = View.inflate(this, R.layout.create_new_folder, null);
		// TODO: final EditText localFileName = (EditText)layout.findViewById(R.id.folder_name);
		
		// TODO builder.setTitle(this.getResources().getString(R.string.create_folder));
		// TODO builder.setView(layout);
		builder.setPositiveButton(/*localResources.getString(R.string.ok)*/"Ok", new OnClickListener(){
	    
	
		
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				File localFile = mPresentFile;
				/* String localString = localFileName.getText().toString().trim();
				if(localString == "" || localString.equals("")){
					localString = localResources.getString(R.string.create_folder);
				}
	    		localFile = new File(localFile.getAbsolutePath() + "/" + localString);
	    		if(!localFile.exists()){
	    			localFile.mkdir();
	    		}
	    		else{
	    			// TODO Toast.makeText(NasStorageLocalList.this, localResources.getString(R.string.rename), Toast.LENGTH_LONG).show();
	    		}	
	    		setTitle(mPresentFile.getAbsolutePath());
	    		fillFile(mPresentFile.listFiles());
				dialog.dismiss();
				*/
			}
		});
		
		builder.setNegativeButton("Cancel"/* TODO localResources.getString(R.string.cancel)*/, new OnClickListener(){
	
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				dialog.dismiss();
			}
		});
		return builder.create();
    }
    
    /*
     * Listeners
     */
    
    /**
     * When a file is chosen
     * @author student
     *
     */
    private class FileChooserListener implements OnItemClickListener{
		
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			File file = list.get(arg2);
			mPresentFile = file;
			if(file.isDirectory()){
				setTitle(file.getAbsolutePath());
				File[] files = file.listFiles();
				fillFile(files);
			}
		}
	}
    
    /**
     * When a LongClick is encountered, create a dialog
     * @author student
     *
     */
    class LongClickListener implements OnItemLongClickListener{

		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			mPresentClick = arg2;
			//createFunctionDialog().show();
			return false;
		}
    	
    }
}
