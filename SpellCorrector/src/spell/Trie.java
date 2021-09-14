package spell;

public class Trie implements ITrie {
	private final Node root;
	private int wordCount;
	private int nodeCount;
	private int indexHasher;

	public Trie() {
		root = new Node();
		wordCount = 0;
		nodeCount = 1;
		indexHasher = 1;
	}

	/**
	 * Adds a new word to the Trie
	 * 
	 * @param word The new word to be added
	 */
	@Override
	public void add(String word) {
		word = word.toLowerCase();
		char letter;
		int index;
		Node currentNode = root;

		for (int i = 0; i < word.length(); i++) {
			letter = word.charAt(i);
			index = letter - 'a';
			indexHasher += letter * 19 * index;

			if (currentNode.getChildren()[index] == null) {
				currentNode.getChildren()[index] = new Node();
				nodeCount++;
			}

			if (i == word.length() - 1) {
				if (currentNode.getChildren()[index].getValue() == 0)
					wordCount++;
				currentNode.getChildren()[index].incrementValue();
			}
			currentNode = currentNode.getChildren()[index];
		}

	}

	/**
	 * Searches the Trie for a given word
	 * 
	 * @param word The word to serach the Trie for
	 * @return The Node at which the word can be found, null if not found
	 */
	@Override
	public Node find(String word) {
		char letter;
		int index = 0;
		Node currentNode = root;

		word = word.toLowerCase();

		for (int i = 0; i < word.length(); i++) {
			letter = word.charAt(i);
			index = letter - 'a';
			Node child = currentNode.getChildren()[index];

			if (child == null)
				return null;
			if (i < word.length() - 1)
				currentNode = child;
		}

		if (currentNode.getChildren()[index].getValue() > 0) {
			return currentNode.getChildren()[index];
		}

		return null;
	}
	
	public boolean foundInTrie(String word) {
		char letter;
		int index = 0;
		Node currentNode = root;

		word = word.toLowerCase();

		for (int i = 0; i < word.length(); i++) {
			letter = word.charAt(i);
			index = letter - 'a';
			Node child = currentNode.getChildren()[index];

			if (child == null)
				return false;
			if (i < word.length() - 1)
				currentNode = child;
		}

		if (currentNode.getChildren()[index].getValue() > 0) {
			return true;
		}
		
		
		return false;
	}

	/**
	 * Gets the number of words stored in the Trie
	 * 
	 * @return The number of words stored in the Trie
	 */
	@Override
	public int getWordCount() {
		return wordCount;
	}

	/**
	 * Gets the number of filled/non-null Nodes in the Trie
	 * 
	 * @return The number of filled nodes in the Trie
	 */
	@Override
	public int getNodeCount() {
		return nodeCount;
	}

	@Override
	public String toString() {
		StringBuilder curWord = new StringBuilder();
		StringBuilder output = new StringBuilder();

		stringListGenerator(root, curWord, output);

		return output.toString();
	}

	/**
	 * Generates the list of words in the Trie as a string stored in the output
	 * parameter; mainly to be used for the toString() function
	 * 
	 * @param n       The node at which to start the list at
	 * @param curWord The current word being built
	 * @param output  The final list as a string
	 */
	private void stringListGenerator(Node n, StringBuilder curWord, StringBuilder output) {
		if (n.getValue() > 0) {
			output.append(curWord.toString());
			output.append("\n");
		}

		for (int i = 0; i < n.getChildren().length; i++) {
			Node child = n.getChildren()[i];
			if (child != null) {
				char childLetter = (char) ('a' + i);
				curWord.append(childLetter);
				stringListGenerator(child, curWord, output);
				curWord.deleteCharAt(curWord.length() - 1);
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trie other = (Trie) obj;
		if (nodeCount != other.nodeCount)
			return false;
		if (wordCount != other.wordCount)
			return false;
		return treeDif(root, other.root);
	}

	/**
	 * Traverses the Tries of both nodes and checks for differences. Mainly to be
	 * used for the equals method
	 * 
	 * @param n1 The first node to be checked
	 * @param n2 The node for n1 to be checked against
	 * @return Whether the Tries corresponding to the two nodes have differences
	 */
	private boolean treeDif(Node n1, Node n2) {
		if (n1.getValue() != n2.getValue())
			return false;

		for (int i = 0; i < n1.getChildren().length; i++) {
			Node child1 = n1.getChildren()[i];
			Node child2 = n2.getChildren()[i];

			if (child1 != null && child2 != null) {
				if (treeDif(child1, child2))
					continue;
				else
					return false;
			} else if (child1 == null && child2 == null)
				continue;
			else if (child1 != null || child2 != null)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {

		int hash = indexHasher;
		return hash;
	}

}
