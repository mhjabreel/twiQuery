/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tquery.utils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import tquery.Tweet;

/**
 *
 * @author MHJ
 */
public class CSVTweetsWriter implements TweetsWriter {

    private String fileName;
    
    private BufferedWriter writer = null;

    public CSVTweetsWriter(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    
    
    @Override
    public boolean write(Tweet tweet) {
        try {
            this.writer.write(tweet.toString());
            this.writer.newLine();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(CSVTweetsWriter.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public void open() {

        try {
            this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.fileName),"UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(CSVTweetsWriter.class.getName()).log(Level.SEVERE, null, ex);
            this.writer = null;
        }
        
    }

    @Override
    public void close() {
        if (this.writer == null) {
            return;
        }
        try {
            
            this.writer.close();
        } catch (IOException ex) {
            Logger.getLogger(CSVTweetsWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            this.writer = null;
        }
    }
    
}
