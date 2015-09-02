package pl.mobilab.imgurclient;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Config;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {

	private static final String TAG = "ImageLoader";

	// private LoadImagesTask task = new LoadImagesTask();
	private static volatile ImageLoader instance;
	private static Context context;

	public static ImageLoader newInstance(Context ctx) {
		if (instance == null) {
			synchronized (ImageLoader.class) {
				if (instance == null) {
					context = ctx;
					instance = new ImageLoader();
				}
			}
		}
		return instance;
	}

	private ImageLoader() {
	}

	static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<LoadImagesTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap, LoadImagesTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<LoadImagesTask>(bitmapWorkerTask);
		}

		public LoadImagesTask getLoadImagesTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

	private static LoadImagesTask getLoadImagesTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getLoadImagesTask();
			}
		}
		return null;
	}

	public static boolean cancelPotentialWork(String url, ImageView imageView) {
		final LoadImagesTask loadImagesTask = getLoadImagesTask(imageView);

		if (loadImagesTask != null) {
			final String bitmapUrl = loadImagesTask.url;
			if (bitmapUrl == null || !bitmapUrl.equals(url)) {
				loadImagesTask.cancel(true);
			} else {
				return false;
			}
		}
		return true;
	}
	
	public void loadBitmap(String url, ImageView imageView) {
	    if (cancelPotentialWork(url, imageView)) {
	        final LoadImagesTask task = new LoadImagesTask(imageView);
	        final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), new BitmapDrawable().getBitmap(), task);
	        imageView.setImageDrawable(asyncDrawable);
	        task.execute(url);
	    }
	}

	private class LoadImagesTask extends AsyncTask<String, Void, Bitmap> {
		
		private String url;
		private final ImageView imageView;

		public LoadImagesTask(ImageView imageView) {
			this.imageView = imageView;
		}

		@Override
		protected Bitmap doInBackground(String... urls) {
			url = urls[0];
			return downloadImage(url);
		}

		// Sets the Bitmap returned by doInBackground
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
	            bitmap = null;
	        }

	        if (imageView != null && bitmap != null) {
	            //final ImageView imageView = imageViewReference.get();
	            final LoadImagesTask loadImagesTask = getLoadImagesTask(imageView);
	            if (this == loadImagesTask && imageView != null) {
	                imageView.setImageBitmap(bitmap);
	            }
	        }
		}

		// Creates Bitmap from InputStream and returns it
		private Bitmap downloadImage(String url) {
			Bitmap bitmap = null;
			try {
				InputStream stream = getHttpConnection(url);
				bitmap = BitmapFactory.decodeStream(stream);
				stream.close();
			} catch (IOException e) {
				Log.e(TAG, "Failed to load image");
			}
			return bitmap;
		}

		private InputStream getHttpConnection(String urlString) {
			InputStream stream = null;
			try {
				URL url = new URL(urlString);
				URLConnection connection = url.openConnection();

				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setRequestMethod("GET");
				httpConnection.setConnectTimeout(10000);
				httpConnection.setReadTimeout(5000);
				httpConnection.connect();

				if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					stream = httpConnection.getInputStream();
				}

			} catch (IOException ex) {
				Log.e(TAG, "Failed to load image");
			}
			return stream;
		}
	}

}
