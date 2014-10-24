package com.shopwiki.classification.weka;

import java.io.*;
import java.util.*;

import weka.classifiers.*;
import weka.classifiers.functions.*;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.*;
import weka.core.converters.*;

/**
 * @owner rstewart
 * @buddy jeff
 */
public class WekaUtil {

	public static final boolean QUIET = Boolean.getBoolean("WEKA.QUIET");
	public static final boolean DEBUG = Boolean.getBoolean("WEKA.DEBUG");

	// INSTANCE METHODS - SEE com.shopwiki.classification.weka.SWInstance

	/*
    public static List<Attribute> getAttributes(Instance instance) {
        List<Attribute> attributes = new ArrayList<Attribute>();
        for (int i = 0; i < instance.numAttributes(); i++) {
            attributes.add(instance.attribute(i));
        }
        return attributes;
    }
	 */

	public static String toString(Instance instance, boolean skipFalse, boolean skip0) {
		return toString(instance, skipFalse, skip0, null);
	}

	public static String toString(Instance instance, boolean skipFalse, boolean skip0, Classifier classifier) {
		double[] coefficients = getCoefficients(classifier, instance.numAttributes());

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < instance.numAttributes(); i++) {
			Attribute attribute = instance.attribute(i);
			String value = instance.toString(i);
			if (skipFalse && value.equals(BooleanAttribute.FALSE)) {
				continue;
			}
			if (skip0 && value.equals("0")) {
				continue;
			}
			sb.append((i + 1) + ". " + attribute.name() + ": " + instance.toString(i));
			if (coefficients != null) {
				sb.append(" (" + MathUtil.round(-1 * coefficients[i], 2) + ")");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	// DATASET METHODS

	public static String getInfo(Instances data) {
		return "dataset \"" + data.relationName() + "\""
				+ " having " + Utils.comma(data.numInstances()) + " instances"
				+ " with " + Utils.comma(data.numAttributes()) + " attributes";
	}

	/*
    public static List<Attribute> getAttributes(Instances instances) {
        List<Attribute> attributes = new ArrayList<Attribute>();
        for (int i = 0; i < instances.numAttributes(); i++) {
            attributes.add(instances.attribute(i));
        }
        return attributes;
    }

    public static List<SWInstance> getSWInstances(Instances instances) {
        Attributes attributes = new Attributes(instances);
        List<SWInstance> swInstances = new ArrayList<SWInstance>();
        for (int i = 0; i < instances.numInstances(); i++) {
            SWInstance swInstance = new SWInstance(attributes, instances.instance(i));
            swInstances.add(swInstance);
        }
        return swInstances;
    }
	 */

	public static void checkForMissingValues(Instances dataset) {
		for (int a = 0; a < dataset.numAttributes(); a++) {
			int numMissing = 0;
			for (int i = 0; i < dataset.numInstances(); i++) {
				if (dataset.instance(i).isMissing(a)) {
					numMissing++;
				}
			}
			if (numMissing > 0 && ! QUIET) {
				System.out.println("WARNING: Attribute " + dataset.attribute(a) + " has " + Utils.comma(numMissing) + " missing values");
			}
		}
	}

	public static void computeStats(Instances dataset) {
		Attribute classAttribute = dataset.classAttribute();
		if (! classAttribute.isNominal()) {
			throw new IllegalArgumentException("Can only compute stats when class Attribue is Nominal!");
		}

		InstanceCounter<String> counts = new InstanceCounter<String>();
		for (int i = 0; i < dataset.numInstances(); i++) {
			SWInstance instance = new SWInstance(dataset.instance(i));
			counts.hit(instance.getString(classAttribute));
		}
		System.out.println(counts);

		System.out.printf("%25s", "ATTRIBUTE");
		for (String key : counts.keySet()) {
			UTF8.out.printf("%20", key);
		}
		System.out.println();

		for (int a = 0; a < dataset.numAttributes(); a++) {
			Attribute attribute = dataset.attribute(a);

			Map<String, Double> sums = new HashMap<String, Double>();
			for (String value : counts.keySet()) {
				sums.put(value, 0.0);
			}

			for (int i = 0; i < dataset.numInstances(); i++) {
				SWInstance instance = new SWInstance(dataset.instance(i));
				String classValue = instance.getString(classAttribute);
				double sum = sums.get(classValue);

				if (attribute.isNumeric()) {
					sum += instance.getDouble(attribute);
				} else if (BooleanAttribute.isBoolean(attribute)) {
					sum += instance.getBoolean(attribute) ? 1 : 0;
				}

				sums.put(classValue, sum);
			}

			UTF8.out.print(TextUtil.pad(attribute.name(), pad, false, ' '));
			for (String value : counts.keySet()) {
				double avg = sums.get(value) / counts.getNumberOfHits(value);
				System.out.print("\t" + MathUtil.round(avg, 3));
			}
			System.out.println();
		}
	}

	// DATASET IO METHODS

	public static void saveSerializedInstances(Instances instances, String filename) {
		if (! QUIET) {
			System.out.println("Saving " + getInfo(instances) + " to file: " + filename);
		}
		checkForMissingValues(instances);
		SerializedInstancesSaver saver = new SerializedInstancesSaver();
		saver.setInstances(instances);
		try {
			saver.setFile(new File(filename));
			saver.writeBatch();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Instances loadSerializedInstances(String filename) {
		SerializedInstancesLoader loader = new SerializedInstancesLoader();
		try {
			loader.setFile(new File(filename));
			Instances instances = loader.getDataSet();
			if (! QUIET) {
				System.out.println("Loaded " + getInfo(instances) + " from file: " + filename);
			}
			return instances;
		} catch (IOException e) {
			throw new RuntimeException("Could not load instances from file: " + filename, e);
		}
	}

	// MODEL IO METHODS

	/** Use Model.load() instead */
	@Deprecated
	public static Classifier loadClassifier(String classifierFilename) throws Exception { // This is NOT a RuntimeException to make sure it is caught & logged
		try {
			Classifier classifier = (Classifier) SerializationHelper.read(classifierFilename);
			if (! QUIET) {
				System.out.println("Loaded classifer (" + classifier.getClass().getName() + ") from file: " + classifierFilename);
			}
			return classifier;
		} catch (Exception e) {
			throw new Exception("Could not load classifer from file: " + classifierFilename, e);
		}
	}

	// CLASSIFIER METHODS

	public static Evaluation evalClassifier(Classifier classifier, Instances dataset, int numFolds) throws Exception {
		return evalClassifier(classifier, dataset, numFolds, null);
	}

	public static Evaluation evalClassifier(Classifier classifier, Instances dataset, int numFolds, String filename) throws Exception {
		if (! QUIET) {
			System.out.println("Doing " + numFolds + " folds of cross-validation"
					+ " for classifier (" + classifier.getClass().getName() + ")"
					+ " on " + getInfo(dataset) + "...");
		}
		Evaluation evaluation = new Evaluation(dataset);
		evaluation.crossValidateModel(classifier, dataset, numFolds, new Random());
		if (! QUIET) {
			System.out.println("Percent Correct: " + evaluation.pctCorrect());
		}
		if (filename != null) {
			saveEvaluation(evaluation, classifier, dataset, numFolds, filename);
		}
		return evaluation;
	}

	private static void saveEvaluation(Evaluation evaluation, Classifier classifier, Instances dataset, int numFolds, String filename) throws Exception {
		if (! QUIET) {
			System.out.println("Saving evaluation to file: " + filename);
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(filename);
			StringBuilder text = new StringBuilder(getInfo(dataset) + "\n");
			text.append("Classifier: " + classifier.getClass().getName() + "\n");
			text.append("numFolds: " + numFolds + "\n\n");
			text.append(evaluation.toSummaryString(false) + "\n");
			if (dataset.classAttribute().isNominal()) {
				text.append(evaluation.toClassDetailsString() + "\n");
				text.append(evaluation.toMatrixString() + "\n");
			}
			writer.write(text.toString());
		} finally {
			if (writer != null) {
				writer.close();	
			}
		}
	}

	private static double[] getCoefficients(Classifier classifier, int numAttributes) {
		if (classifier instanceof FilteredClassifier) {
			classifier = ((FilteredClassifier)classifier).getClassifier();
		}

		if (classifier instanceof Logistic) {
			double[][] coefficients = ((Logistic)classifier).coefficients();
			if (coefficients.length != numAttributes) {
				if (DEBUG) {
					System.err.println("coefficients.length: " + coefficients.length + " != numAttributes: " + numAttributes);
				}
				return null;
			}
			if (coefficients[0].length != 1) // must be binary class
			{
				if (DEBUG) {
					System.err.println("coefficients[0].length != 1: " + coefficients[0].length);
				}
				return null;
			}
			double[] ret = new double[coefficients.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = -1 * coefficients[i][0]; // multiply by -1 to get the 2nd class (e.g. TRUE rather than FALSE)
			}
			return ret;
		}

		if (classifier instanceof com.shopwiki.classification.weka.SimpleLogistic) {
			double[][] coefficients = ((com.shopwiki.classification.weka.SimpleLogistic) classifier).baseModel().coefficients();
			if (coefficients[0].length - 1 != numAttributes) {
				return null;
			}
			if (coefficients.length != 2) {
				return null;
			} // must be binary class
			double[] ret = new double[coefficients[0].length - 1];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = -1 * coefficients[0][i + 1]; // multiply by -1 to get the 2nd class (e.g. TRUE rather than FALSE)
			}
			return ret;
		}

		if (classifier instanceof LinearRegression) {
			return ((LinearRegression)classifier).coefficients();
		}

		return null;
	}

	private static int longestAttribute(Instances dataset) {
		int max = 0;
		for (int i = 0; i < dataset.numAttributes(); i++) {
			int len = dataset.attribute(i).name().length();
			max = Math.max(len, max);
		}
		return max;
	}

	public static String classifierInfo(Instances dataset, Classifier classifier) {
		double[] coefficients = getCoefficients(classifier, dataset.numAttributes());
		if (coefficients == null) {
			return classifier.toString();
		}
		Model model = new Model(dataset, classifier);
		StringBuilder sb = new StringBuilder(model.toString() + "\n");
		int longest = longestAttribute(dataset);
		String format = "%3s. %-"+longest+"s %5.2f %n";
		for (int i = 0; i < coefficients.length; i++) {
			Formatter formatter = new Formatter();
			Attribute attr = dataset.attribute(i);
			String name = attr != null ? attr.name() : null;
			formatter.format(format, i + 1, name, coefficients[i]);
			sb.append(formatter.toString());
			formatter.close();
		}
		return sb.toString();
	}
}
