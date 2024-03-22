package sbccunittest;

import static java.lang.Math.*;
import static java.lang.System.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.junit.Assert.*;
import static sbcc.Core.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;

import org.junit.*;
import org.w3c.dom.ranges.*;

import sbcc.*;
import cs106.*;
import cs106.Dictionary;

/**
 * 10/22/2022
 * 
 * @author sstrenn
 *
 */
public class SpellCheckerTester {

	public static String newline = System.getProperty("line.separator");

	public static int totalScore = 0;

	public static int extraCredit = 0;
	public static boolean isZeroScore = false;
	public static String scorePreamble = "";



	@BeforeClass
	public static void beforeTesting() {
		totalScore = 0;
		extraCredit = 0;
	}


	@AfterClass
	public static void afterTesting() {
		if (isZeroScore) {
			totalScore = 0;
			extraCredit = 0;
		}
		println(lineSeparator() + scorePreamble + "Estimated score (w/o late penalties, etc.) = " + totalScore);
		println("Estimated extra credit (assuming on time submission) = " + extraCredit);
}


	@Test(timeout = 10000)
	public void testImportFile() throws Exception {
		Dictionary dictionary = new BasicDictionary();

		dictionary.importFile("full_dictionary.txt");

		assertNotNull("Dictionary.getRoot() should not be null.", dictionary.getRoot());

		int depth = getTreeDepth(dictionary);
		int maxDepth = 100;
		if (depth > maxDepth)
			fail("The tree depth is " + depth
					+ " is greater than the maximum allowed depth of " + maxDepth + ".");

		dictionary.save("full_dictionary.pre");
		String s = readFile("full_dictionary.pre");
		String[] parts = s.split("\n");
		assertEquals(175169, parts.length);

		totalScore += 5;
	}


	@Test(timeout = 10000)
	public void testImportFileCompleteTree() throws Exception {
		Dictionary dictionary = new BasicDictionary();
		dictionary.importFile("full_dictionary.txt");

		var root = dictionary.getRoot();
		assertNotNull("Dictionary.getRoot() should not be null.", root);

		int depth = getTreeDepth(dictionary);
		int maxDepth = 18;
		if (depth > maxDepth)
			fail("The tree depth is " + depth
					+ ", which is > than the max allowed depth of " + maxDepth
					+ ".");

		dictionary.save("full_dictionary.pre");
		String s = readFile("full_dictionary.pre");
		String[] parts = s.split("\n");
		assertEquals(175169, parts.length);

		extraCredit += 5;

		int count = countNodes(root);
		int index = incompleteAtLevelOrderIndex(root, count);
		if (index >= 0)
			fail("Level order index " + index
					+ " is empty, so the tree is not complete.  But the tree has the right number of levels to get the extra credit.");
	}


	/* This function counts the number of nodes in a binary tree */
	int countNodes(BinaryTreeNode node) {
		if (node == null)
			return (0);
		return (1 + countNodes(node.left) + countNodes(node.right));
	}


	/* This function checks if the binary tree is complete or not */
	boolean isComplete(BinaryTreeNode root, int index, int numNodes) {
		// An empty tree is complete
		if (root == null)
			return (true);

		// If index assigned to current node is more than
		// number of nodes in tree, then tree is not complete
		if (index >= numNodes)
			return (false);

		// Recur for left and right subtrees
		return (isComplete(root.left, 2 * index + 1, numNodes) &&
				isComplete(root.right, 2 * index + 2, numNodes));
	}


	// public static void main(String[] args) {
	// var d = new BasicDictionary();
	// d.add("D");
	// d.add("B");
	// d.add("F");
	// // d.add("A");
	// d.add("C");
	// d.add("E");
	// d.add("G");
	// println(incompleteAtLevelOrderIndex(d.getRoot(), d.getCount()));
	// }

	static int incompleteAtLevelOrderIndex(BinaryTreeNode cursor, int numNodes) {
		int index = 0;
		var q = new ArrayDeque<BinaryTreeNode>();
		while (index < numNodes) {
			index++;
			if (index >= numNodes)
				break;
			if (cursor.left == null)
				return index;
			else
				q.add(cursor.left);

			index++;
			if (index >= numNodes)
				break;
			if (cursor.right == null)
				return index;
			else
				q.add(cursor.right);
			cursor = q.remove();
		}
		return -1;
	}


