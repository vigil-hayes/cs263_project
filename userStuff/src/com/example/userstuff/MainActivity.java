package com.example.userstuff;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
		String username = myPrefs.getString("USERNAME",null);
		String password = myPrefs.getString("PASSWORD",null);
		String server = myPrefs.getString("SERVER",null);

		if (username == null || password == null || server == null )
		{
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
		}
		findViewById(R.id.logout).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						
						SharedPreferences myPrefs = view.getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
						SharedPreferences.Editor editor = myPrefs.edit();
						editor.putString("PASSWORD", null);
						editor.putString("SERVER", null);
						editor.putString("USERNAME", null);
						editor.commit();
						
						Intent i = new Intent(view.getContext(), LoginActivity.class);
						startActivity(i);
					}
				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
