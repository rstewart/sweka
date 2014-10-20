package com.shopwiki.classification.weka.sandbox;

import weka.core.*;

public abstract class SWAttribute<T> extends Attribute
{
    private static final long serialVersionUID = 4089716312195740933L;

    private final Object _defaultValue;
    private final double _defaultDouble;

    public SWAttribute(String attributeName, FastVector attributeValues, T defaultValue)
    {
        super(attributeName, attributeValues);
        _defaultValue  = defaultValue;
        _defaultDouble = (defaultValue == null) ? Instance.missingValue() : getValueIndex(defaultValue.toString());
    }

    public SWAttribute(String attributeName, FastVector attributeValues) { this(attributeName, attributeValues, null); }

    public SWAttribute(String attributeName, Number defaultValue)
    {
        super(attributeName);
        _defaultValue  = defaultValue;
        _defaultDouble = (defaultValue == null) ? Instance.missingValue() : defaultValue.doubleValue();
    }

    public SWAttribute(String attributeName) { this(attributeName, (Number)null); }

    public T defaultValue() { return (T)_defaultValue; }
    
    public double defaultDouble() { return _defaultDouble; }
    
  //public boolean hasDefault() { return _defaultValue != null; }
    public boolean hasDefault() { return ! Double.isNaN(_defaultDouble); }
    
    public int getValueIndex(String value) { return getValueIndex(this, value); }
    
    public static int getValueIndex(Attribute attribute, String value)
    {
        int index = attribute.indexOfValue(value);
        if (index > -1) { return index; }
        if (attribute.isString()) { return attribute.addStringValue(value); }
        throw new IllegalArgumentException("Invalid value \"" + value + "\" for Attribute: " + attribute);
    }
}
