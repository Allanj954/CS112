package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		
		HashMap<String, Occurrence> map = new HashMap<String, Occurrence>();
		if (docFile == null){
			throw new FileNotFoundException();
		}
		Scanner scan = new Scanner(new File(docFile));
		
		while(scan.hasNext()){
			String key = getKeyword(scan.next());
			if (key != null) {
				if (map.containsKey(key))
				{
					Occurrence o = map.get(key);
					o.frequency++;
				}
				else
				{
					Occurrence o = new Occurrence(docFile, 1);
					map.put(key, o);
				}
			}
		}
		return map;
	}
	
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		for (String key : kws.keySet())
		{
			ArrayList<Occurrence> occur = new ArrayList<Occurrence>();

			if (keywordsIndex.containsKey(key))
				occur = keywordsIndex.get(key);
			
			occur.add(kws.get(key));
			insertLastOccurrence(occur);
			keywordsIndex.put(key, occur);
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) { 
		if (word == null) {
			return null;
		}
		word = word.toLowerCase();
		
		
		if (hasPunctuation(word)) {
			return null;
		}
		
		word = removeTrailingPunc(word);
		
		if (hasDigits(word)) {
			return null;
		}
		
		if (noiseWords.contains(word)) {
			return null;
		}
		
		if (word.length() <= 0) {
			return null;
		}
		return word;
	}
	
	private boolean hasPunctuation(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (word.charAt(i) == '.' || word.charAt(i) == '!' || word.charAt(i) == ',' || word.charAt(i) == '?' || word.charAt(i) == ':' || word.charAt(i) == ';') {
				return true;
			}
		}
		return false;
	}
	
	private boolean hasDigits(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (Character.isDigit(word.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	private String removeTrailingPunc(String word)
	{
		int count = 0;
		while (count < word.length())
		{
			char c = word.charAt(count);
			if (!(Character.isLetter(c)))
				break;
			count++;
		}
		return word.substring(0, count);
	}
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		ArrayList<Integer> midpoints = new ArrayList<Integer>();		
		int low = 0;
		int high = occs.size() - 2;
		int mid = (low + high) / 2;
		Occurrence target = occs.get(occs.size() - 1);
		while (low <= high ) {
			mid = (low + high) / 2;
			midpoints.add(mid);
			if (occs.get(mid).frequency == target.frequency) {
				break;
			}
			
			else if (target.frequency < occs.get(mid).frequency) {
				low = mid + 1;
			}
			
			else {
				high = mid - 1;
			}
		}
		midpoints.add(mid);
		Occurrence last = occs.remove(occs.size()-1);
		occs.add(midpoints.get(midpoints.size()-1), last);
		return midpoints;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
	
		ArrayList<Occurrence> oc1 = new ArrayList<Occurrence>();
		ArrayList<Occurrence> oc2 = new ArrayList<Occurrence>();
		ArrayList<Occurrence> combined = new ArrayList<Occurrence>();
		
		if (keywordsIndex.containsKey(kw1))
			oc1 = keywordsIndex.get(kw1);
		
		if (keywordsIndex.containsKey(kw2))
			oc2 = keywordsIndex.get(kw2);
		
		combined.addAll(oc1);
		combined.addAll(oc2);
		
		if (!(oc1.isEmpty()) && !(oc2.isEmpty()))
		{
			// Sort with preference for ocArr1
			for (int i = 0; i < combined.size()-1; i++)
			{
				for (int y = 1; y < combined.size()-i; y++)
				{
					if (combined.get(y-1).frequency < combined.get(y).frequency)
					{
						Occurrence temp = combined.get(y-1);
						combined.set(y-1, combined.get(y));
						combined.set(y,  temp);
					}
				}
			}

			// Remove duplicates
			for (int a = 0; a < combined.size()-1; a++)
			{
				for (int y = a + 1; y < combined.size(); y++)
				{
					if (combined.get(a).document == combined.get(y).document)
						combined.remove(y);
				}
			}
		}

		// Top 5
		while (combined.size() > 5)
			combined.remove(combined.size()-1);
		
		ArrayList<String> result = new ArrayList<String>();
		for (Occurrence oc : combined)
			result.add(oc.document);

		return result;
	}
}
