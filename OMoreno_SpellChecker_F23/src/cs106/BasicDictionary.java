package cs106;

import java.io.*;
import java.util.*;

import static java.lang.System.*;
import static java.util.Collections.*;
import static sbcc.Core.*;
import static org.apache.commons.lang3.StringUtils.*;

public class BasicDictionary implements Dictionary {
    private BinaryTreeNode root;
    private int count;

    void saveTreePreOrder(BinaryTreeNode cursor, StringBuilder sb) {
        if (cursor == null) return;

        sb.append(cursor.value).append(lineSeparator());

        saveTreePreOrder(cursor.left, sb);
        saveTreePreOrder(cursor.right, sb);
    }

    @Override
    public void importFile(String filename) throws Exception {
        // Might need a clear function
        var list = readFileAsLines(filename);
        shuffle(list);
        for (var word : list) {
            add(trim(word));
        }
    }

    @Override
    public void load(String filename) throws Exception {
        var list = readFileAsLines(filename);
        for (var word : list) {
            add(word);
        }
    }

    @Override
    public void save(String filename) throws Exception {
        var sb = new StringBuilder();
        saveTreePreOrder(root, sb);
        writeFile(filename, sb.toString());
    }

    @Override
    public String[] find(String word) {
        if (root == null)
            return null;
        var cur = root;
        String[] arr = {"", ""};
        int cmp;
        while (cur != null) {
            cmp = cur.value.compareToIgnoreCase(word);
            if (cmp == 0)
                return null;
            if (cmp > 0) {
                arr[1] = cur.value;
                cur = cur.left;
            } else {
                arr[0] = cur.value;
                cur = cur.right;
            }
        }
        return arr;
    }

    @Override
    public void add(String word) {
        // In the case of an empty tree
        if (root == null) {
            root = new BinaryTreeNode(word);
            count++;
            return;
        }
        var cur = root;
        int cmp = word.compareToIgnoreCase(cur.value);
        while (cur != null) { // Warning stems from early returns
            if (cmp == 0) // Means same word was already in dictionary
                return;
            if (cmp < 0) {
                if (cur.left == null) {
                    cur.left = new BinaryTreeNode(word);
                    count++;
                    return;
                } else {
                    cur = cur.left;
                }
            } else {
                if (cur.right == null) {
                    cur.right = new BinaryTreeNode(word);
                    count++;
                    return;
                } else {
                    cur = cur.right;
                }
            }
            cmp = word.compareToIgnoreCase(cur.value);
        }
    }

    @Override
    public BinaryTreeNode getRoot() {
        return root;
    }

    @Override
    public int getCount() {
        return count;
    }
}
