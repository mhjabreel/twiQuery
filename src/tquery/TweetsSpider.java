/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tquery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tquery.utils.TweetsWriter;

/**
 *
 * @author MHJ
 */
public class TweetsSpider {
    
    private TweetsWriter tweetsWriter;
    
    private JLabel lblTweetsLogger = null;
    
    private boolean needToStop = false;
    
    public static String USER_AGENT = "Mozilla";//5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.2117.157 Safari/537.36";

    public TweetsSpider(TweetsWriter tweetsWriter) {
        this.tweetsWriter = tweetsWriter;
    }

    public TweetsSpider(TweetsWriter tweetsWriter, JLabel lblTweetsLogger) {
        this.tweetsWriter = tweetsWriter;
        this.lblTweetsLogger = lblTweetsLogger;
    }
    
    

    public TweetsWriter getTweetsWriter() {
        return tweetsWriter;
    }

    public void setTweetsWriter(TweetsWriter tweetsWriter) {
        this.tweetsWriter = tweetsWriter;
    }

    public JLabel getLblTweetsLogger() {
        return lblTweetsLogger;
    }

    public void setLblTweetsLogger(JLabel lblTweetsLogger) {
        this.lblTweetsLogger = lblTweetsLogger;
    }

    public boolean isNeedToStop() {
        return needToStop;
    }

    public void setNeedToStop(boolean needToStop) {
        this.needToStop = needToStop;
    }
    
    
    public  void crawlTweets(String query) {
        
        this.tweetsWriter.open();
        
        int tweetsScraed = 0;
        
        String startURL;
        try {
            query = query.replace("#", "%23").replace(" ", "%20").replace(":", "%3A");
            startURL = "https://twitter.com/search?q=" + query + "&src=typd&lang=en";
            
            Document doc = Jsoup.connect(startURL).userAgent(USER_AGENT).get();
            Elements timeline = doc.select("#timeline");
            
            if (timeline != null && !timeline.isEmpty()) {
                
                String initData = doc.getElementById("init-data").attr("value");
                JSONObject jInitData = new JSONObject(initData);
                String endPoint = jInitData.getString("searchEndpoint");
                System.out.println(endPoint);
                
                Element streamContainer = timeline.first().select("div.stream-container").first();
                
                String maxPosition = streamContainer.attr("data-min-position");
                System.out.println(maxPosition);
                Elements tweets = streamContainer.select("li.stream-item");
                
                if(tweets == null || tweets.isEmpty()) {
                    this.tweetsWriter.close();
                }
                
                System.out.println(String.format("%s - Found %d tweet(s) ..", getCurrentTime(), tweets.size()));
                
                int writed = processTweets(tweets);
                
                tweetsScraed += writed;
                
                if(lblTweetsLogger != null) {
                    lblTweetsLogger.setText(String.format("%d tweet(s) scraped", tweetsScraed));
                }
                
                System.out.println(String.format("%s - Writed %d tweet(s) ..", getCurrentTime(), writed));
                
                while(!maxPosition.isEmpty() && !this.needToStop) {
                    String nextPage = getNextPage(endPoint, maxPosition);

                    JSONObject jsonObj = new JSONObject(nextPage);

                    maxPosition = jsonObj.getString("min_position"); 
                    
                    doc = Jsoup.parse(jsonObj.getString("items_html"));
                    
                    tweets = doc.select("li.stream-item");
                    
                    if(tweets.isEmpty()) {
                        break;
                    }
                    
                    System.out.println(String.format("%s - Found %d tweet(s) ..", getCurrentTime(), tweets.size()));

                    writed = processTweets(tweets);
                    
                    tweetsScraed += writed;

                    if(lblTweetsLogger != null) {
                        lblTweetsLogger.setText(String.format("%d tweet(s) scraped", tweetsScraed));
                    }
                    System.out.println(String.format("%s - Writed %d tweet(s) ..", getCurrentTime(), writed));                    
                    
                }

            }
            
            this.tweetsWriter.close();
            
            System.out.println();            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TweetsSpider.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TweetsSpider.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }
    
    
    private String getNextPage(String searchEndPoint, String maxPosition) {
        
        String autoLoadURL;
        StringBuilder stringBuilder = new StringBuilder();
        
        try {
            autoLoadURL = "https://twitter.com" +
                    searchEndPoint +
                    "&include_available_features=1&include_entities=1" +
                    "&max_position=" + maxPosition + "&reset_error_state=false";
            URL url = new URL(autoLoadURL);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla");
            System.out.println(url.toString());
            
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            String strTemp = "";
            
            while (null != (strTemp = br.readLine())) {
                System.out.println(strTemp);
                stringBuilder.append(strTemp);
                
            }            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TweetsSpider.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(TweetsSpider.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TweetsSpider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return stringBuilder.toString();
        
        
    }
    
    private int processTweets(Elements tweets) {
        
        int writed = 0;
        
        for (Element tweet: tweets) {
            long tweetID = Long.parseLong(tweet.attr("data-item-id"));
            
            Elements tweetTime = tweet.select("a.tweet-timestamp");
            String createdAt = "";
            if(!tweetTime.isEmpty()) {    
                createdAt = tweetTime.first().attr("title");
            }
            
            Elements tweetsTexts = tweet.select("p.tweet-text");
            
            if(!tweetsTexts.isEmpty()) {
                String tweetText = tweetsTexts.first().text();

                Elements user = tweet.select("span.username");

                String userName = "";

                if(!user.isEmpty()) {
                    userName = user.first().text();
                    if(tweetsWriter.write(new Tweet(tweetID, tweetText, createdAt, userName, null))) {
                        writed++;
                    }                    
                }

            }
            
        }
        return writed;
    }


    
    private String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();    
        return dateFormat.format(date);
    }    
    
}
