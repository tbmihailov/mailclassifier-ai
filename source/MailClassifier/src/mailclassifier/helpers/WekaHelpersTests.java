package mailclassifier.helpers;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class WekaHelpersTests {

	//@Test 
	public void enron_test_convert_kaminski() throws Exception{
		String sourceDir = "D:\\Programming\\Research\\MailClassification\\enron_tests\\kaminski-v";
		String outArff = "D:\\Programming\\Research\\MailClassification\\enron_tests\\kaminski-v.arff";
		WekaHelpers.createArffWithTextVectorsFromTextDir(sourceDir,outArff);
	}
	
	//@Test 
	public void enron_test_convert_lockay() throws Exception{
		String sourceDir = "D:\\Programming\\Research\\MailClassification\\enron_tests\\lokay-m";
		String outArff = "D:\\Programming\\Research\\MailClassification\\enron_tests\\lokay-m.arff";
		WekaHelpers.createArffWithTextVectorsFromTextDir(sourceDir,outArff);
	}
	
	//@Test 
	public void enron_test_convert_williams() throws Exception{
		String sourceDir = "D:\\Programming\\Research\\MailClassification\\enron_tests\\williams-w3";
		String outArff = "D:\\Programming\\Research\\MailClassification\\enron_tests\\williams-w3.arff";
		WekaHelpers.createArffWithTextVectorsFromTextDir(sourceDir,outArff);
	}
	
	
	@SuppressWarnings("deprecation")
	//@Test
	public void test_folderToStringTokens() throws Exception {
		Instances instances = WekaHelpers.createDatasetFromTextDir("D:\\Programming\\Research\\Weka\\Categorization-Sample\\text_example");
		System.out.println(instances);
		Assert.assertTrue(instances.instance(0) != null);
	}
	
	//@Test 
	public void test_dir_to_string_vector() throws Exception{
		String sourceDir = "D:\\Programming\\Research\\Weka\\Categorization-Sample\\text_example";
		String outArff = "D:\\Programming\\Research\\Weka\\Categorization-Sample\\text_example\\test_ex.arff";
		WekaHelpers.createArffWithTextVectorsFromTextDir(sourceDir,outArff);
	}
	
	// /http://stackoverflow.com/questions/19192823/words-to-keep-attribute-in-stringtowordvector-filter-in-weka
	public void test_StringToWordVector() throws Exception {
		String html = "repeat repeat repeat";

		Attribute input = new Attribute("html", (FastVector) null);

		FastVector inputVec = new FastVector();
		inputVec.addElement(input);

		Instances htmlInst = new Instances("html", inputVec, 1);
		htmlInst.add(new Instance(1));
		htmlInst.instance(0).setValue(0, html);

		StringToWordVector filter = new StringToWordVector();
		filter.setUseStoplist(true);
		filter.setOutputWordCounts(true);

		filter.setInputFormat(htmlInst);
		Instances dataFiltered = Filter.useFilter(htmlInst, filter);

		Instance last = dataFiltered.lastInstance();
		System.out.println(last);
	}

	// http://ianma.wordpress.com/2010/01/16/weka-with-java-eclipse-getting-started/
	public void gettingStarted() throws Exception {
		// Declare two numeric attributes
		Attribute Attribute1 = new Attribute("firstNumeric");
		Attribute Attribute2 = new Attribute("secondNumeric");

		// Declare a nominal attribute along with its values
		FastVector fvNominalVal = new FastVector(3);
		fvNominalVal.addElement("blue");
		fvNominalVal.addElement("gray");
		fvNominalVal.addElement("black");
		Attribute Attribute3 = new Attribute("aNominal", fvNominalVal);

		// Declare the class attribute along with its values
		FastVector fvClassVal = new FastVector(2);
		fvClassVal.addElement("positive");
		fvClassVal.addElement("negative");
		Attribute ClassAttribute = new Attribute("theClass", fvClassVal);

		// Declare the feature vector
		FastVector fvWekaAttributes = new FastVector(4);
		fvWekaAttributes.addElement(Attribute1);
		fvWekaAttributes.addElement(Attribute2);
		fvWekaAttributes.addElement(Attribute3);
		fvWekaAttributes.addElement(ClassAttribute);

		// Create an empty training set
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, 10);

		// Set class index
		isTrainingSet.setClassIndex(3);

		// Create the instance
		Instance iExample = new Instance(4);
		iExample.setValue((Attribute) fvWekaAttributes.elementAt(0), 1.0);
		iExample.setValue((Attribute) fvWekaAttributes.elementAt(1), 0.5);
		iExample.setValue((Attribute) fvWekaAttributes.elementAt(2), "gray");
		iExample.setValue((Attribute) fvWekaAttributes.elementAt(3), "positive");

		// add the instance
		isTrainingSet.add(iExample);
		Classifier cModel = (Classifier) new NaiveBayes();
		cModel.buildClassifier(isTrainingSet);

		// Test the model
		Evaluation eTest = new Evaluation(isTrainingSet);
		eTest.evaluateModel(cModel, isTrainingSet);

		// Print the result à la Weka explorer:
		String strSummary = eTest.toSummaryString();
		System.out.println(strSummary);

		// Get the confusion matrix
		double[][] cmMatrix = eTest.confusionMatrix();
		for (int row_i = 0; row_i < cmMatrix.length; row_i++) {
			for (int col_i = 0; col_i < cmMatrix.length; col_i++) {
				System.out.print(cmMatrix[row_i][col_i]);
				System.out.print("|");
			}
			System.out.println();
		}

	}
}
