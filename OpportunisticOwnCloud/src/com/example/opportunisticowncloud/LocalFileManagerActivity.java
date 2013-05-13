package com.example.opportunisticowncloud;

import com.example.opportunisticowncloud.LocalFileManagerActivity.UpdateUiHandler;

import com.googlecode.sardine.Sardine;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


import com.googlecode.sardine.SardineFactory;


import com.example.opportunisticowncloud.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class LocalFileManagerActivity extends Activity {
	
public static final int UPLOAD_TOAST = 1;
	
	private String ROOT = "http://128.111.52.223/owncloud/files/webdav.php/";
	private String USERNAME="testuser";
	private String PASSWORD="test";
	private Sardine sardine = null;
	
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
	static UpdateUiHandler mUpdateUiHandler;
	private ProgressDialog progressDialog;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.local_files);
        this.setTitle(this.getResources().getString(R.string.app_title));
        
        initVariable();
        createDirectory();    
        listFile();
        localResources = this.getResources();
        
    }
	
	protected void onDestroy(){
    	Log.i(tag, "onDestroy");
    	super.onDestroy();
    }
	/*
	 * ==================================
	 * BEGIN LISTENERS
	 * ==================================
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event){
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		Intent localIntent = new Intent();
    		localIntent.setClass(LocalFileManagerActivity.this, MainActivity.class);
    		LocalFileManagerActivity.this.startActivity(localIntent);
    		LocalFileManagerActivity.this.finish();
    	}
    	return false;
    }
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
	  class LongClickListener implements OnItemLongClickListener{

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				mPresentClick = arg2;
				createFunctionDialog().show();
				return false;
			}
	    	
	    }
	    
	/*
	 * ==================================
	 * END LISTENERS
	 * ==================================
	 */
	/*
	 * ==================================
	 * BEGIN HANDLER CLASSES
	 * ==================================
	 */
    private Handler handler = new Handler() {               
        public void handleMessage(Message message) {
            progressDialog.dismiss();
            listFile();
            if(message.what == 1) {
            	Toast.makeText(LocalFileManagerActivity.this, localResources.getString(R.string.upload_toast), Toast.LENGTH_SHORT).show();
            }
        }
	};
    
	public class UpdateUiHandler extends Handler{
		public void handleMessage(final Message msg) {  
			LocalFileManagerActivity.this.setTitle("���ϴ�" + msg.what + "%");
		}
	}

	/*
	 * ====================================
	 * END HANDLER CLASSES
	 * ====================================
	 */
	/*
	 * ====================================
	 * BEGIN HELPER FUNCTIONS
	 * ====================================
	 */
	public void initVariable(){
    	mFileDirList = (ListView)findViewById(R.id.mLocalList);
    	mPictures = new int[]{R.drawable.back_icon, R.drawable.folder_icon, R.drawable.file_icon};
    	mUpdateUiHandler = new UpdateUiHandler();
    	sardine = SardineFactory.begin(USERNAME, PASSWORD);
    }
	
	 public void createDirectory(){
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
	 public void listFile(){
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
			adapter = new SimpleAdapter(this, recordItem, R.layout.local_item, new String[]{"picture", "name"}, new int[]{R.id.local_picture, R.id.local_text});
			mFileDirList.setAdapter(adapter);
			mFileDirList.setOnItemClickListener(new FileChooserListener());
			mFileDirList.setOnItemLongClickListener(new LongClickListener());
	    }
	    
	    private int Invalid(File f){
			if(f.isDirectory()){
				return 0;
			}
			else{
				return 1;
			}
		}
	    
	    public void uploadFile() {
	    	InputStream fis;
			File t = localFiles[mPresentClick];
			try {
				fis = new FileInputStream(t);
				sardine.put(ROOT + t.getName(), fis);
				fis.close();
			} catch (FileNotFoundException e1) {
				Log.e(tag,t.getName());
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    public Dialog createFunctionDialog(){
		    AlertDialog.Builder builder = new Builder(this);
		    String []localArray = {localResources.getString(R.string.upload_file), localResources.getString(R.string.delete)};
		    builder.setItems(localArray, new OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					switch(which){
					case 0:
						progressDialog = ProgressDialog.show(LocalFileManagerActivity.this, localResources.getString(R.string.upload_dialog_tile), localResources.getString(R.string.dialog_mess));
						new UpLoadThread().run();
						break;
					case 1:
						localFiles[mPresentClick].delete();
						listFile();
						break;
					}
				}
		    	
		    });
			return builder.create();
	    }
	
	/*
	 * ====================================
	 * END HELPER FUNCTIONS
	 * ====================================
	 */
	/*
	 * ====================================
	 * BEGIN RUNNABLE
	 * ====================================
	 */
	 class UpLoadThread implements Runnable{

		public void run() {
			uploadFile();
			Message msg_uploadedData = new Message();
			msg_uploadedData.what = UPLOAD_TOAST;
	       	 handler.sendMessageDelayed(msg_uploadedData, 500);
		}
	    	
	 }
	    
	
	/*
	 * ====================================
	 * END RUNNABLE
	 * ====================================
	 */
}
