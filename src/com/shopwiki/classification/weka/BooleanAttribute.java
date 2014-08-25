package com.shopwiki.classification.weka;

import weka.core.*;

/**
 * Extends attribute to give a single (sane) constructor.
 *
 * @owner jeff
 * @buddy rstewart
 */
public class BooleanAttribute extends Attribute {

	private static final long serialVersionUID = 8438415547742948772L;

	public BooleanAttribute(String name) {
		super(name, BOOLEANS);
	}

	public static final String FALSE = "false";
	public static final String TRUE = "true";

	private static final FastVector BOOLEANS = new FastVector(2); // TODO: Should this be numeric (0/1) instead of nominal ???
	static {
		BOOLEANS.addElement(FALSE);
		BOOLEANS.addElement(TRUE);
	}

	public static boolean isBoolean(Attribute attribute) {
		if (attribute.numValues() != 2) {
			return false;
		}

		return attribute.value(0).equals(FALSE)
				&& attribute.value(1).equals(TRUE);
	}
}
