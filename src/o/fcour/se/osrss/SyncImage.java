package o.fcour.se.osrss;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class SyncImage extends AsyncTask<String,Void,Bitmap>{

	Callback callback;
	
	public SyncImage(Callback callback) {
		this.callback = callback;
	}
	@Override
	protected Bitmap doInBackground(String... url) {
		try {
			Log.d("Image",url[0]);
			InputStream in = new java.net.URL(url[0]).openStream();
			Bitmap image = BitmapFactory.decodeStream(in);
			return image;
		} catch (Exception e) {
			//Silently Fail
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Bitmap image) {
		if(callback!=null)
			callback.complete(image);
	}
	
	public interface Callback {
		public void complete(Bitmap image);
	}

}
