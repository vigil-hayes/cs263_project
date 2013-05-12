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

public class LocalFileManagerActivity extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.local_files);
        this.setTitle(this.getResources().getString(R.string.app_title));
        
        /* TODO initVariable();
        createDirectory();    
        listFile();
        localResources = this.getResources();
        */
    }

}