	public int getTreeDepth(Dictionary dictionary) {
		return getTreeDepth(dictionary.getRoot());
	}


	public int getTreeDepth(BinaryTreeNode node) {
		if (node == null)
			return 0;
		else
			return 1 + max(getTreeDepth(node.left), getTreeDepth(node.right));
	}


	@Test(timeout = 10000)
	public void testLoad() throws Exception {
		Dictionary dictionary = new BasicDictionary();
		dictionary.load("dict_14.pre");

		assertNotNull("Dictionary.getRoot() should not be null.", dictionary.getRoot());

		int depth = getTreeDepth(dictionary);
		assertEquals(6, depth);

		totalScore += 8;
	}


	@Test(timeout = 10000)
	public void testSave() throws Exception {
		Dictionary dictionary = new BasicDictionary();
		String[] words = new String[] { "bull", "are", "genetic", "cotton", "dolly",
				"florida", "each", "bull" };
		for (String word : words)
			dictionary.add(word);

		dictionary.save("test_save.pre");
		String s = readFile("test_save.pre");
		String[] parts = s.split("\n");

		assertEquals(words.length - 1, parts.length);
		for (int ndx = 0; ndx < parts.length; ndx++)
			assertEquals(words[ndx], parts[ndx].trim().toLowerCase());

		totalScore += 8;
	}

	@Test
	public void testAdd() throws Exception{
		Dictionary d = new BasicDictionary();
		for (var w : "AECBD".split(""))
			d.add(w);
		var n = d.getRoot();
		assertTrue(n.value.equals("A"));
		assertTrue(n.right.value.equals("E"));
		assertTrue(n.right.left.value.equals("C"));
		assertTrue(n.right.left.left.value.equals("B"));
		assertTrue(n.right.left.right.value.equals("D"));
		
		d = new BasicDictionary();
		for (var w : "EDCAB".split(""))
			d.add(w);
		n = d.getRoot();
		assertTrue(n.value.equals("E"));
		assertTrue(n.left.value.equals("D"));
		assertTrue(n.left.left.value.equals("C"));
		assertTrue(n.left.left.left.value.equals("A"));
		assertTrue(n.left.left.left.right.value.equals("B"));
		totalScore += 5;
	}

	@Test
	public void testFind() throws Exception {
		Dictionary dictionary = new BasicDictionary();
		String dictionaryPath = "dict_14.pre";
		dictionary.load(dictionaryPath);

		checkWord(dictionary, dictionaryPath, "cotton", null);
		checkWord(dictionary, dictionaryPath, "CottoN", null);
		checkWord(dictionary, dictionaryPath, "Cotto", new String[] { "bull", "cotton" });
		checkWord(dictionary, dictionaryPath, "mit", new String[] { "life", "mite" });
		checkWord(dictionary, dictionaryPath, "mite", null);
		checkWord(dictionary, dictionaryPath, "just", null);

		totalScore += 8;
	}


	private void checkWord(Dictionary dictionary, String dictionaryPath, String word,
			String[] expectedResult) {
		String[] result = dictionary.find(word);
		if (expectedResult != null) {
			if (result != null) {
				assertEquals(expectedResult[0], result[0]);
				assertEquals(expectedResult[1], result[1]);
			} else
				fail("The dictionary indicated that it found " + word
						+ ", but it should not have been found.");
		} else {
			if (result != null) {
				fail("The dictionary returned "
						+ (result.length > 0 ? result[0] : "an empty array")
						+ " but should have returned null because " + word
						+ " does exist in " + dictionaryPath);
			}
		}

	}


