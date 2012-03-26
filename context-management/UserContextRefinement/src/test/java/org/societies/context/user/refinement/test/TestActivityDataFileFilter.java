package org.societies.context.user.refinement.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;
import org.societies.context.user.refinement.impl.tools.DataFileFilter;

public class TestActivityDataFileFilter {
	
	private static DataFileFilter filter;

	private static String regex = ".*[_talking_learning_].*";//\\.ev\\..*";
	private File dir = new File("resources/speaking/");

	@BeforeClass
	public static void setUpBeforeClass () throws Exception {
		filter = new DataFileFilter (Pattern.compile(regex));
	}
	@Test
	public void testAccept() {
		File[] files = dir.listFiles(filter);
		assertTrue(files.length>0);

	}

}
