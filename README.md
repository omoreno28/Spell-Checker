# Spell-Checker
Written in Java. This is a simplified spell-checker program that uses a dictionary which stores its words in a balanced binary tree. 

Creates three classes:
  1. BinaryTreeNode
  2. BasicDictionary
  3. BasicSpellChecker

Functionality:
The program will take a given .txt document as an input. It will loop over the contents, identifying if each word is within the given dictionary. If a word is not detected to be in the dictionary, the program makes use of a balanced binary to recommend the two words most similar to the misspelled word.

Improvements:
In order to improve the efficiency of the program, words can be sequentially added to a hashmap when they are detected to be in the dictionary. That way, you won't have to spell-check words that have already been spell-checked. 

