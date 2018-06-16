package com.stacktips.speechtotext.dataSet;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by apple on 16/06/18.
 */

public class Constants {
    public static String wakeUPWord="hey dish";


    public static String channelVerb="channel";
    public static String channelnumber="number";

     public static ArrayList<String> verbArray = new ArrayList<String>(
             Arrays.asList("Play", "Change", "Switch","Turn"));

     public static ArrayList<String> channelArray=new ArrayList<>(Arrays.asList("number","name"));
    public static ArrayList<String> favouriteArray = new ArrayList<String>(
            Arrays.asList("favorite sport", "favorite Channel", "favorite Movie","favorite Actor"));


    public static ArrayList<String> categoryArray = new ArrayList<String>(
            Arrays.asList("Movie", "Sports", "Music","News"));


}
