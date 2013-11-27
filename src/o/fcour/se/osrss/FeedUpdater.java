package o.fcour.se.osrss;

import de.nava.informa.utils.UpdateChannelTask;
import android.os.AsyncTask;

public class FeedUpdater extends AsyncTask<UpdateChannelTask, Void, String> {

	Callback callback;
	
	public FeedUpdater(Callback callback) {
		this.callback = callback;
	}
	
	@Override
	protected String doInBackground(UpdateChannelTask... arg0) {
		arg0[0].run();
		return null;
	}
	
	@Override
	protected void onPostExecute(String arg0) {
		callback.complete();
	}
	
	public interface Callback {
		public void complete();
	}

}
