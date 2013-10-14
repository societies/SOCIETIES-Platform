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
		String expected = "\"Mr John Smith\" will become \"Mr John Smith\" (no change)";
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
		
		expected = "\"Mr John Smith\" will become \"Mr Smith\"";
		obfuscationLevel = 0.3;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.31;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		obfuscationLevel = 0.59;
		actual = obfuscatorInfo.getObfuscationExample(obfuscationLevel);
		LOG.info("For "+obfuscationLevel+": Expected: \""+expected+"\" and was: \""+actual+"\"");
		Assert.assertEquals(expected, actual);
		
		expected = "\"Mr John Smith\" will become \"Mr Anonymous\"";
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
		
		expected = "\"Mr John Smith\" will become \"Anonymous\"";
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
		String expected = "(2.3509, 48.8566, 542) will become (2.350987, 48.8566, 542) (no change)";
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
		
		expected = "(2.3509, 48.8566, 542) will become (5.2145, 52.2459, 1245)";
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
