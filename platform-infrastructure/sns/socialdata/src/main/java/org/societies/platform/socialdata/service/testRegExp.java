package org.societies.platform.socialdata.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.societies.platform.socialdata.converters.ActivityConverterFromFacebook;

public class testRegExp {

	
	private String readFile( String file ) throws IOException {
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String line  = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    String ls = System.getProperty("line.separator");
	    while( ( line = reader.readLine() ) != null ) {
	    	
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }
	    return stringBuilder.toString();
	 }
	
	
	public void test(String story){
		 
		 Pattern p = Pattern.compile(".* was tagged in .* (photo|album)(?:\\s(.*))?.");
	     Matcher m = p.matcher(story);
	     if (m.find()){
	    	 System.out.println("Trovata: "+m.group());
	    	 System.out.println("A: "+m.group(1));
	    	 System.out.println("B: "+m.group(2));
	     }
	}
	
	public void test1(String story){
		 
		 Pattern p = Pattern.compile("\"(.*)\" on .* (\\S+).");
	     Matcher m = p.matcher(story);
	     if (m.find()){
	    	 System.out.println("Trovata1: "+m.group());
	    	 System.out.println("A: "+m.group(1));
	    	 System.out.println("A: "+m.group(2));
	    	 
	     }
	}
	
	public void test2(String story){
		 
		 Pattern p = Pattern.compile(".* (?:is|are) now friends.*.");
	     Matcher m = p.matcher(story);
	     if (m.find()){
	    	 System.out.println("Trovata2: "+m.group());
	    	 
	     }
	}
	
	public void test3(String story){
		 
		 Pattern p = Pattern.compile(".* likes .*.");
	     Matcher m = p.matcher(story);
	     if (m.find()){
	    	 System.out.println("Trovata3: "+m.group());
	    	 
	     }
	}
	
	public void test4(String story){
		 
		 Pattern p = Pattern.compile(".* (?:changed|updated) .* (?:picture|photo).");
	     Matcher m = p.matcher(story);
	     if (m.find()){
	    	 System.out.println("Trovata4: "+m.group());
	    	 
	     }
	}
	
	public void test5(String story){
		 
		 Pattern p = Pattern.compile(".* added (\\d+) new photo.* album (.*).");
	     Matcher m = p.matcher(story);
	     if (m.find()){
	    	 System.out.println("Photos: "+m.group());
	    	 System.out.println("A: "+m.group(1));
	    	 
	     }
	}
	
	
	public void test6(String story){
		 
		 Pattern p = Pattern.compile(".* went to (.*) at (.*).");
	     Matcher m = p.matcher(story);
	     if (m.find()){
	    	 System.out.println("Trovata6: "+m.group());
	    	 System.out.println("A: "+m.group(1));
	    	 System.out.println("B: "+m.group(2));
	    	 
	     }
	}
	
	public void test7(String story){
		 
		 Pattern p = Pattern.compile(".* shared a link.");
	     Matcher m = p.matcher(story);
	     if (m.find()){
	    	 System.out.println("Trovata7: "+m.group());
	    	
	    	 
	     }
	}
	
	
	public void test8(String story){
		 
		 Pattern p = Pattern.compile(".* asked: (.*).");
	     Matcher m = p.matcher(story);
	     if (m.find()){
	    	 System.out.println("Trovata8: "+m.group());
	    	 System.out.println("A: "+m.group(1));
	    	
	    	 
	     }
	}
	
	public void test9(String story){
		 
		 Pattern p = Pattern.compile(".* answered (.*) with (.*).");
	     Matcher m = p.matcher(story);
	     if (m.find()){
	    	 System.out.println("Trovata9: "+m.group());
	    	 System.out.println("A: "+m.group(1));
	    	 System.out.println("B: "+m.group(2));
	    	
	    	 
	     }
	}
	
	
	public testRegExp(){
		 String story = "";
		
		 
		
		try {
			String data = readFile("activities.txt");
			ActivityConverterFromFacebook parser = new ActivityConverterFromFacebook();
			List<ActivityEntry> list = parser.load(data);
			Iterator<ActivityEntry> it = list.iterator();
			while (it.hasNext()){
				story = it.next().getContent();
//				test1(story);
//				test2(story);
//				test3(story);
//				test4(story);
				test5(story);
//				test8(story);
//				test9(story);
				
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		
		
//		 story = "Luca Lamorte was tagged in his own photo.";
//		 test(story);
//		 
//		 story = "Luca Lamorte was tagged in his own photo.";
//		 test(story);
//		 
//		 story = "\"Auguri!!!\" on Ivan Lo Nigro's timeline.";
//		 test1(story);
//		 
//		 story= "\"cavoli te ne davo 25!\" on his own post on Marco Marchetti's wall.";
//		 test1(story);
//		 
//		 story= "Luca Lamorte and Bruno Neri are now friends.";
//		 test2(story);
//		 
//		 story= "Luca Lamorte is now friends with Silvia Agnesina and 2 other people.";
//		 test2(story);
//		 
//		 story= "Luca Lamorte likes NO alla Terza Stella: le sentenze si rispettano.";
//		 test3(story);
//		 
//		 story= "Luca Lamorte likes PINKO e PALLINo and PARIGI";
//		 test3(story);
//	     
//		 
//		 story= "Luca Lamorte updated his cover photo.";
//		 test4(story);
//		 story= "Luca Lamorte changed his profile picture.";
//		 test4(story);
//		 
//		 story= "Luca Lamorte added 29 new photos to the album Teo&Tha Wed.";
//		 test5(story);
//		 story= "Luca Lamorte added 16 new photos to the album Teamlife Photos.";
//		 test5(story);
	     
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new testRegExp();
		

	}

}
