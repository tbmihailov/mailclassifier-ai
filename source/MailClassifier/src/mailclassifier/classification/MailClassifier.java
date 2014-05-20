package mailclassifier.classification;

import java.io.File;
import java.io.IOException;

import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class MailClassifier {

	private Instances _dataSet = null;
	private StringToWordVector _filterStrToWordVector2 = new StringToWordVector();
	private StringToNominal _filterStrToNominal1 = new StringToNominal();

	private SMO _classifier;
	String _accountName = null;
	String _storagePath = null;

	// this is true when the dataset is initially created or loaded
	// or new item is added to the instances set
	private Boolean _isClassifierUpToDate = false;
	
	// Attributes
	FastVector attributes = null;
	int classIndex = 0;
	
	public MailClassifier(String storagePath, String accountName)
			throws Exception {
		_accountName = accountName;
		_storagePath = storagePath;

		_filterStrToWordVector2 = new StringToWordVector();
		_filterStrToWordVector2.setOptions(weka.core.Utils.splitOptions("-L"));
		
		_filterStrToNominal1 = new StringToNominal();
		_classifier = new SMO();
		
		attributes = new FastVector(2);
		attributes.addElement(new Attribute("Data", (FastVector) null));
		attributes.addElement(new Attribute("Folder", (FastVector) null));
	}

	public void loadOrCreateDataSet() throws Exception {
		loadDataSet(_storagePath, _accountName);
	}

	public void loadDataSet(String storagePath, String accountName)
			throws Exception {
		String dataSetPathForProfile = storagePath + "\\" + accountName
				+ ".arff";

		loadOrCreateDataSet(dataSetPathForProfile);
	}

	public void loadOrCreateDataSet(String dataSetPathForProfile)
			throws Exception {

		File dataSourceFile = new File(dataSetPathForProfile);
		if (dataSourceFile.exists()) {
			// load .arff file for the specified profile
			DataSource source = new DataSource(dataSetPathForProfile);
			Instances data = source.getDataSet();

			// set class index
			_dataSet = data;
			_dataSet.setClassIndex(_dataSet.numAttributes() - 1);
			// update method and classifier
			updateFilterAndClassifier();
		} else {
			createNewDataSet();
		}
	}

	private void updateFilterAndClassifier() throws Exception {
		// specify the format of the filter

		_filterStrToNominal1.setInputFormat(_dataSet);
		_filterStrToNominal1.setOptions(weka.core.Utils.splitOptions("-R last"));
		Instances filteredData = Filter.useFilter(_dataSet, _filterStrToNominal1);

		_filterStrToWordVector2.setInputFormat(filteredData);
		// _filter.setOptions(weka.core.Utils.splitOptions("-R 2-last"));

		filteredData = Filter.useFilter(filteredData, _filterStrToWordVector2);

		//filteredData.setClassIndex(0);

		// Rebuild classifier.
		_classifier = new SMO();
		_classifier
				.setOptions(weka.core.Utils
						.splitOptions("-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
		_classifier.buildClassifier(filteredData);

		setIsClassifierUpToDate(true);
	}

	private void createNewDataSet() throws Exception {
		// TODO Check what capacity means - initial capacity
		_dataSet = new Instances(_accountName, attributes, 10);
		_dataSet.setClassIndex(_dataSet.numAttributes()-1);
	}

	public void trainWithMessage(String msgData, String mailFolder) {
		Instance instance = createInstanceFromMessageData(msgData, _dataSet);
		instance.setClassValue(mailFolder);

		_dataSet.add(instance);
	}

	public String classifyMessage(String message) throws Exception {

		if (_dataSet.numInstances() == 0) {
			throw new Exception("Cannot classify message on empty set.");
		}

		if (!getIsClassifierUpToDate()) {
			updateFilterAndClassifier();
		}

		// Create dummy set and classify instance - FROM SAMPLES
		Instances dummySet = _dataSet.stringFreeStructure();
		Instance instance = createInstanceFromMessageData(message, dummySet);

		// Filter instance
		Instance filteredInstance = null;
		//_filterStrToNominal1.input(instance);
		//filteredInstance = _filterStrToNominal1.output();
		_filterStrToWordVector2.input(instance);
		filteredInstance = _filterStrToWordVector2.output();

		// Predict class value
		double predicted = _classifier.classifyInstance(filteredInstance);

		// Get class
		String messageFolder = _dataSet.classAttribute().value((int) predicted);
		
		return messageFolder;
	}

	private Instance createInstanceFromMessageData(String msgData,
			Instances data) {
		Instance instance = new Instance(2);
			
		// build instance with the specified attributes
		Attribute dataAttr = data.attribute("Data");
		instance.setValue(dataAttr, dataAttr.addStringValue(msgData));

		instance.setDataset(data);
		return instance;
	}

	public String classifyMessageToFolder(String msgData) throws Exception {
		return classifyMessage(msgData);
	}

	public void saveDataSet(String storagePath, String profileName)
			throws Exception {
		String filePath = storagePath + "\\" + profileName + ".arff";
		saveDataSet(filePath);
	}

	public void saveDataSet(String filePath) throws Exception {

		// StringToWordVector filter = new StringToWordVector();
		// filter.setInputFormat(_dataSet);
		// Instances dataSet = Filter.useFilter(_dataSet, filter);

		Instances dataSet = _dataSet;
		
		
		// Export instances to arff file
		ArffSaver saver = new ArffSaver();
		saver.setInstances(dataSet);

		saver.setFile(new File(filePath));
		saver.writeBatch();
		System.out.println("Instances saved to " + filePath);
	}

	public int getDataSetLength() {
		// TODO Auto-generated method stub
		if (_dataSet != null) {
			return _dataSet.numInstances();
		}
		return 0;
	}

	public void saveDataSet() throws Exception {
		saveDataSet(_storagePath, _accountName);
	}

	Boolean getIsClassifierUpToDate() {
		return _isClassifierUpToDate;
	}

	void setIsClassifierUpToDate(Boolean _isClassifierUpToDate) {
		this._isClassifierUpToDate = _isClassifierUpToDate;
	}

	public Boolean doesArffExistsForCurrentAccount() {
		String dataSetPathForProfile = _storagePath + "\\" + _accountName
				+ ".arff";
		File file = new File(dataSetPathForProfile);
		return file.exists();
	}

}
