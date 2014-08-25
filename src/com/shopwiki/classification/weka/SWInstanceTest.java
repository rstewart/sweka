package com.shopwiki.classification.weka;

import org.junit.Assert;
import org.junit.Test;

import weka.core.Instances;

@SuppressWarnings("static-method")
public class SWInstanceTest {

	private static Attributes ATTS = new Attributes();
	static {
		ATTS.add(new NumericAttribute("score"));
		ATTS.add(new NumericAttribute("count"));
		ATTS.add(new StringAttribute ("title"));
		ATTS.add(new NominalAttribute<String>("option", new String[] {"one", "two", "three"}));
		ATTS.add(new BooleanAttribute("isType"));
	}

	private static Instances DATA = new Instances("Data", ATTS, 0);

	private static Integer COUNT  = 1;
	private static Boolean ISTYPE = true;

	@Test
	public void testSettersAndGetters() {
		SWInstance swin = new SWInstance(DATA);
		swin.set("score", 1.1);
		swin.set("count", 1);
		swin.set("title", "foobar");
		swin.set("option", "two");
		swin.set("isType", true);

		Assert.assertEquals(1.1,      swin.getDouble ("score" ), 0);
		Assert.assertEquals(COUNT,    swin.getInteger("count" ));
		Assert.assertEquals("foobar", swin.getString ("title" ));
		Assert.assertEquals("two",    swin.getString ("option"));
		Assert.assertEquals(ISTYPE,   swin.getBoolean("isType"));
	}

	@Test
	public void testMissing() {
		SWInstance swin = new SWInstance(DATA);
		Assert.assertEquals(null, swin.getDouble ("score" ));
		Assert.assertEquals(null, swin.getInteger("count" ));
		Assert.assertEquals(null, swin.getString ("title" ));
		Assert.assertEquals(null, swin.getString ("option"));
		Assert.assertEquals(null, swin.getBoolean("isType"));

		swin.set("score", 1.1);
		swin.set("count", 1);
		swin.set("title", "foobar");
		swin.set("option", "two");
		swin.set("isType", true);
		Assert.assertFalse(swin.isMissing("score" ));
		Assert.assertFalse(swin.isMissing("count" ));
		Assert.assertFalse(swin.isMissing("title" ));
		Assert.assertFalse(swin.isMissing("option"));
		Assert.assertFalse(swin.isMissing("isType"));

		swin.setMissing("score" );
		swin.setMissing("count" );
		swin.setMissing("title" );
		swin.setMissing("option");
		swin.setMissing("isType");
		Assert.assertEquals(null, swin.getDouble ("score" ));
		Assert.assertEquals(null, swin.getInteger("count" ));
		Assert.assertEquals(null, swin.getString ("title" ));
		Assert.assertEquals(null, swin.getString ("option"));
		Assert.assertEquals(null, swin.getBoolean("isType"));
	}

	@Test
	public void testIntegerException() {
		SWInstance swin = new SWInstance(DATA);
		swin.set("count", 1.1);
		Exception e = null;
		try {
			swin.getInteger("count");
		} catch (IllegalArgumentException iae) {
			e = iae;
		}
		Assert.assertTrue( e != null);
	}

	@Test
	public void testEmptyString() {
		SWInstance swin = new SWInstance(DATA);
		swin.set("title", "");
		Assert.assertEquals("", swin.getString("title"));
	}
}
