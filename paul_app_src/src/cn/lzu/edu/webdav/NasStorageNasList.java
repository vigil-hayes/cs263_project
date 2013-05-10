package cn.lzu.edu.webdav;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NasStorageNasList extends Activity {

	private static final int DOWNLOAD_TOAST = 2;
	private static final int DELETE_TOAST = 3;
	private static final int PASTE_TOAST = 7;
	private String ROOT = "http://128.111.52.223/owncloud/files/webdav.php";
	private String USERNAME="testuser";
	private String PASSWORD="test";
	private String REMOTEDOWNROOT = ROOT + "/";
	private String LOCALDOWNROOT = "/mnt/sdcard/owncloud/";
	private List<DavResource> resources = null;
	private Sardine sardine = null;
	private int dept = 0;
	private String SOURCEURL = null; // Just for copy and move
	private String DESTANATIONFILE = null; // Just for copy and move
	private boolean isExistRootCache = false;
	private int pasteMode = 0; // 0: copy; 1: move

	Resources localResources = null;

	private final int CREATE_FOLDER = 1;
	private final int PASTE_FILE = 2;
	private int mPresentDown = 0;

	private int mPictures[];

	private String tag = "ownClient-ServerSide";

	private ListView mFileDirList;

	private ArrayList<HashMap<String, Object>> recordItem;

	BroadcastReceiver mExternalStorageReceiver;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.server_files);
		initVariable();
		localResources = this.getResources();
		progressDialog = ProgressDialog.show(this,
				localResources.getString(R.string.load_dialog_tile),
				localResources.getString(R.string.dialog_mess));
		new mThread().run();
		this.setTitle("NasStorage Server");
	}

	class mThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				connecting();
				Message msg_listData = new Message();
				handler.sendMessageDelayed(msg_listData, 500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			progressDialog.dismiss();
			listFile();
			switch (message.what) {
			case 2:
				Toast.makeText(NasStorageNasList.this, localResources.getString(R.string.download_toast), Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(NasStorageNasList.this,
						localResources.getString(R.string.delete_toast),
						Toast.LENGTH_SHORT).show();
				break;
			case 7:
				Toast.makeText(NasStorageNasList.this,
						localResources.getString(R.string.paste_toast),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	public void initVariable() {
		mFileDirList = (ListView) findViewById(R.id.mServerList);
		mPictures = new int[] { R.drawable.back, R.drawable.dir, R.drawable.doc };
	}

	public void connecting() {
		sardine = SardineFactory.begin(USERNAME, PASSWORD);
	}

	public void listFile() {
		try {
			resources = sardine.list(ROOT);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block.
			e.printStackTrace();
		}
		if (resources == null) {
			error();
		}
		fillFile();
	}

	private void error() {
		final Resources localResources = this.getResources();
		AlertDialog.Builder builder = new Builder(NasStorageNasList.this);
		builder.setMessage(localResources.getString(R.string.error));
		builder.setTitle(localResources.getString(R.string.prompt));
		builder.setPositiveButton(localResources.getString(R.string.ok),
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent localIntent = new Intent();
						localIntent.setClass(NasStorageNasList.this,
								SardineActivity.class);
						NasStorageNasList.this.startActivity(localIntent);
						NasStorageNasList.this.finish();
					}
				});
		builder.create().show();
	}

	public void fillFile() {
		SimpleAdapter adapter = null;
		recordItem = null;
		recordItem = new ArrayList<HashMap<String, Object>>();
		int count = 0;
		for (DavResource res : resources) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			if (count == 0) {
				count++;
				map.put("picture", mPictures[0]);
				map.put("type", "currdirectory");
				map.put("name", res.getName());
				recordItem.add(map);
			} else if (res.getContentType() == null) {
				map.put("picture", mPictures[2]);
				map.put("type", "file");
				map.put("name", res.getName());
				recordItem.add(map);
			} else if (res.getContentType().equalsIgnoreCase(
					"httpd/unix-directory")) {
				map.put("picture", mPictures[1]);
				map.put("type", "httpd/unix-directory");
				map.put("name", res.getName());
				recordItem.add(map);
			} else {
				map.put("picture", mPictures[2]);
				map.put("type", "file");
				map.put("name", res.getName());
				recordItem.add(map);
			}
		}
		Collections.sort(recordItem, new ComparatorValues());
		adapter = new SimpleAdapter(this, recordItem, R.layout.server_item,
				new String[] { "picture", "name" }, new int[] {
						R.id.server_picture, R.id.server_text });
		mFileDirList.setAdapter(adapter);
		mFileDirList.setOnItemLongClickListener(new LongClickListener());
		mFileDirList.setOnItemClickListener(new ClickListener());
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, CREATE_FOLDER, 0, R.string.create_folder).setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(0, PASTE_FILE, 0, R.string.paste).setIcon(
				android.R.drawable.ic_menu_set_as);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CREATE_FOLDER:
			createDialog().show();
			break;
		case PASTE_FILE:
			PasteFile();
			break;
		}

		return true;
	}

	protected void onDestroy() {
		Log.i(tag, "onDestroy");
		super.onDestroy();
	}

	public Dialog createDialog() {
		AlertDialog.Builder builder = new Builder(this);
		final View layout = View
				.inflate(this, R.layout.create_new_folder, null);
		final EditText localFileName = (EditText) layout
				.findViewById(R.id.folder_name);

		builder.setTitle(this.getResources().getString(R.string.create_folder));
		builder.setView(layout);
		builder.setPositiveButton(localResources.getString(R.string.ok),
				new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						try {
							sardine.createDirectory(ROOT
									+ localFileName.getText().toString().trim());
						} catch (IOException e) {
							e.printStackTrace();
						}
						listFile();
						dialog.dismiss();
					}
				});

		builder.setNegativeButton(localResources.getString(R.string.cancel),
				new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		return builder.create();
	}

	public Dialog createFunctionDialog() {
		AlertDialog.Builder builder = new Builder(this);
		String[] localArray = { localResources.getString(R.string.download),
				localResources.getString(R.string.delete),
				localResources.getString(R.string.rename),
				localResources.getString(R.string.copy),
				localResources.getString(R.string.move) };
		builder.setItems(localArray, new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					progressDialog = ProgressDialog.show(
							NasStorageNasList.this,
							localResources.getString(R.string.load_dialog_tile),
							localResources.getString(R.string.dialog_mess));
					new DownLoadThread().run();
					break;
				case 1:
					progressDialog = ProgressDialog.show(
							NasStorageNasList.this,
							localResources.getString(R.string.dele_dialog_tile),
							localResources.getString(R.string.dialog_mess));
					new DeleteFileThread().run();
					break;
				case 2:
					RenameFile(
							(String) recordItem.get(mPresentDown).get("name"))
							.show();
					break;
				case 3:
					CopyFile((String) recordItem.get(mPresentDown).get("name"),
							(String) recordItem.get(mPresentDown).get("type"));
					break;
				case 4:
					MoveFile((String) recordItem.get(mPresentDown).get("name"),
							(String) recordItem.get(mPresentDown).get("type"));
					break;
				}
			}

		});
		return builder.create();
	}

	class ClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			mPresentDown = arg2;
			if(recordItem.get(mPresentDown).get("type").toString().equalsIgnoreCase("file")) {
				createFunctionDialog().show();
			} else {
				changeDirectory();
			}

		}

	}

	public void changeDirectory() {
		progressDialog = ProgressDialog.show(NasStorageNasList.this,
				localResources.getString(R.string.load_dialog_tile),
				localResources.getString(R.string.dialog_mess));
		String selectedFile = (String) recordItem.get(mPresentDown).get("name");
		if (mPresentDown == 0 && dept > 0) {
			dept--;
			ROOT = ROOT.replaceAll(selectedFile + "/", "");
		} else if (recordItem.get(mPresentDown).get("type").toString()
				.equalsIgnoreCase("httpd/unix-directory")
				&& mPresentDown > 0) {
			dept++;
			ROOT = ROOT + selectedFile + "/";
		}
		Message msg_listData = new Message();
		handler.sendMessageDelayed(msg_listData, 500);
	}

	class LongClickListener implements OnItemLongClickListener {

		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			if (arg2 > 0) {
				mPresentDown = arg2;
				createFunctionDialog().show();
			}
			return false;
		}

	}

	class DownLoadThread implements Runnable {

		public void run() {
			download((String) recordItem.get(mPresentDown).get("name"),
					(String) recordItem.get(mPresentDown).get("type"));
			Message msg_listData = new Message();
			msg_listData.what = DOWNLOAD_TOAST;
			handler.sendMessageDelayed(msg_listData, 500);
		}

	}

	class DeleteFileThread implements Runnable {

		public void run() {
			deleteFileFromDir(
					(String) recordItem.get(mPresentDown).get("name"),
					(String) recordItem.get(mPresentDown).get("type"));
			Message msg_listData = new Message();
			msg_listData.what = DELETE_TOAST;
			handler.sendMessageDelayed(msg_listData, 500);
		}

	}

	public void deleteFileFromDir(String fileName, String type) {
		String destDel = (ROOT + fileName).replaceAll(" ", "%20");
		if (type.equals("httpd/unix-directory")) {
			destDel = destDel + "/";
		}
		try {
			sardine.delete(destDel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void download(String fileName, String type) {
		int tmpCount = 0;
		if (type.equals("httpd/unix-directory")) {
			REMOTEDOWNROOT = ROOT + fileName + "/";
			LOCALDOWNROOT = LOCALDOWNROOT + fileName + "/";
			List<DavResource> downList = null;
			try {
				downList = sardine.list(REMOTEDOWNROOT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (DavResource res : downList) {
				if (tmpCount == 0) {
					tmpCount++;
					continue;
				}
				if (res.getContentType().equalsIgnoreCase(
						"httpd/unix-directory")) {
					download(res.getName(), res.getContentType());
				} else {
					downFile(res.getName());
				}
			}
		} else {
			downFile(fileName);
		}
	}

	public void downFile(String fileName) {
		try {
			File destDir = new File(LOCALDOWNROOT);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
			File outputFile = new File(destDir, fileName);

			InputStream fis = sardine.get(REMOTEDOWNROOT
					+ fileName.replace(" ", "%20"));
			FileOutputStream fos = new FileOutputStream(outputFile);
			byte[] buffer = new byte[1444];
			int byteread = 0;
			while ((byteread = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, byteread);
			}
			fis.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 
	 */
	public Dialog RenameFile(final String fileName) {
		AlertDialog.Builder builder = new Builder(this);
		final View layout = View
				.inflate(this, R.layout.create_new_folder, null);
		final EditText localFileName = (EditText) layout
				.findViewById(R.id.folder_name);
		localFileName.setText(fileName);

		builder.setTitle(this.getResources().getString(R.string.rename_prompt));
		builder.setView(layout);
		builder.setPositiveButton(localResources.getString(R.string.ok),
				new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						try {
							sardine.move(ROOT + fileName.replaceAll(" ", "%20"),
									ROOT + localFileName.getText().toString().replaceAll(" ", "%20"));
						} catch (IOException e) {
							e.printStackTrace();
						}
						listFile();
						Toast.makeText(NasStorageNasList.this,
								ROOT + localFileName.getText(),
								Toast.LENGTH_LONG).show();
						dialog.dismiss();
					}
				});

		builder.setNegativeButton(localResources.getString(R.string.cancel),
				new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		return builder.create();
	}

	public void CopyFile(String fileName, String type) {
		if (type.equalsIgnoreCase("httpd/unix-directory")) {
			SOURCEURL = ROOT + fileName.replace(" ", "%20") + "/";
			DESTANATIONFILE = fileName.replace(" ", "%20") + "/";
		} else {
			SOURCEURL = ROOT + fileName.replace(" ", "%20");
			DESTANATIONFILE = fileName.replace(" ", "%20");
		}
		isExistRootCache = true;
		pasteMode = 0;
		Toast.makeText(this,
				localResources.getString(R.string.copy_toast) + fileName,
				Toast.LENGTH_SHORT).show();
	}

	public void MoveFile(String fileName, String type) {
		if (type.equalsIgnoreCase("httpd/unix-directory")) {
			SOURCEURL = ROOT + fileName.replace(" ", "%20") + "/";
			DESTANATIONFILE = fileName.replace(" ", "%20") + "/";
		} else {
			SOURCEURL = ROOT + fileName.replace(" ", "%20");
			DESTANATIONFILE = fileName.replace(" ", "%20");
		}
		isExistRootCache = true;
		pasteMode = 1;
		Toast.makeText(this,
				localResources.getString(R.string.move_toast) + fileName,
				Toast.LENGTH_SHORT).show();
	}

	public void PasteFile() {
		if (!isExistRootCache) {
			Toast.makeText(this,
					localResources.getString(R.string.cannotpaste_toast),
					Toast.LENGTH_SHORT).show();
			return;
		}
		progressDialog = ProgressDialog.show(NasStorageNasList.this,
				localResources.getString(R.string.load_dialog_tile),
				localResources.getString(R.string.dialog_mess));
		isExistRootCache = false;
		switch (pasteMode) {
		case 0:
			try {
				sardine.copy(SOURCEURL, ROOT + DESTANATIONFILE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 1:
			try {
				sardine.move(SOURCEURL, ROOT + DESTANATIONFILE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		Message msg_listData = new Message();
		msg_listData.what = PASTE_TOAST;
		handler.sendMessageDelayed(msg_listData, 500);

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent localIntent = new Intent();
			localIntent.setClass(NasStorageNasList.this, SardineActivity.class);
			NasStorageNasList.this.startActivity(localIntent);
			NasStorageNasList.this.finish();
		}
		return false;
	}

	public static final class ComparatorValues implements
			Comparator<HashMap<String, Object>> {

		@Override
		public int compare(HashMap<String, Object> object1,
				HashMap<String, Object> object2) {

			int result = 0;
			String type1 = (String) object1.get("type");
			String type2 = (String) object2.get("type");
			String name1 = (String) object1.get("name");
			String name2 = (String) object2.get("name");
			if (type1.equals("currdirectory") || type2.equals("currdirectory")) {
				if (type1.equals("currdirectory")) {
					result = -1;
				} else {
					result = 1;
				}
			} else if (type1.equals("httpd/unix-directory")
					&& type2.equals("httpd/unix-directory")) {
				if (name1.compareTo(name2) > 0) {
					result = 1;
				} else {
					result = -1;
				}
			} else if (type1.equals("httpd/unix-directory")
					|| type2.equals("httpd/unix-directory")) {
				if (type1.equals("httpd/unix-directory")) {
					result = -1;
				} else {
					result = 1;
				}
			} else {
				if (name1.compareTo(name2) > 0) {
					result = 1;
				} else {
					result = -1;
				}
			}

			return result;

		}
	}
}