package com.shopwiki.classification.weka;

import java.util.Collection;

import weka.core.*;

/**
 * Extends attribute to give a single (sane) constructor.
 *
 * @owner jeff
 * @buddy rstewart
 */
public class NominalAttribute<T> extends Attribute {

	private static final long serialVersionUID = -9087298378087103367L;

	public NominalAttribute(String name, T[] values) {
		super(name, getFastVector(values));
	}

	public NominalAttribute(String name, Collection<T> values) {
		super(name, getFastVector(values.toArray()));
	}

	private static FastVector getFastVector(Object[] values) {
		FastVector ret = new FastVector();
		for (Object value : values) {
			ret.addElement(value.toString());
		}
		return ret;
	}

	public String[] getValues() {
		return getValues(this);
	}

	public static String[] getValues(Attribute attribute) {
		if (! attribute.isNominal()) {
			return null;
		}
		String[] values = new String[attribute.numValues()];
		for (int i = 0; i < values.length; i++) {
			values[i] = attribute.value(i);
		}
		return values;
	}
}
