package spell;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class SpellCorrector implements ISpellCorrector {

	Trie dictionary;
	TreeMap<String, Integer> possibleSpellings;
	boolean alreadyCorrect;

	public SpellCorrector() {
		dictionary = new Trie();
	}

	@Override
	public void useDictionary(String dictionaryFileName) throws IOException {
		File file = new File(dictionaryFileName);
		Scanner scanner = new Scanner(file);

		// int dictionaryWordCount = 0;
		while (scanner.hasNext()) {
			dictionary.add(scanner.next());
			// dictionaryWordCount++;
		}
		scanner.close();
	}

	@Override
	public String suggestSimilarWord(String inputWord) {
		alreadyCorrect = false;
		inputWord = inputWord.toLowerCase();
		if (inputWord == "") {
			return null;
		}
		
		if (dictionary.foundInTrie(inputWord)) {
			alreadyCorrect = true;
		}
		if (alreadyCorrect)
			return inputWord;
		
		possibleSpellings = new TreeMap<String, Integer>();
		addDeletionSpellings(inputWord);
		addTranspositionSpellings(inputWord);
		addAlterationSpellings(inputWord);
		addInsertionSpellings(inputWord);
		
		if (possibleSpellings.size() == 1) {
			return possibleSpellings.firstKey();
		}
		int max = findMax(possibleSpellings);
		TreeMap<String, Integer> finalSpellings = sortOutMaxs(possibleSpellings, max);

		/*
		 * Edit distance two required check This will exponentially expand the
		 * possibleSpellings Map
		 */
		if (possibleSpellings.firstEntry() == null) {
			TreeMap<String, Integer> distanceOneList = generateDistanceOneList(inputWord);
			for (Map.Entry<String, Integer> entry : distanceOneList.entrySet()) {
				addDeletionSpellings(entry.getKey());
				addTranspositionSpellings(entry.getKey());
				addAlterationSpellings(entry.getKey());
				addInsertionSpellings(entry.getKey());
			}
			if (possibleSpellings.firstEntry() == null) {
				return null;
			}
			max = findMax(possibleSpellings);
			finalSpellings = sortOutMaxs(possibleSpellings, max);
		}
		
		return finalSpellings.firstKey();
	}

	/**
	 * Adds the possible edit distance one words available in the Trie through
	 * deletion to the TreeMap
	 * 
	 * @param word Word to have the edit distance one deletions run on
	 */

	private void addDeletionSpellings(String word) {
		if (word.length() < 2)
			return;
		for (int i = 0; i < word.length(); i++) {
			StringBuilder sb = new StringBuilder(word);
			sb.deleteCharAt(i);
			if (dictionary.foundInTrie(sb.toString())) {
				possibleSpellings.put(sb.toString(), dictionary.find(sb.toString()).getValue());
			}
		}
	}

	/**
	 * Adds the possible edit distance one words available in the Trie through
	 * transposition to the TreeMap
	 * 
	 * @param word Word to have the edit distance one transpositions run on
	 */

	private void addTranspositionSpellings(String word) {
		if (word.length() < 2)
			return;
		for (int i = 1; i < word.length(); i++) {
			StringBuilder sb = new StringBuilder(word);
			char savedLetter = word.charAt(i);
			sb.setCharAt(i, word.charAt(i - 1));
			sb.setCharAt(i - 1, savedLetter);

			if (dictionary.foundInTrie(sb.toString())) {
				possibleSpellings.put(sb.toString(), dictionary.find(sb.toString()).getValue());
			}
		}
	}

	/**
	 * Adds the possible edit distance one words available in the Trie through
	 * alteration to the TreeMap
	 * 
	 * @param word Word to have the edit distance one alterations run on
	 */

	private void addAlterationSpellings(String word) {
		for (int i = 0; i < word.length(); i++) {
			for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
				StringBuilder sb = new StringBuilder(word);
				sb.setCharAt(i, alphabet);
				if (dictionary.foundInTrie(sb.toString())) {
					possibleSpellings.put(sb.toString(), dictionary.find(sb.toString()).getValue());
				}
			}
		}
	}

	/**
	 * Adds the possible edit distance one words available in the Trie through
	 * insertion to the TreeMap
	 * 
	 * @param word Word to have the edit distance one insertions run on
	 */
	private void addInsertionSpellings(String word) {
		for (int i = 0; i <= word.length(); i++) {
			for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
				StringBuilder sb = new StringBuilder(word);
				sb.insert(i, alphabet);
				if (dictionary.foundInTrie(sb.toString())) {
					possibleSpellings.put(sb.toString(), dictionary.find(sb.toString()).getValue());
				}

			}
		}
	}

	/**
	 * Finds the word with the highest count/value in the map
	 * 
	 * @param map Map to find the maximum count/value in
	 * @return The max count/value found
	 */

	private int findMax(TreeMap<String, Integer> map) {
		int max = 0;
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			if (entry.getValue() > max) {
				max = entry.getValue();
			}
		}
		return max;
	}

	/**
	 * Provides a map with only the highest counts/values given a max
	 * 
	 * @param map Map to sort through
	 * @param max Maximum count/value to find; can be found using
	 *            findMax(TreeMap<String, Integer> map)
	 * @return A new map with only the highest count/value items
	 */

	private TreeMap<String, Integer> sortOutMaxs(TreeMap<String, Integer> map, int max) {
		TreeMap<String, Integer> returnedMap = new TreeMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			if (entry.getValue() == max) {
				returnedMap.put(entry.getKey(), entry.getValue());
			}
		}

		return returnedMap;
	}

	/**
	 * Generates a list with all the possible edit distance one words regardless of
	 * whether they are in the Trie or not
	 * 
	 * @param word
	 * @return
	 */
	private TreeMap<String, Integer> generateDistanceOneList(String word) {
		TreeMap<String, Integer> distanceOneMap = new TreeMap<String, Integer>();

		// deletion
		for (int i = 0; i < word.length(); i++) {
			if (word.length() < 2)
				break;
			StringBuilder sb = new StringBuilder(word);
			sb.deleteCharAt(i);
			if (distanceOneMap.containsKey(sb.toString())) {
				distanceOneMap.replace(sb.toString(), distanceOneMap.get(sb.toString() + 1));
			} else {
				distanceOneMap.put(sb.toString(), 1);
			}

		}

		// transposition
		for (int i = 1; i < word.length(); i++) {
			if (word.length() < 2)
				break;
			StringBuilder sb = new StringBuilder(word);
			char savedLetter = word.charAt(i);
			sb.setCharAt(i, word.charAt(i - 1));
			sb.setCharAt(i - 1, savedLetter);

			if (distanceOneMap.containsKey(sb.toString())) {
				distanceOneMap.replace(sb.toString(), distanceOneMap.get(sb.toString() + 1));
			} else {
				distanceOneMap.put(sb.toString(), 1);
			}
		}

		// alteration
		for (int i = 0; i < word.length(); i++) {
			for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
				StringBuilder sb = new StringBuilder(word);
				sb.setCharAt(i, alphabet);
				if (distanceOneMap.containsKey(sb.toString())) {
					distanceOneMap.replace(sb.toString(), distanceOneMap.get(sb.toString() + 1));
				} else {
					distanceOneMap.put(sb.toString(), 1);
				}
			}
		}

		// insertion
		for (int i = 0; i <= word.length(); i++) {
			for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
				StringBuilder sb = new StringBuilder(word);
				sb.insert(i, alphabet);
				if (distanceOneMap.containsKey(sb.toString())) {
					distanceOneMap.replace(sb.toString(), distanceOneMap.get(sb.toString() + 1));
				} else {
					distanceOneMap.put(sb.toString(), 1);
				}
			}
		}
		return distanceOneMap;

	}

}
