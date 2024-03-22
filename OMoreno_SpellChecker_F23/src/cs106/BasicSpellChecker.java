package cs106;

import static java.lang.System.*;
import static java.util.Collections.*;
import static sbcc.Core.*;
import static org.apache.commons.lang3.StringUtils.*;
import static java.lang.Math.*;
import static java.lang.System.*;

import java.io.*;
import java.util.regex.*;

public class BasicSpellChecker implements SpellChecker {

    private BasicDictionary dictionary = new BasicDictionary();
    private StringBuilder document = new StringBuilder();
    private int index;

    @Override
    public void importDictionary(String filename) throws Exception {
        dictionary.importFile(filename);
    }

    @Override
    public void loadDictionary(String filename) throws Exception {
        dictionary.load(filename);
    }

    @Override
    public void saveDictionary(String filename) throws Exception {
        dictionary.save(filename);
    }

    @Override
    public void loadDocument(String filename) throws Exception {
        document.append(readFile(filename));
    }

    @Override
    public void saveDocument(String filename) throws Exception {
        writeFile(filename, document.toString());
    }

    @Override
    public String getText() {
        return document.toString();
    }

    @Override
    public String[] spellCheck(boolean continueFromPrevious) {
        String[] output = {"", "", "", ""};
        var p = Pattern.compile("\\b[\\w']+\\b");
        var m = p.matcher(getText());
        if (!continueFromPrevious)
            index = 0;
        while (m.find(index)) {  // Starts the SpellCheck from the index
            var word = m.group();
            index = m.end();
            if (dictionary.find(word) != null) {
                var prevAndNext = dictionary.find(word);
                output[0] = word;
                output[1] = m.start() + "";
                output[2] = prevAndNext[0];
                output[3] = prevAndNext[1];
                return output;
            }
        }
        return null;
    }

    @Override
    public void addWordToDictionary(String word) {
        dictionary.add(word);
    }

    @Override
    public void replaceText(int startIndex, int endIndex, String replacementText) {
        document.replace(startIndex, endIndex, replacementText);
        index += abs((endIndex - startIndex) - replacementText.length());
    }
}