	@Test
	public void testLoadDocument() throws Exception {
		String dictionaryText = readFile("small_dictionary.txt");
		String[] words = dictionaryText.split(newline);
		Random rng = new Random();
		String doc = words[rng.nextInt(words.length)].trim() + " "
				+ words[rng.nextInt(words.length)].trim() + " "
				+ words[rng.nextInt(words.length)].trim() + " "
				+ words[rng.nextInt(words.length)].trim() + " "
				+ words[rng.nextInt(words.length)].trim();
		writeFile("random_doc.txt", doc);
		SpellChecker basicSpellChecker = new BasicSpellChecker();
		basicSpellChecker.loadDocument("random_doc.txt");
		String text = basicSpellChecker.getText();
		assertEquals(doc, text);

		totalScore += 2;
	}


	@Test
	public void testSpellCheckWithOneUnknownWord() throws Exception {
		SpellChecker basicSpellChecker = new BasicSpellChecker();

		String dictionaryImportPath = "small_dictionary.txt";
		String dictionaryPath = "small_dictionary.pre";
		String documentPath = "small_document_one_unknown.txt";

		basicSpellChecker.importDictionary(dictionaryImportPath);
		basicSpellChecker.saveDictionary(dictionaryPath);

		basicSpellChecker.loadDocument(documentPath);

		String[] result;

		result = basicSpellChecker.spellCheck(false);
		if (result == null)
			fail("There should be one unknown word in " + documentPath
					+ " when the dictionary is "
					+ dictionaryImportPath);
		else {
			assertEquals("explosins", result[0]);
			assertEquals("87", result[1]);
			assertEquals("ever", result[2]);
			assertEquals("explosions", result[3]);
		}

		totalScore += 6;
	}


	@Test
	public void testSpellCheckReplaceOneUnknownWord() throws Exception {
		SpellChecker basicSpellChecker = new BasicSpellChecker();

		String dictionaryImportPath = "small_dictionary.txt";
		String dictionaryPath = "small_dictionary.pre";
		String documentPath = "small_document_one_unknown.txt";

		basicSpellChecker.importDictionary(dictionaryImportPath);
		basicSpellChecker.saveDictionary(dictionaryPath);

		basicSpellChecker.loadDocument(documentPath);

		String[] result;

		// Spell-check and find one word misspelled.
		result = basicSpellChecker.spellCheck(false);
		if (result == null)
			fail("There should be one unknown word in " + documentPath
					+ " when the dictionary is "
					+ dictionaryImportPath);
		else {
			assertEquals("explosins", result[0]);
			assertEquals("87", result[1]);
			assertEquals("ever", result[2]);
			assertEquals("explosions", result[3]);
		}

		// Replace it with the second suggestion.
		int startIndex = Integer.parseInt(result[1]);
		int endIndex = startIndex + result[0].length();
		basicSpellChecker.replaceText(startIndex, endIndex, result[3]);

		// Check against corrected.
		String text = basicSpellChecker.getText();
		String expected = readFile("small_document_one_unknown_corrected.txt");
		assertEquals(expected, text);

		totalScore += 6;
	}


	@Test
	public void testSpellCheckNoUnknownWords() throws Exception {
		SpellChecker basicSpellChecker = new BasicSpellChecker();

		String dictionaryImportPath = "small_dictionary.txt";
		String dictionaryPath = "small_dictionary.pre";
		String documentPath = "small_document.txt";

		basicSpellChecker.importDictionary(dictionaryImportPath);
		basicSpellChecker.saveDictionary(dictionaryPath);

		basicSpellChecker.loadDocument(documentPath);

		String[] result;

		result = basicSpellChecker.spellCheck(false);
		if (result != null)
			fail("There should be no unknown words in " + documentPath
					+ " when the dictionary is " + dictionaryPath);

		totalScore += 4;
	}


