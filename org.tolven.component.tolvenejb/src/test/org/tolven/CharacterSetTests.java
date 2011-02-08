package test.org.tolven;


import java.util.Arrays;

import junit.framework.TestCase;
/**
 * This test verifies that at least this source module has the correct character set.
 * Tolven uses UTF-8 exclusively. If the charset attribute of this module is not set to UTF-8, it's likely to cause an error. 
 * @author John Churin
 *
 */
public class CharacterSetTests extends TestCase {
	private static final String line1 = "„ø¤º°¨°º¤ø„¸„ø¤º°¨°º¤ø„¸„ø¤º°¨°º¤ø„¸„ø¤º°¨°º¤ø„¸„ø¤º°¨°º¤ø„¸„ø¤º°¨°º¤ø„¸";
	private static final String line2 = "     T           O           L           V           E           N";
	private static final String line3 = "°º¤ø„¸„ø¤º°¨°º¤ø„¸„ø¤º°¨°º¤ø„¸„ø¤º°¨°º¤ø„¸„ø¤º°¨°º¤ø„¸„ø¤º°¨°º¤ø„¸„ø¤º°¨";
	private static final char[] line1Chars = {8222,248,164,186,176,168,176,186,164,248,8222,184,8222,248,164,186,176,168,176,186,164,248,8222,184,8222,248,164,186,176,168,176,186,164,248,8222,184,8222,248,164,186,176,168,176,186,164,248,8222,184,8222,248,164,186,176,168,176,186,164,248,8222,184,8222,248,164,186,176,168,176,186,164,248,8222,184};
	private static final char[] line2Chars = {32,32,32,32,32,84,32,32,32,32,32,32,32,32,32,32,32,79,32,32,32,32,32,32,32,32,32,32,32,76,32,32,32,32,32,32,32,32,32,32,32,86,32,32,32,32,32,32,32,32,32,32,32,69,32,32,32,32,32,32,32,32,32,32,32,78};
	private static final char[] line3Chars = {176,186,164,248,8222,184,8222,248,164,186,176,168,176,186,164,248,8222,184,8222,248,164,186,176,168,176,186,164,248,8222,184,8222,248,164,186,176,168,176,186,164,248,8222,184,8222,248,164,186,176,168,176,186,164,248,8222,184,8222,248,164,186,176,168,176,186,164,248,8222,184,8222,248,164,186,176,168};

	public void generateChars( ) {
		StringBuffer sb = new StringBuffer();
		for (char c : line1.toCharArray()) {
			sb.append((int)c);
			sb.append(",");
		}
		System.out.println(sb.toString());
		sb = new StringBuffer();
		for (char c : line2.toCharArray()) {
			sb.append((int)c);
			sb.append(",");
		}
		System.out.println(sb.toString());
		sb = new StringBuffer();
		for (char c : line3.toCharArray()) {
			sb.append((int)c);
			sb.append(",");
		}
		System.out.println(sb.toString());
		
	}
	
	public void testUTF8( ) {
//		generateChars();
		assertTrue(Arrays.equals(line1.toCharArray(), line1Chars));
		System.out.println( line1 );
		assertTrue(Arrays.equals(line2.toCharArray(), line2Chars));
		System.out.println( line2 );
		assertTrue(Arrays.equals(line3.toCharArray(), line3Chars));
		System.out.println( line3 );
	}
	
}
