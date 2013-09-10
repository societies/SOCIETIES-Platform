package org.societies.orchestration.cpa.test.util;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.model.Activity;
import org.societies.api.activity.IActivity;
import org.societies.orchestration.cpa.impl.comparison.util.LanguageUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: epic
 * Date: 08/07/13
 * Time: 14:40
 */
public class Tweet2011Extractor {
    protected static Logger LOG = LoggerFactory.getLogger(Tweet2011Extractor.class);
    public static ArrayList<Status> readJsonGzFile(String file){
        LOG.info("reading tweets from file: \""+file+"\"");
        ArrayList<Status> ret = new ArrayList<Status>();
        GZIPInputStream gzipInputStream = null;
        try {
             gzipInputStream = new GZIPInputStream(new FileInputStream(new File(file)));
        } catch (IOException e) {
            LOG.error("could not open file: ",e);
        }
        if(gzipInputStream!=null){
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzipInputStream));
            String strLine;
            int count = 0;
            try {
                while ((strLine = reader.readLine()) != null)   {
                    ret.add(splitLine(strLine));
                    count++;
                }
            } catch (IOException e) {
                LOG.error("Error reading gzip file: ",e);
            }
            LOG.info("read "+count+" lines form gzip file");
        }
        return ret;

    }
    public static List<IActivity> convertToActivities(ArrayList<Status> statuses){
        ArrayList<IActivity> ret = new ArrayList<IActivity>();
        Activity a = null;
        ArrayList<String> targets = null;
        for(Status s : statuses){
            targets = findTargets(s);
            for(String target : targets){
                a = new Activity();
                a.setActor(s.getScreenName());
                a.setObject(s.getText());
                a.setTarget(target);
                //a.setPublished(); TODO: convert json date format to epoch long string.
                ret.add(a);
            }
        }
        return ret;
    }
    public static ArrayList<String> findTargets(Status s){
        String workString = s.getText();
        ArrayList<String> ret = new ArrayList<String>();
        String[] words = s.getText().split(" ");
        for(String word : words){
            if(word.charAt(0) == '@'){
                ret.add(word.substring(word.indexOf("@")+1));
            }
        }
/*        while(workString.contains("@")){
            ret.add(workString.substring(workString.indexOf("@")+1).split(" ")[0]);
            workString = workString.substring(workString.indexOf("@")+1,workString.indexOf(" "));
            workString = workString.trim();
        }*/
        return ret;

    }
    public static List<IActivity> actsFromGzJson(String filename){
        return convertToActivities(readJsonGzFile(filename));
    }
    /**
     * Should return {id,create_at,screen_name,text}
     * @param json
     * @return
     */
    private static Status splitLine(String json){
        Gson gson = new Gson();
        Status ret = gson.fromJson(json,Status.class);
        return ret;

    }
    public static void main(String args[]){
        //Status status=  splitLine("{\"id\":33246488821903360,\"screenName\":\"TaylorBormann\",\"createdAt\":\"11:32 AM - 3 Feb 11\",\"text\":\"@ltrain_ oh there is I promise haha\"}");
        Status status = readJsonGzFile(args[0]).get(0);
        LOG.info("status "+status.getCreatedAt()+" - "+status.getScreenName()+ " : "+status.getText());
    }

    public static boolean isEnglish(String text){
        return LanguageUtil.isEnglish(text);
    }
}
