package com.example.opportunisticowncloud;

import android.app.Activity;
import android.os.Bundle;

public class RemoteFileManagerActivity extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_files);
        this.setTitle(this.getResources().getString(R.string.app_title));
        
        /* TODO initVariable();
        createDirectory();    
        listFile();
        localResources = this.getResources();
        */
    }

}
