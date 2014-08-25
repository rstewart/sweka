package com.shopwiki.classification.weka;

import java.io.*;

import weka.classifiers.Classifier;
import weka.core.*;

/**
 * Convenience class to keep a copy of the Attributes header along with a Classifier
 *
 * @owner rstewart
 */
public class Model implements Serializable {

	private static final long serialVersionUID = -8817620172359678531L;

	private Instances _header;
	//private Attributes _attributes; // TODO: Just store Attributes instead of Instances ???

	private Classifier _classifier;

	public Model(Instances dataset, Classifier classifier) {
		//_header = new Instances(dataset, 0);
		_header = new Dataset(dataset);
		_classifier = classifier;
	}

	public Instances header() {
		return _header;
	}

	//public Attributes attributes() { return _attributes; }

	public Classifier classifier() {
		return _classifier;
	}

	@Override
	public String toString() {
		return "model (" + _classifier.getClass().getName() + ") for \"" + _header.relationName() + "\" with " + Utils.comma(_header.numAttributes()) + " attributes";
	}

	public String getInfo() {
		return WekaUtil.classifierInfo(_header, _classifier);
	}

	public void save(String filename) throws Exception {
		if (! WekaUtil.QUIET) {
			System.out.println("Saving " + this + " to file: " + filename);
		}
		try {
			OutputStream stream = null;
			try {
				stream = IOUtil.getBufferedOutputStream(filename, false);
				SerializationHelper.write(stream, this);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
		} catch (Exception e) {
			throw new Exception("Could not save model to file: " + filename, e);
		}
	}

	public static Model load(String filename) throws Exception { // This is NOT a RuntimeException to make sure it is caught & logged
		InputStream stream = null;
		try {
			stream = IOUtil.getBufferedInputStream(filename);
			Model model = (Model)SerializationHelper.read(stream);
			if (! WekaUtil.QUIET) {
				System.out.println("Loaded " + model + " from file: " + filename);
			}
			return model;
		} catch (Exception e) {
			throw new Exception("Could not load model from file: " + filename, e);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	public static Model train(Classifier classifier, Instances dataset) throws Exception {
		if (! WekaUtil.QUIET) {
			System.out.println("Training classifier (" + classifier.getClass().getName() + ") on " + WekaUtil.getInfo(dataset) + "...");
		}
		classifier.buildClassifier(dataset);
		//System.out.println(classifier);
		return new Model(dataset, classifier);
	}
}
