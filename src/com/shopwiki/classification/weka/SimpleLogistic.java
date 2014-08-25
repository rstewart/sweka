package com.shopwiki.classification.weka;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.*;

/**
 * This class exists because the weka version doesn't call m_boostedModel.cleanup() after building the classifier.
 * Doing so makes the serialized classifier MUCH smaller!
 *
 * @owner rstewart
 */
public class SimpleLogistic extends weka.classifiers.functions.SimpleLogistic {

	private static final long serialVersionUID = -8635189189718005763L;

	public SimpleLogistic() {
		super();
	}

	public SimpleLogistic(int numBoostingIterations, boolean useCrossValidation, boolean errorOnProbabilities) {
		super(numBoostingIterations, useCrossValidation, errorOnProbabilities);
	}

	public com.shopwiki.classification.weka.LogisticBase baseModel() {
		return (com.shopwiki.classification.weka.LogisticBase)m_boostedModel;
	}

	/**
	 * Overridden so I can use my LogisticBase AND so I can call m_boostedModel.cleanup() !!!
	 */
	@Override
	public void buildClassifier(Instances data) throws Exception {

		// can classifier handle the data?
		getCapabilities().testWithFail(data);

		// remove instances with missing class
		data = new Instances(data);
		data.deleteWithMissingClass();

		//replace missing values
		m_ReplaceMissingValues = new ReplaceMissingValues();
		m_ReplaceMissingValues.setInputFormat(data);
		data = Filter.useFilter(data, m_ReplaceMissingValues);

		//convert nominal attributes
		m_NominalToBinary = new NominalToBinary();
		m_NominalToBinary.setInputFormat(data);
		data = Filter.useFilter(data, m_NominalToBinary);

		//create actual logistic model
		m_boostedModel = new com.shopwiki.classification.weka.LogisticBase(m_numBoostingIterations, m_useCrossValidation, m_errorOnProbabilities);
		m_boostedModel.setMaxIterations(m_maxBoostingIterations);
		m_boostedModel.setHeuristicStop(m_heuristicStop);
		m_boostedModel.setWeightTrimBeta(m_weightTrimBeta);
		m_boostedModel.setUseAIC(getUseAIC());

		//build logistic model
		m_boostedModel.buildClassifier(data);
		m_boostedModel.cleanup(); // FREE UP MEMORY SO SERILIZED MODEL CAN BE SMALLER !!!
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "LogitBoost iterations performed: " + getNumRegressions() + "\n";
	}
}
