package com.shopwiki.classification.weka;

import java.util.*;

import weka.core.*;

/**
 * Make Weka's FastVector more like a List, thus less annoying to use.
 *
 * @owner rstewart
 */
public class SWFastVector<E> extends FastVector implements Iterable<E> {

	private static final long serialVersionUID = -6497823345093913834L;

	public void add(E e) {
		addElement(e);
	}

	public void addAll(SWFastVector<E> elements) {
		for (E e : elements) {
			add(e);
		}
	}

	public E get(int i) {
		return (E) elementAt(i);
	}

	@Override
	public Iterator<E> iterator() {
		return new SWFastVectorIterator();
	}

	private class SWFastVectorIterator implements Iterator<E> {
		private int _i = -1;

		@Override
		public boolean hasNext() {
			return _i < size() - 1;
		}

		@Override
		public E next() {
			_i++;
			return get(_i);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (E e : this) {
			sb.append(e.toString() + "\n");
		}
		return sb.toString();
	}
}
