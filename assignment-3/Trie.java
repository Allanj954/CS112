package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		/** COMPLETE THIS METHOD **/
		
		TrieNode root = new TrieNode(null, null, null);
		
		if (allWords.length == 0) {
			return root;
		}
		int findIndex = 0;
		int wordIndex = 0;
		short startIndex = 0;
		short endIndex = 0;
		
		root.firstChild = new TrieNode(new Indexes(0, (short)(0), (short)(allWords[0].length() - 1)), null, null);
		
		TrieNode pointer = root.firstChild;
		TrieNode previous = root.firstChild;
		
		for (int i = 1; i < allWords.length; i++) {
			String word = allWords[i];
			while (pointer != null) {
				wordIndex = pointer.substr.wordIndex;
				startIndex = pointer.substr.startIndex;
				endIndex = pointer.substr.endIndex;
				
				String currentWord = allWords[wordIndex].substring(startIndex, endIndex);
				
				if (startIndex > word.length()) {
					previous = pointer;
					pointer = pointer.sibling;
				}
				
				findIndex = findMatchingIndex(word.substring(startIndex), allWords[wordIndex].substring(startIndex, endIndex + 1));
				
				if (findIndex != -1) {
					findIndex += startIndex;
				}
				
				if (findIndex == -1) {
					previous = pointer;
					pointer = pointer.sibling;
				} else {
					if (findIndex == endIndex) {
						previous = pointer;
						pointer = pointer.firstChild;
					} else if (findIndex != endIndex) {
						previous = pointer;
						break;
					}	
				}
				
				
			}
			
			if (pointer == null) {
				previous.sibling = new TrieNode(new Indexes(i, startIndex, (short)(word.length() - 1)), null, null);
			} else {
				
				
				short newChildEnd = previous.substr.endIndex;
				int newChildIndex = previous.substr.wordIndex;
				Indexes newIndex = new Indexes(wordIndex, previous.substr.startIndex, newChildEnd);
				
				TrieNode currentFirstChild = previous.firstChild;
				Indexes currentIndexes = previous.substr;
				
				Indexes newWordIndexes = new Indexes(previous.substr.wordIndex, (short)(findIndex + 1), previous.substr.endIndex);
				currentIndexes.endIndex = (short)findIndex;
				
				previous.firstChild = new TrieNode(newWordIndexes, null, null);
				previous.firstChild.firstChild = currentFirstChild;
				previous.firstChild.sibling = new TrieNode(new Indexes(i, (short)(findIndex + 1), (short)(word.length() - 1)), null, null);
			}
			
			pointer = root.firstChild;
			previous = root.firstChild;
			findIndex = 0;
			wordIndex = 0;
			startIndex = 0;
			endIndex = 0;
		}
		return root;
	}
	
		private static int findMatchingIndex(String newWord, String currentWord) {
			int index = 0;
			
			while (index < newWord.length() && index < currentWord.length() && newWord.charAt(index) == currentWord.charAt(index)) {
				index++;
			}
			
			if (index == 0) {
				return -1;
			} else {
				return (index-1);
			}
		
		
	}
		
	
	
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		
		if (root == null) {
			return null;
		}
		
		ArrayList<TrieNode> result = new ArrayList<TrieNode>();
		TrieNode pointer = root;

		
;
		
		while (pointer != null) {
			if (pointer.substr == null) {
				pointer = pointer.firstChild;
			}
			
			String string1 = allWords[pointer.substr.wordIndex];
			String currentNode = string1.substring(0, pointer.substr.endIndex + 1);
		
			if (currentNode.startsWith(prefix)) {
				if (pointer.firstChild != null) {
					result.addAll(completionList(pointer.firstChild, allWords, prefix));
					pointer = pointer.sibling;
				} else {
					result.add(root);
					pointer = pointer.sibling;
				}
			} else {
				pointer = pointer.sibling;
			}
		}
		return result;
	}
	
	private static ArrayList<TrieNode> findAllLeafs(TrieNode root, ArrayList<TrieNode> list) {
		ArrayList<TrieNode> result = new ArrayList<TrieNode>();
		
		if (root.firstChild != null) {
			findAllLeafs(root.firstChild, list);
		} else {
			result.add(root);
			findAllLeafs(root.sibling, list);
		}
		return result;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
