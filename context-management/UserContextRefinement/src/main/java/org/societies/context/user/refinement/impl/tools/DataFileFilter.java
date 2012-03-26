package org.societies.context.user.refinement.impl.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataFileFilter implements FilenameFilter {
	
	private String[] patterns;
	private Pattern regex;
	
	public DataFileFilter(String s){
		if(!s.contains("|")) patterns = new String[]{s.trim()};
		StringTokenizer st = new StringTokenizer(s,"|");
		patterns = new String[st.countTokens()];
		int count=0;
		while (st.hasMoreTokens()){
			patterns[count]=st.nextToken().trim();
			count++;
		}
	}
	public DataFileFilter(Pattern p){
		this.regex = p;
	}

	public boolean accept(File dir, String name) {
		if (regex!=null){
			Matcher m = regex.matcher(name);
			return m.matches();
		}
		for(String pattern:patterns){
			if (name.toLowerCase().contains(pattern.toLowerCase())) return true;
		}
		return false;
	}
}
