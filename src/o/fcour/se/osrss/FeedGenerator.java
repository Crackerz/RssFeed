package o.fcour.se.osrss;

import java.io.IOException;
import java.net.URL;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.parsers.FeedParser;

import android.os.AsyncTask;

public class FeedGenerator extends AsyncTask<URL,Void,ChannelIF> {

	private Callback callback;

	public FeedGenerator(Callback callback) {
		this.callback = callback;
	}
	@Override
	protected ChannelIF doInBackground(URL... url) {
		ChannelBuilder builder = new ChannelBuilder();
		try {
			ChannelIF feed = FeedParser.parse(builder, url[0]);
			return feed;
		} catch (IOException e) {
			// Network Error
			e.printStackTrace();
		} catch (ParseException e) {
			// Malformed RSS
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onPostExecute(ChannelIF feed) {
		if(callback!=null)
			callback.complete(feed);
	}

	public interface Callback {
		public void complete(ChannelIF feed);
	}
}
