package mailclassifier.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.misc.Regexp;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.TextDirectoryLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.regex.*;

import mailclassifier.classification.*;
import mailclassifier.models.MailMessageDto;

public class WekaHelpers {
	public static Instances createDatasetFromTextDir(String directoryWithContent)
			throws Exception {
		TextDirectoryLoader loader = new TextDirectoryLoader();
		loader.setDirectory(new File(directoryWithContent));
		Instances dataRaw = loader.getDataSet();

		System.out.println("\n\nImported data:\n\n" + dataRaw);

		// apply the StringToWordVector
		// (see the source code of setOptions(String[]) method of the filter
		// if you want to know which command-line option corresponds to which
		// bean property)
		System.out.println("Apply StringToWordVector");
		StringToWordVector filter = new StringToWordVector();
		filter.setInputFormat(dataRaw);
		Instances dataFiltered = Filter.useFilter(dataRaw, filter);

		return dataFiltered;
	}

	public static void createArffWithTextVectorsFromTextDir(
			String dirWithContent, String destArffFile) throws Exception {
		Instances dataSet = WekaHelpers
				.createDatasetFromTextDir(dirWithContent);
		ArffSaver saver = new ArffSaver();
		saver.setInstances(dataSet);
		saver.setFile(new File(destArffFile));
		saver.writeBatch();
	}

}