	@Test
	public void testSpellCheckReplaceUnknowns() throws Exception {
		SpellChecker basicSpellChecker = new BasicSpellChecker();

		String dictionaryImportPath = "small_dictionary.txt";
		String dictionaryPath = "small_dictionary.pre";
		String documentPath = "small_document_four_unknown.txt";

		basicSpellChecker.importDictionary(dictionaryImportPath);
		basicSpellChecker.saveDictionary(dictionaryPath);

		basicSpellChecker.loadDocument(documentPath);

		String[] result;

		// Find first unknown
		result = basicSpellChecker.spellCheck(false);
		if (result == null)
			fail("Failed to find the first unknown word in " + documentPath
					+ " when the dictionary is "
					+ dictionaryImportPath);
		else {
			assertEquals("explosins", result[0]);
			assertEquals("87", result[1]);
			assertEquals("ever", result[2]);
			assertEquals("explosions", result[3]);
		}

		// Replace it with the successor word
		int startIndex = Integer.parseInt(result[1]);
		int endIndex = startIndex + result[0].length();
		basicSpellChecker.replaceText(startIndex, endIndex, result[3]);

		// find the 2nd unknown (the word "which")
		result = basicSpellChecker.spellCheck(true);
		if (result == null)
			fail("Failed to find the second unknown word in " + documentPath
					+ " when the dictionary is "
					+ dictionaryImportPath);
		else {
			assertEquals("which", result[0]);
			assertEquals("130", result[1]);
			assertEquals("use", result[2]);
			assertEquals("with", result[3]);
		}

		// Add this word to the dictionary
		String wordToAdd = result[0];
		basicSpellChecker.addWordToDictionary(result[0]);

		// find the 3rd unknown (the word "vast")
		result = basicSpellChecker.spellCheck(true);
		if (result == null)
			fail("Failed to find the third unknown word in " + documentPath
					+ " when the dictionary is "
					+ dictionaryImportPath);
		else {
			assertEquals("vast", result[0]);
			assertEquals("275", result[1]);
			assertEquals("use", result[2]);
			assertEquals("which", result[3]);
		}

		// Find third unknown
		result = basicSpellChecker.spellCheck(true);
		if (result == null)
			fail("Failed to find the fourth unknown word in " + documentPath
					+ " when the dictionary is "
					+ dictionaryImportPath);
		else {
			assertEquals("cuosmos", result[0]);
			assertEquals("280", result[1]);
			assertEquals("cosmos", result[2]);
			assertEquals("dozen", result[3]);
		}

		// Replace it with the predecessor word
		startIndex = Integer.parseInt(result[1]);
		endIndex = startIndex + result[0].length();
		basicSpellChecker.replaceText(startIndex, endIndex, result[2]);

		// Verify document is correct
		String expectedText = readFile("small_document_four_unknown_corrected.txt");
		String actualText = basicSpellChecker.getText();
		assertEquals(expectedText, actualText);

		// Verify the saved document is correct
		basicSpellChecker
				.saveDocument("small_document_four_unknown_after_spellchecking.txt");
		actualText = readFile("small_document_four_unknown_after_spellchecking.txt");
		assertEquals(expectedText, actualText);

		// Verify the dictionary is correct
		basicSpellChecker.saveDictionary("small_dictionary_after_spellchecking.pre");
		String dictText = readFile("small_dictionary_after_spellchecking.pre");

		if (!dictText.contains(wordToAdd))
			fail("Dictionary file didn't contain " + wordToAdd + ".");

		totalScore += 4;
	}


	@Test
	public void testSpellCheckNoSuccessor() throws Exception {

		SpellChecker basicSpellChecker = new BasicSpellChecker();
		String dictionaryImportPath = "small_dictionary.txt";
		String dictionaryPath = "small_dictionary.pre";
		String documentPath = "small_document_test_no_successor.txt";

		basicSpellChecker.importDictionary(dictionaryImportPath);
		basicSpellChecker.saveDictionary(dictionaryPath);

		basicSpellChecker.loadDocument(documentPath);

		String[] result;

		// Find first unknown
		result = basicSpellChecker.spellCheck(false);
		if (result == null)
			fail("Failed to find the first unknown word in " + documentPath
					+ " when the dictionary is "
					+ dictionaryImportPath);
		else {
			assertEquals("zebras", result[0]);
			assertEquals("87", result[1]);
			assertEquals("with", result[2]);
			assertEquals("", result[3]);
		}

		totalScore += 2;
	}


	@Test
	public void testPmd() {
		try {
			execPmd("src" + File.separator + "cs106", "cs106.ruleset");
		} catch (Exception ex) {
			fail(ex.getMessage());
		}

		totalScore += 6;

	}
	
