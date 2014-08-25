package com.shopwiki.classification.weka;

import weka.classifiers.Classifier;
import weka.core.*;

/**
 * Class for setting the values of a weka.core.Instance object.
 *
 * Because (according to the Weka API), Instance.setValue() "Performs a deep copy of the vector of attribute values before the value is set,
 * so if you are planning on calling setValue many times it may be faster to create a new instance using toDoubleArray."
 *
 * But working with a double[] and keeping track of attribute & value indices is awkward, so this class should help.
 *
 * "All values (numeric, date, nominal, string or relational) are internally stored as floating-point numbers.
 * If an attribute is nominal (or a string or relational),
 * the stored value is the index of the corresponding nominal (or string or relational) value in the attribute's definition.
 * We have chosen this approach in favor of a more elegant object-oriented approach because it is much faster."
 *
 * @owner rstewart
 */
public class SWInstance { // TODO: Maybe InstanceBuilder would be a better name ???

	//private final Attributes _attributes; // TODO: Just use this instead of _dataset ???
	private final Instances _dataset;
	private double[] _values;

	// CONSTRUCTORS

	private SWInstance(int numAttributes, Attributes attributes, Instances dataset) {
		//_attributes = attributes;
		_dataset = dataset;
		_values = new double[numAttributes];

		for (int a = 0; a < _values.length; a++) {
			_values[a] = Instance.missingValue();
		}
	}

	public SWInstance(Instances dataset) {
		this(dataset.numAttributes(), null, dataset);
	}

	//  public SWInstance(Attributes attributes) {
	//      this(attributes.size(), attributes, null);
	//  }

	public SWInstance(Instance instance) {
		//_attributes = null;
		_dataset = instance.dataset();
		_values = instance.toDoubleArray();
	}

	// CORE PUBLIC METHODS

	public Instance getInstance() { // TODO: Should this be renamed toInstance() or build() ???
		Instance instance = new Instance(1.0, _values);
		instance.setDataset(_dataset);
		return instance;
	}

	public Attribute getAttribute(int i) {
		//if (_attributes != null) { return _attributes.get(i); }
		return _dataset.attribute(i);
	}

	public Attribute getAttribute(String name) {
		//if (_attributes != null) { return _attributes.get(name); }
		return _dataset.attribute(name);
	}

	// PRIVATE METHODS

	private Attribute _getAttribute(String name) {
		Attribute attribute = getAttribute(name);
		if (attribute == null) {
			throw new IllegalArgumentException("Dataset does not have Attribute named \"" + name + "\"");
		}
		return attribute;
	}

	private static int _getIndex(Attribute attribute) {
		int index = attribute.index();
		if (index < 0) {
			throw new IllegalArgumentException("Dataset does not have Attribute: " + attribute);
		}
		return index;
	}

	private static int _getValueIndex(Attribute attribute, String value) {
		int index = attribute.indexOfValue(value);
		if (index > -1) {
			return index;
		}
		if (attribute.isString()) {
			return attribute.addStringValue(value);
		}
		throw new IllegalArgumentException("Invalid value \"" + value + "\" for Attribute: " + attribute);
	}

	private static String _getValue(Attribute attribute, int index) {
		String value = attribute.value(index);
		//if (value.isEmpty()) { throw new RuntimeException("SHOULD NEVER HAPPEN! - Invalid index \"" + index + "\" for Attribute: " + attribute); }
		return value;
	}

	// SETTERS

	public void set(Attribute attribute, double value) {
		if (! attribute.isNumeric()) {
			throw new IllegalArgumentException("Attribute not numeric: " + attribute);
		}
		_values[_getIndex(attribute)] = value;
	}

	public void set(Attribute attribute, String value) {
		if (! attribute.isNominal() && ! attribute.isString()) {
			throw new IllegalArgumentException("Attribute not nominal or string: " + attribute);
		}
		_values[_getIndex(attribute)] = _getValueIndex(attribute, value);
	}

	public void set(Attribute attribute, boolean value) {
		if (! BooleanAttribute.isBoolean(attribute)) {
			throw new IllegalArgumentException("Attribute not boolean: " + attribute);
		}
		set(attribute, String.valueOf(value));
	}

	public void set(String name, double value) {
		set(_getAttribute(name), value);
	}

	public void set(String name, String value) {
		set(_getAttribute(name), value);
	}

	public void set(String name, boolean value) {
		set(_getAttribute(name), value);
	}

	// GETTERS
	// TODO: Change the getters to use weka.core.Instance ??? Or just not have getters at all ???

	public Double getDouble(Attribute attribute) {
		if (! attribute.isNumeric()) {
			throw new IllegalArgumentException("Attribute not numeric: " + attribute);
		}

		if (isMissing(attribute)) {
			return null;
		}

		return _values[_getIndex(attribute)];
	}

	public Integer getInteger(Attribute attribute) {
		Double value = getDouble(attribute);
		if (value == null) {
			return null;
		}
		return NumericAttribute.toInt(value);
	}

	public String getString(Attribute attribute) {
		if (! attribute.isNominal() && ! attribute.isString()) {
			throw new IllegalArgumentException("Attribute not nominal or string: " + attribute);
		}

		if (isMissing(attribute)) {
			return null;
		}

		int nominalIndex = (int)_values[_getIndex(attribute)];
		return _getValue(attribute, nominalIndex);
	}

	public Boolean getBoolean(Attribute attribute) {
		if (! BooleanAttribute.isBoolean(attribute)) {
			throw new IllegalArgumentException("Attribute not boolean: " + attribute);
		}

		if (isMissing(attribute)) {
			return null;
		}

		return getString(attribute).equals(BooleanAttribute.TRUE);
	}

	public Double  getDouble (String name) {
		return getDouble(_getAttribute(name));
	}

	public Integer getInteger(String name) {
		return getInteger(_getAttribute(name));
	}

	public String  getString (String name) {
		return getString(_getAttribute(name));
	}

	public Boolean getBoolean(String name) {
		return getBoolean(_getAttribute(name));
	}

	// OTHER METHODS

	public void increment(Attribute attribute, double amount) {
		set(attribute, isMissing(attribute) ? 0.0 : getDouble (attribute) + amount);
	}

	public void increment(String name, double amount) {
		increment(_getAttribute(name), amount);
	}

	public void setMissing(Attribute attribute) {
		_values[_getIndex(attribute)] = Instance.missingValue();
	}

	public void setMissing(String name) {
		setMissing(_getAttribute(name));
	}

	public boolean isMissing(Attribute attribute) {
		return Double.isNaN(_values[_getIndex(attribute)]); // == Instance.missingValue() doesn't work!
	}

	public boolean isMissing(String name) {
		return isMissing (_getAttribute(name));
	}

	@Override
	public String toString() {
		return toString(false, false);
	}

	public String toString(boolean skipFalse, boolean skip0) {
		return WekaUtil.toString(this.getInstance(), skipFalse, skip0);
	}

	public String toString(boolean skipFalse, boolean skip0, Classifier classifier) {
		return WekaUtil.toString(this.getInstance(), skipFalse, skip0, classifier);
	}
}
