package bulstem;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class StemmerTest {

	@Test
	public void testStem() throws Exception {
		List<String> wordsToTest = new ArrayList<String>();

        wordsToTest.add("първи");
        wordsToTest.add("случай");
        wordsToTest.add("българия");
        wordsToTest.add("здравеопазването");
        wordsToTest.add("точната");
        wordsToTest.add("продължителен");
        wordsToTest.add("здравна");
        wordsToTest.add("известен");

        Stemmer stemmer = new Stemmer();

        //level1
        stemmer.loadStemmingRules("D:\\Programming\\Java\\ai-project\\MailClassifier\\BulStem\\res\\Rules\\stem_rules_context_1_utf8.txt");
        System.out.println("level1 test");
        for(Iterator<String> i = wordsToTest.iterator(); i.hasNext(); ) {
            String item = i.next();
            String stem = stemmer.stem(item);
            System.out.println(String.format("wordsToTest.Add(\"%s\", \"%s\");",item,stem));
        }

        //level2
        stemmer.loadStemmingRules("D:\\Programming\\Java\\ai-project\\MailClassifier\\BulStem\\res\\Rules\\stem_rules_context_2_utf8.txt");
        System.out.println("level2 test");
        for(Iterator<String> i = wordsToTest.iterator(); i.hasNext(); ) {
            String item = i.next();
            String stem = stemmer.stem(item);
            System.out.println(String.format("wordsToTest.Add(\"%s\", \"%s\");",item,stem));
        }

        //level3
        stemmer.loadStemmingRules("D:\\Programming\\Java\\ai-project\\MailClassifier\\BulStem\\res\\Rules\\stem_rules_context_3_utf8.txt");
        System.out.println("level3 test");
        for(Iterator<String> i = wordsToTest.iterator(); i.hasNext(); ) {
            String item = i.next();
            String stem = stemmer.stem(item);
            System.out.println(String.format("wordsToTest.Add(\"%s\", \"%s\");",item,stem));
        }
	}

}
