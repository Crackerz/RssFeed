package o.fcour.se.osrss;

import java.util.Date;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fima.cardsui.objects.Card;

import de.nava.informa.core.ItemIF;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ArticleCard extends Card implements Comparable<ArticleCard>, OnClickListener, SyncImage.Callback {

	private ItemIF article;
	private Context context;
	private View self;
	private Bitmap image;
	
	public ArticleCard(ItemIF article, Context context) {
		this.article = article;
		this.context = context;
	}
	
	@Override
    public View getCardContent(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.card_article, null);
            self = view;
            
            TextView articleTitle = (TextView) view.findViewById(R.id.articleTitle);
            TextView articleDate = ((TextView) view.findViewById(R.id.articleDate));
            TextView articleDesc = ((TextView) view.findViewById(R.id.articleDesc));
            articleTitle.setText(article.getTitle());
            articleDate.setText(article.getDate().toString());
            articleDesc.setText(Jsoup.parse(article.getDescription()).text());
            Document text = Jsoup.parse(article.getDescription());
            
            
            if(image!=null) {
            	setImage();
            } else {
	            Elements imgs = text.getElementsByTag("img");
	            Iterator<Element> it = imgs.iterator();
	            String imgUrl = null;
	            
	            while(it.hasNext()) {
	            	Element next = it.next();
	            	if(!next.attr("src").equals("")) {
	            		imgUrl = next.attr("src");
	            	}
	            }
	            
	            ImageView articleImage = (ImageView) self.findViewById(R.id.articleImage);
	        	articleImage.setVisibility(View.GONE);
	            if(imgUrl!=null) {
	            	SyncImage image = new SyncImage(this);
	            	image.execute(imgUrl);
	            }
            }
            
            if(!article.getUnRead()) {
            	view.setAlpha(.5f);
            }
	
            return view;
    }
	
	public void setImage() {
    	ImageView articleImage = (ImageView) self.findViewById(R.id.articleImage);
    	articleImage.setVisibility(View.VISIBLE);
        articleImage.setImageBitmap(image);
	}
	
	public Date getDate() {
		return article.getDate();
	}

	@Override
	public int compareTo(ArticleCard arg0) {
		return -getDate().compareTo(arg0.getDate());
	}

	@Override
	public void onClick(View v) {
		article.setUnRead(false);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getLink().toString()));
		context.startActivity(intent);
	}

	@Override
	public void complete(Bitmap image) {
		if(image==null) return;
		this.image=image;
		setImage();
	}

}
