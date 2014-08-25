package com.shopwiki.classification.weka;

import java.util.*;

import weka.core.*;

/**
 * @owner rstewart
 * @buddy jeff
 */
public class Attributes extends SWFastVector<Attribute> { // TODO: Now that SWInstance doesn't use this, is it needed at all ???

	private static final long serialVersionUID = 8859278212308531381L;

	private final Map<String, Attribute> _namesToAttributes = new HashMap<String, Attribute>(); // TODO: Should this be a WeakHashMap ???  Is it needed at all ???

	public Attributes() {
		super();
	}

	public Attributes(Instances instances) {
		this();
		for (int i = 0; i < instances.numAttributes(); i++) {
			add(instances.attribute(i));
		}
	}

	@Override
	public void add(Attribute attribute) {
		String name = attribute.name();
		if (_namesToAttributes.containsKey(name)) {
			throw new RuntimeException("Attribute named \"" + name + "\" already exists!");
		}
		super.add(attribute);
		_namesToAttributes.put(name, attribute);
	}

	public Attribute get(String name) {
		//if (! _namesToAttributes.containsKey(name)) { throw new RuntimeException("Attribute named \"" + name + "\" does not exist!"); }
		return _namesToAttributes.get(name);
	}
}