	@Test
	public void testUsesTemplateProjectCorrectly()
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		verifyCoreVersion();
		long count = Files.find(Paths.get("lib"), Integer.MAX_VALUE,
				(path, basicFileAttributes) -> path.toFile().getName().matches("sbcccore.*.*.*.jar")).count();
		assertTrue(
				"This project doesn't appear to be based on a copy of JavaCoreTemplate because the sbcccore library was not found in the lib directory.  See https://github.com/ProfessorStrenn/JavaCoreTemplate#java-core-template---quickstart for help on using the JavaCoreTemplate.",
				count > 0);
		totalScore += 2;

	}

	private void verifyCoreVersion()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		try {
			var method = Core.class.getMethod("getSbccCoreVersion");

			String ver = (String) method.invoke(null);
			var parts = ver.split("\\.");
			if (parseInt(parts[0]) < 1 || parseInt(parts[1]) < 0 || parseInt(parts[2]) < 10)
				throw new RangeException((short) 0, "sbcccore version is " + ver + ", but must be at least 1.0.10");

		} catch (RangeException | NoSuchMethodException e) {
			isZeroScore = true;
			scorePreamble = "RESUBMISSION REQUIRED :  ************  See JUnit test results for help  ************";
			fail("RESUBMISSION REQUIRED.  This project appears to be based on a previous semester's project template.  See https://github.com/ProfessorStrenn/JavaCoreTemplate#java-core-template---quickstart for help on using the JavaCoreTemplate.");
		}
	}
	
	private static void execPmd(String srcFolder, String rulePath) throws Exception {

		File srcDir = new File(srcFolder);
		File ruleFile = new File(rulePath);

		verifySrcAndRulesExist(srcDir, ruleFile);

		ProcessBuilder pb;
		if (getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			String pmdBatPath = ".\\pmd_min\\bin\\pmd.bat";
			String curPath = Paths.get(".").toAbsolutePath().toString();

			// Handle CS lab situation where the current dir is a UNC path
			if (curPath.startsWith("\\\\NEBULA\\cloud$")) {
				curPath = "N:\\" + substringAfter(curPath, "cloud$\\");
				pmdBatPath = curPath + pmdBatPath.substring(1);
			}
			pb = new ProcessBuilder(
					pmdBatPath,
					"-f", "text",
					"-d", srcDir.getAbsolutePath(),
					"-R", ruleFile.getAbsolutePath());
		} else {
			pb = new ProcessBuilder(
					"./pmd_min/bin/run.sh", "pmd",
					"-d", srcDir.getAbsolutePath(),
					"-R", ruleFile.getAbsolutePath());
		}
		Process process = pb.start();
		int errCode = process.waitFor();

		switch (errCode) {

		case 1:
			out.println("PMD Check: -5 pts");
			String errorOutput = getOutput(process.getErrorStream());
			fail("Command Error:  " + errorOutput);
			break;

		case 4:
			out.println("PMD Check: -5 pts");
			String output = getOutput(process.getInputStream());
			fail(trimFullClassPaths(output));
			break;

		}

	}


	private static String trimFullClassPaths(String output) {
		// Shorten output to just the short class name, line, and error.
		String[] lines = output.split(getProperty("line.separator"));
		StringBuilder sb = new StringBuilder();
		for (String line : lines)
			sb.append(substringAfterLast(line, File.separator)).append(lineSeparator());

		String trimmedOutput = sb.toString();
		return trimmedOutput;
	}


	private static void verifySrcAndRulesExist(File fileFolderToCheck, File ruleFile)
			throws Exception {
		if (!fileFolderToCheck.exists())
			throw new FileNotFoundException(
					"The folder to check '" + fileFolderToCheck.getAbsolutePath()
							+ "' does not exist.");

		if (!fileFolderToCheck.isDirectory())
			throw new FileNotFoundException(
					"The folder to check '" + fileFolderToCheck.getAbsolutePath()
							+ "' is not a directory.");

		if (!ruleFile.exists())
			throw new FileNotFoundException(
					"The rule set file '" + ruleFile.getAbsolutePath()
							+ "' could not be found.");
	}


	private static String getOutput(InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + getProperty("line.separator"));
			}
		} finally {
			br.close();
		}
		return sb.toString();

	}

}
