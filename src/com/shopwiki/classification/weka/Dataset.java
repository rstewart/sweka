package com.shopwiki.classification.weka;

import java.util.Iterator;

import weka.core.*;

/**
 * The main purpose of this class (at the moment)
 * is to override the weka.core.Instances implementation of the attribute(name) method
 * because it is slow
 * --it loops over the entire FastVector of attributes,
 * doing a string comparison every time.
 * The implementation here uses the Attributes class,
 * which looks up an attribute by name using a HashMap.
 *
 * @owner rstewart
 * @buddy jeff
 */
public class Dataset extends Instances implements Iterable<Instance> {

	private static final long serialVersionUID = -2497649575206089893L;

	private final Attributes _attributes; // TODO: Instances has method that modify m_attributes!

	public Dataset(String name, Attributes attributes) {
		super(name, attributes, 0);
		_attributes = attributes;
	}

	public Dataset(Instances instances) {
		this(instances.relationName(), new Attributes(instances));
		setClassIndex(instances.classIndex());
	}

	public Attributes getAttributes() {
		return _attributes;
	}

	@Override
	public Attribute attribute(String name) {
		return _attributes.get(name);
	}

	@Override
	public Iterator<Instance> iterator() {
		return new InstanceIterator();
	}

	private class InstanceIterator implements Iterator<Instance> {
		private int _i = -1;

		@Override
		public boolean hasNext() {
			return _i < numInstances() - 1;
		}

		@Override
		public Instance next() {
			_i++;
			return instance(_i);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	// WekaUtil CONVENICE METHODS

	public String getInfo() {
		return WekaUtil.getInfo(this);
	}

	public void save(String path) {
		WekaUtil.saveSerializedInstances(this, path);
	}

	public void checkForMissingValues() {
		WekaUtil.checkForMissingValues(this);
	}

	public void printStats() {
		WekaUtil.computeStats(this);
	}
}
