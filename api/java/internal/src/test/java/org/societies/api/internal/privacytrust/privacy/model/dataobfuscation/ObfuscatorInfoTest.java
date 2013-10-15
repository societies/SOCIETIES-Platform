package org.societies.api.internal.privacytrust.privacy.model.dataobfuscation;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObfuscatorInfoTest {
	private static final Logger LOG = LoggerFactory.getLogger(NameObfuscatorInfo.class.getName());

	@Test
	public void testNameGetObfuscationExample() {
		NameObfuscatorInfo obfuscatorInfo = new NameObfuscatorInfo();
		double obfuscationLevel = -1.0;
		String expected = "\"John Smith\" will become \" \" (anonymous)";
		String actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = -0.99;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.0;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.01;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.19;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		
		expected = "\"John Smith\" will become \"J. S.\"";
		obfuscationLevel = 0.2;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.21;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.39;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		
		expected = "\"John Smith\" will become \"John S.\"";
		obfuscationLevel = 0.4;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.41;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.59;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		
		expected = "\"John Smith\" will become \"J. Smith\"";
		obfuscationLevel = 0.6;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.61;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.79;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		
		expected = "\"John Smith\" will become \"John Smith\" (no change)";
		obfuscationLevel = 0.8;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.81;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 1.0;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 2.0;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testLocationCoordinatesGetObfuscationExample() {
		LocationCoordinatesObfuscatorInfo obfuscatorInfo = new LocationCoordinatesObfuscatorInfo();
		double obfuscationLevel = -1.0;
		String expected = "(2.3509, 48.8566, 542) will become (5.2145, 52.2459, 1245) (anonymous)";
		String actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = -0.99;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.0;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.01;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.29;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.3;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.49;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		
		expected = "(2.3509, 48.8566, 542) will become (2.3491, 48.8555, 693)";
		obfuscationLevel = 0.50;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.6;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.61;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.99;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		
		expected = "(2.3509, 48.8566, 542) will become (2.350987, 48.8566, 542) (no change)";
		obfuscationLevel = 1.0;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 2.0;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testStatusGetObfuscationExample() {
		StatusObfuscatorInfo obfuscatorInfo = new StatusObfuscatorInfo();
		double obfuscationLevel = -1.0;
		String expected = "\"Bad mood\" will become \"Bad mood\" (no change)";
		String actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = -0.99;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.0;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.01;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.29;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.3;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.49;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.50;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.6;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.61;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.99;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 1.0;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 2.0;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
	}
}
