package com.shopwiki.classification.weka;

import weka.classifiers.functions.SimpleLinearRegression;

/**
 * This class exists because the weka version's getCoefficients() method is protected
 *
 * @owner rstewart
 */
public class LogisticBase extends weka.classifiers.trees.lmt.LogisticBase {

	private static final long serialVersionUID = -6526398433602194847L;

	public LogisticBase(int numBoostingIterations, boolean useCrossValidation, boolean errorOnProbabilities) {
		super(numBoostingIterations, useCrossValidation, errorOnProbabilities);
	}

	public double[][] coefficients() { return getCoefficients(); }

	public SimpleLinearRegression[][] getRegressions() { return m_regressions; }
}
