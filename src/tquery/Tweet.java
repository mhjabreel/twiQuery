/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tquery;

/**
 *
 * @author MHJ
 */
public class Tweet {
    
    private long tweetID;
    
    private String text;
    
    private String createdAt;
    
    private String userName;
    
    private String geo;

    public Tweet() {
    }

    public Tweet(long tweetID, String text, String createdAt, String userName, String geo) {
        this.tweetID = tweetID;
        this.text = text;
        this.createdAt = createdAt;
        this.userName = userName;
        this.geo = geo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTweetID() {
        return tweetID;
    }

    public void setTweetID(long tweetID) {
        this.tweetID = tweetID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return String.format("%d, %s, %s, %s, %s", this.tweetID, this.text.replace(",", "$$$COMA$$$"), this.createdAt, this.userName, this.geo);
    }
    
    
    
}
