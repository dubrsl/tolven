package test.org.tolven.app;

import java.io.PrintStream;

import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuDataWord;

import junit.framework.TestCase;

public class WordIndexTests extends TestCase {

	public void printWordList( MenuData md, PrintStream out, String title ) {
		out.format("\n%s\n", title);
		for ( MenuDataWord word : md.getWords()) {
			out.format("Word: %s, position: %d, Field: %s, Language %s\n", 
				word.getWord(), word.getPosition(), word.getField(), word.getLanguage());
		}
	}
	
	public void testAddPhrase1() {
		MenuData md = new MenuData();
		md.addPhrase("this is a simple phrase", "name", "en_US");
		printWordList( md, System.out, "Simple phrase" );
	}
	
	public void testAddPhrase2() {
		MenuData md = new MenuData();
		md.addPhrase("My phase two  question-ANSWER, phrase# oo0oo", "name", "en_US");
		printWordList( md, System.out, "Phrase with special characters" );
	}

	public void testAddPhrase3() {
		MenuData md = new MenuData();
		md.addPhrase("My 2-phase question-3 oo0oo", "name", "en_US");
		printWordList( md, System.out, "Phrase with numbers" );
	}

	public void testAddPhrase4() {
		MenuData md = new MenuData();
		md.addPhrase(" My   question,,,; is this ", "name", "en_US");
		printWordList( md, System.out, "Phrase with extra spaces and punctuation" );
	}

	public void testAddPhrase5() {
		MenuData md = new MenuData();
		md.addPhrase(" My name's o'Brian ", "name", "en_US");
		printWordList( md, System.out, "Phrase with punctuation in words" );
	}

}
