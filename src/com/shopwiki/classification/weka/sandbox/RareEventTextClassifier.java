package com.shopwiki.classification.weka.sandbox;

import weka.classifiers.*;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class RareEventTextClassifier extends RandomizableIteratedSingleClassifierEnhancer {

	private static final long serialVersionUID = 4167173014232453093L;

	private int _spreadSubsampleMaxCount = 0;

	public void setSpreadSubsampleMaxCount(int maxCount) {
		_spreadSubsampleMaxCount = maxCount;
	}

	public int getSpreadSubsampleMaxCount() {
		return _spreadSubsampleMaxCount;
	}

	@Override
	public void buildClassifier(Instances data) throws Exception {

		if (! data.classAttribute().isNominal()) {
			throw new Exception("Class Attribute must be nominal!");
		}

		if (data.classAttribute().numValues() != 2) {
			throw new Exception("Class Attribute must be binary!");
		}

		super.buildClassifier(data);

		for (int i = 0; i < getNumIterations(); i++) {
			if (i > 0) {
				data = getPositivesAndFalsePositives(data, m_Classifiers[i-1]);
			}

			SpreadSubsample sampler = new SpreadSubsample();
			//sampler.setRandomSeed(getSeed());
			sampler.setMaxCount(_spreadSubsampleMaxCount);
			sampler.setDistributionSpread(1);
			Instances sample = Filter.useFilter(data, sampler);

			StringToWordVector wordFilter = new StringToWordVector();
			wordFilter.setWordsToKeep(100);
			// TODO: Lowercase !!!
			sample = Filter.useFilter(sample, wordFilter);

			FilteredClassifier filteredClassifier = new FilteredClassifier();
			filteredClassifier.setClassifier(m_Classifiers[i]);
			filteredClassifier.setFilter(wordFilter);
			filteredClassifier.buildClassifier(sample);
			m_Classifiers[i] = filteredClassifier;
		}
	}

	private Instances getPositivesAndFalsePositives(Instances dataIn, Classifier classifier) throws Exception {
		Attribute classAttribute = dataIn.classAttribute();
		Instances dataOut = new Instances(dataIn, 0);
		for (int i = 0; i < dataIn.numInstances(); i++) {
			Instance instance = dataIn.instance(i);
			if (instance.value(classAttribute) == _true) {
				dataOut.add(instance);
			} else if (classifier.classifyInstance(instance) == _true) {
				dataOut.add(instance);
			}
		}
		return dataOut;
	}

	private int _false = 1;
	private int _true  = 1 - _false;

	@Override
	public double classifyInstance(Instance instance) throws Exception {
		for (int i = 0; i < getNumIterations(); i++) {
			if (m_Classifiers[i].classifyInstance(instance) == _false) {
				return _false;
			} 
		}
		return _true;
	}

	/*
    private static Dataset makeDataset(DatasetsMaker datasetsMaker, BrowseNode browseNode) throws Exception {

        Attributes attributes = new Attributes();
        attributes.add(new StringAttribute("title"));
        Attribute classAttribute = new BooleanAttribute("inBrowseNode"); 
        attributes.add(classAttribute);
        Dataset dataset = new Dataset("BrowseNode " + browseNode, attributes);
        dataset.setClass(classAttribute);

        for (Product product : datasetsMaker.getProducts(browseNode.id(), 100)) {
            dataset.add(makeInstance(dataset, product));
        }

        for (Product product : datasetsMaker.getProducts(Base.RAND_BROWSE_NODE_ID, 1000)) {
            if (product.browseNodes.contains(browseNode)) { continue; }
            dataset.add(makeInstance(dataset, product));
        }
        return dataset;
    }

    private static Instance makeInstance(Instances dataset, Product product) {
        SWInstance instance = new SWInstance(dataset);
        String title = StringUtil.join(" ", product.titleWords);
        instance.set("title", title);
        return instance.getInstance();
    }

    public static void main(String[] jargs) throws Exception {
        Args args = new Args(jargs);
        long browseNodeId = args.getLong("bn", -1);
      //ClassifyConf base = ClassifyConf.create(args);
      //BrowseNode browseNode = base.browseNodeManager().get(browseNodeId);
      //DatasetsMaker datasetsMaker = new DatasetsMaker(args);
      //Dataset dataset = makeDataset(datasetsMaker, browseNode);
      //WekaUtil.saveSerializedInstances(dataset, "/data/test/browseNode" + browseNodeId + ".bsi");
    }
	 */
}
