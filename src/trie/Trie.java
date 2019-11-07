package trie;
import java.lang.reflect.Array;
import java.util.ArrayList;
/**
 * This class implements a Trie.
 *
 * @author Sesh Venugopal
 */
public class Trie {
	// prevent instantiation
	private Trie() {
	}
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 *
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		TrieNode root = new TrieNode(null, null, null);
		TrieNode ptr = root.firstChild;
		TrieNode lastSib = null;
		TrieNode parent = root;
		Indexes in = new Indexes(0, (short) 0, (short) (allWords[0].length() - 1));
		root.firstChild = new TrieNode(in, null, null);
		if (allWords.length == 1)
			return root;
		for (short i = 1; i < allWords.length; i++) {
			String ins = allWords[i];
			ptr = root.firstChild;
			lastSib = null;
			parent = root;
			boolean makeChild = true;
			short lastIndex = -1;
			short prevLastIndex = -1;
			while (ptr != null) {
				String word = allWords[ptr.substr.wordIndex].substring(0, ptr.substr.endIndex + 1); /*
				if (ptr.firstChild != null) {
					if (!ins.substring(0, word.length()).equals(word)) {
						lastIndex = -1;
						for (short j = 0; j < ins.length(); j++) {
							if ((j < ins.length() && j < word.length()) && word.charAt(j) == ins.charAt(j))
								lastIndex = (short) (j + 1);
							else
								break;
						}
						if (lastIndex != -1 && lastIndex > prevLastIndex) {
							parent = ptr;
							parent.firstChild = new TrieNode((new Indexes(ptr.substr.wordIndex, ptr.substr.startIndex, (short) (lastIndex - 1))), ptr, null);
							ptr.sibling = new TrieNode(new Indexes(i, lastIndex, (short) (ins.length() - 1)), null, null);
							makeChild = false;
							break;
						}
						else {
							if (ptr.sibling != null) {
								ptr = ptr.sibling;
								continue;
							}
							else {
								ptr.sibling = new TrieNode(new Indexes(i, (short)0, (short) (ins.length() - 1)), null, null);
								makeChild = false;
								break;
							}

						}
					}
				} */
				lastIndex = -1;
				for (short j = 0; j < ins.length(); j++) {
					if ((j < ins.length() && j < word.length()) && word.charAt(j) == ins.charAt(j))
						lastIndex = (short) (j + 1);
					else
						break;
				}
				if (lastIndex != -1 && lastIndex > prevLastIndex) {
					if (ptr.firstChild != null) {
						if (lastIndex == word.length()) {
							boolean isRoot = parent == root;
							parent = ptr;
							ptr = ptr.firstChild;
						} else {
							parent.firstChild = new TrieNode(new Indexes(ptr.substr.wordIndex, ptr.substr.startIndex, (short) (lastIndex - 1)), ptr, parent.sibling);
							ptr.substr.startIndex = lastIndex;
							while (ptr.sibling != null)
								ptr = ptr.sibling;
							ptr.sibling = new TrieNode(new Indexes(i, lastIndex, (short) (ins.length() - 1)), null, null);
							makeChild = false;
							break;
						}
					} else {
						boolean isRoot = parent == root;
						parent = ptr;
						ptr = ptr.firstChild;
					}

					prevLastIndex = lastIndex;
					continue;
				} else if (ptr.sibling != null) {
					ptr = ptr.sibling;
					prevLastIndex = lastIndex;
					continue;
				}
					/*ptr.firstChild = new TrieNode((new Indexes(ptr.substr.wordIndex, lastIndex, ptr.substr.endIndex)), null, null);
					ptr.substr.endIndex = (short) (lastIndex-1);
					ptr.firstChild.sibling = new TrieNode((new Indexes(i, lastIndex, (short) (ins.length() - 1))), null, null);
					break;
					 */
				else if (parent.sibling == null && ptr.sibling == null && prevLastIndex != -1) {
					lastSib = parent;
					ptr = null;
				} else {
					lastSib = ptr;
					ptr = ptr.sibling;
					/*while (ptr != null) {
						if (ptr.sibling != null)
							ptr = ptr.sibling;
						else {
							in = new Indexes(i, (short) 0, (short) (allWords[i].length() - 1));
							ptr.sibling = new TrieNode(in, null, null);
							ptr = root.firstChild;
							break;
						}
					}
					 */
				}
				if (ptr == null) {
					if (lastIndex == -1)
						lastIndex = 0;
					in = new Indexes(i, (short) lastIndex, (short) (allWords[i].length() - 1));
					lastSib.sibling = new TrieNode(in, null, null);
					ptr = root.firstChild;
					makeChild = false;
					break;
				}
			}
			if (makeChild) {
				parent.firstChild = new TrieNode((new Indexes(parent.substr.wordIndex, prevLastIndex, (short) (parent.substr.endIndex))), null, null);
				parent.substr.endIndex = (short) (prevLastIndex - 1);
				parent.firstChild.sibling = new TrieNode((new Indexes(i, prevLastIndex, (short) (ins.length() - 1))), null, null);
			}
			print(root, allWords);
		}
		return root;
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
	 * @param root     Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix   Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix,
	 * order of leaf nodes does not matter.
	 * If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
													 String[] allWords, String prefix) {
		String pref = prefix;
		TrieNode ptr = root.firstChild;
		TrieNode parent = root;
		ArrayList<TrieNode> out = new ArrayList<TrieNode>();
		short lastIndex, prevLastIndex = -1;
		int count = 0;
		while (ptr != null) {
			String word = allWords[ptr.substr.wordIndex].substring(0, ptr.substr.endIndex + 1);
			if (word.equals(pref)) {
				if (ptr.firstChild != null) {
					add(ptr.firstChild, out);
				} else {
					out.add(ptr);
				}
				break;
			} else {
				lastIndex = -1;
				for (short j = 0; j < prefix.length(); j++) {
					if ((j < prefix.length() && j < word.length()) && word.charAt(j) == prefix.charAt(j))
						lastIndex = (short) (j + 1);
					else
						break;
				}
				if (lastIndex > prevLastIndex) {
					parent = ptr;
					ptr = ptr.firstChild;
					prevLastIndex = lastIndex;
				} else {
					ptr = ptr.sibling;
					continue;
				}
				if (lastIndex == prefix.length()) {
					if (ptr != null) {
						if (word.equals(pref))
							add(ptr.firstChild, out);
						else
							add(ptr, out);
					}
					else
						add(parent, out);
					break;
				}
				count++;
				continue;
			}
		}
		if (out.size() == 0)
			return null;
		return out;
	}
	private static void add(TrieNode root, ArrayList<TrieNode> out) {
		TrieNode ptr = root;
		while (ptr != null) {
			if (ptr.firstChild == null) {
				out.add(ptr);
			} else
				add(ptr.firstChild, out);
			ptr = ptr.sibling;
		}
	}
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i = 0; i < indent - 1; i++) {
			System.out.print("    ");
		}
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
					.substring(0, root.substr.endIndex + 1);
			System.out.println("      " + pre);
		}
		for (int i = 0; i < indent - 1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		for (TrieNode ptr = root.firstChild; ptr != null; ptr = ptr.sibling) {
			for (int i = 0; i < indent - 1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent + 1, words);
		}
	}
}
