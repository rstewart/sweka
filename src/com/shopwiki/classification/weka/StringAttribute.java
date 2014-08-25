package com.shopwiki.classification.weka;

import weka.core.*;

/**
 * Extends attribute to give a single (sane) constructor.
 *
 * @owner jeff
 * @buddy rstewart
 */
public class StringAttribute extends Attribute {

	private static final long serialVersionUID = -2317304045092026303L;

	public StringAttribute(String name) {
		super(name, VALUES);
	}

	private static final FastVector VALUES = null;
}
