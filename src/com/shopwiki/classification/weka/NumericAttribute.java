package com.shopwiki.classification.weka;

import weka.core.Attribute;

/**
 * Extends attribute to give a single (sane) constructor.
 *
 * @owner jeff
 * @buddy rstewart
 */
public class NumericAttribute extends Attribute {

	private static final long serialVersionUID = 8602858879588506475L;

	public NumericAttribute(String name) {
		super(name);
	}

	public static int toInt(double d) {
		int i = (int)d;
		if (d != (double)i) {
			throw new IllegalArgumentException("Not an int: " + d);
		}
		return i;
	}

	public static long toLong(double d) {
		long i = (long)d;
		if (d != (double)i) {
			throw new IllegalArgumentException("Not a long: " + d);
		}
		return i;
	}
}
