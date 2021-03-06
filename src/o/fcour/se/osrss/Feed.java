package o.fcour.se.osrss;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.fima.cardsui.views.CardUI;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemIF;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.utils.ChannelRegistry;
import de.nava.informa.utils.UpdateChannelInfo;
import de.nava.informa.utils.UpdateChannelTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class Feed extends SlidingActivity implements FeedGenerator.Callback, FeedUpdater.Callback {

	private ChannelRegistry rssFeeds;
	private ArrayList<URL> urls;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);
		setBehindContentView(R.layout.activity_feed);
		
		/** We need a progress bar to show we are refreshing **/
		ProgressBar loading = (ProgressBar) findViewById(R.id.loadingFeed);
		loading.setVisibility(View.VISIBLE);
		
		if(urls==null) { //If no URLS, populate defaults
			urls = new ArrayList<URL>(2);
			try {
				urls.add(new URL("http://dailyegyptian.com/feed/"));
				urls.add(new URL("http://lifehacker.com/rss"));
			} catch (MalformedURLException e) {
				//Since urls are hardcoded, this shouldn't be reached
			}
		}

		if(rssFeeds==null) { //If no feeds, populate them with our URLS
			
			/** This is where we populate the URLS.. Replace later **/
			if(urls==null) { //If no URLS, populate defaults
				urls = new ArrayList<URL>(2);
				try {
					urls.add(new URL("http://dailyegyptian.com/feed/"));
					urls.add(new URL("http://lifehacker.com/rss"));
				} catch (MalformedURLException e) {
					//Since urls are hardcoded, this shouldn't be reached
				}
			}

			rssFeeds = new ChannelRegistry(new ChannelBuilder());
			Iterator<URL> it = urls.iterator();
			while(it.hasNext()) {
				(new FeedGenerator(this)).execute(it.next());
			}
		}
		getSupportActionBar().setHomeButtonEnabled(true);
		getSlidingMenu().setBehindWidthRes(R.dimen.SlidingMenuWidth);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	public void update() {
		Iterator<?> feeds = rssFeeds.getChannels().iterator();
		while(feeds.hasNext()) {
			ChannelIF next = (ChannelIF)feeds.next();
			FeedUpdater.execute(new UpdateChannelTask(rssFeeds,new ChannelBuilder(), next, new UpdateChannelInfo(5)));
		}
	}

	public void refresh() {

		ArrayList<ArticleCard> cards = new ArrayList<ArticleCard>();

		Iterator<?> feeds = rssFeeds.getChannels().iterator();
		while(feeds.hasNext()) {
			ChannelIF next = (ChannelIF)feeds.next();
			Iterator<ItemIF> articles = next.getItems().iterator();
			while(articles.hasNext()) {
				ArticleCard card = new ArticleCard(articles.next(),this);
				card.setOnClickListener(card);
				cards.add(card);
			}
		}
		Collections.sort(cards);

		CardUI feedView = (CardUI) findViewById(R.id.feedVIew);
		feedView.clearCards();
		Iterator<ArticleCard> it = cards.iterator();
		while(it.hasNext()) {
			feedView.addCard(it.next());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getSupportMenuInflater().inflate(R.menu.feed, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			toggle();
		case R.id.feedRefresh:
			refresh();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void complete(ChannelIF feed) {
		ProgressBar loading = (ProgressBar) findViewById(R.id.loadingFeed);
		loading.setVisibility(View.GONE);
		rssFeeds.addChannel(feed,false,Integer.MAX_VALUE);
		refresh();
	}

	@Override
	public void complete() {
		refresh();
	}

}
