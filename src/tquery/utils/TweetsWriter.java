/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tquery.utils;

import tquery.Tweet;

/**
 *
 * @author MHJ
 */
public interface TweetsWriter {
    
    boolean write(Tweet tweet);
    
}
