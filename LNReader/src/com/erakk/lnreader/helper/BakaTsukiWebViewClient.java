package com.erakk.lnreader.helper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.erakk.lnreader.Constants;
import com.erakk.lnreader.DisplayImageActivity;
import com.erakk.lnreader.dao.NovelsDao;
import com.erakk.lnreader.model.NovelContentModel;
import com.erakk.lnreader.model.PageModel;

@TargetApi(11)
public class BakaTsukiWebViewClient extends WebViewClient {
	private Activity caller;
	
	public BakaTsukiWebViewClient(Activity caller) {
		super();
		this.caller = caller;
	}
	
	@Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        if (Uri.parse(url).getHost().equals("www.example.com")) {
//            // This is my web site, so do not override; let my WebView load the page
//            return false;
//        }

		Log.d("shouldOverrideUrlLoading", url);
		
		Context context = view.getContext();
		
		// if image file
		if(url.contains("title=File:")) {
			Intent intent = new Intent(context, DisplayImageActivity.class);
			intent.putExtra(Constants.EXTRA_IMAGE_URL, url);
			context.startActivity(intent);
		}
		else {
			// get the title from url
			boolean useDefault = true;
			if(url.contains("/project/index.php?title=")) {
				String titles[] = url.split("title=", 2);
				if(titles.length == 2 && !(titles[1].length() == 0)) {
					Toast.makeText(context, "Page Title: " + titles[1], Toast.LENGTH_SHORT).show();
					// check if have inside db
					NovelsDao dao = new NovelsDao(context);
					try {
						PageModel pageModel =  dao.getPageModel(titles[1], null);
						caller.getIntent().putExtra(Constants.EXTRA_PAGE, pageModel.getPage());
						caller.recreate();
//						if(pageModel != null) {
//							NovelContentModel content = dao.getNovelContent(pageModel, null);
//							if(content != null) {
//								String html = Constants.WIKI_CSS_STYLE + "<body>" + content.getContent() + "</body></html>" ;
//								view.loadDataWithBaseURL(Constants.BASE_URL, html, "text/html", "utf-8", "");
//								caller.getIntent().putExtra(Constants.EXTRA_PAGE, pageModel.getPage());
//								
//								
//							}
//						}
						useDefault = false;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if(useDefault){
				
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				context.startActivity(browserIntent);
			}
			return true;
		}
        return true;
    }	
}
