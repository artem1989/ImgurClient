package pl.mobilab.imgurclient;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

	private static final String TAG = "Activity";

	private Button button;
	private GridView map;
	private Context context;

	private ImgurServiceHelper imgurServiceHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = this;

		imgurServiceHelper = new ImgurServiceHelper();
		map = (GridView) findViewById(R.id.gridview);

		button = (Button) findViewById(R.id.submit);
		button.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit:
			new HttpAsyncTask().execute("https://api.imgur.com/3/gallery/hot/viral/0.json");
		}
	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			return imgurServiceHelper.GET(urls[0]);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
			ImageAdapter adapter = new ImageAdapter(context, imgurServiceHelper.processJsonResult(result));
			map.setAdapter(adapter);
		}		
		
	}

}
