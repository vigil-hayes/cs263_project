package cn.lzu.edu.webdav;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Resources localResources = this.getResources();
		this.setTitle(localResources.getString(R.string.about));
		this.setContentView(R.layout.about);

		WebView aboutView = (WebView) findViewById(R.id.about);

		aboutView.loadUrl("file:///android_asset/about.html");
	}

}