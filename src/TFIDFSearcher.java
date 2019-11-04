//Name: Saranphon Phaithoon, Nuengruethai Wuttisak, Komolmarch Treechaiworapong
//Section: 1,3
//ID: 6088114, 6088138, 6088213

import com.sun.codemodel.internal.JCommentPart;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import org.w3c.dom.ls.LSOutput;

import javax.print.Doc;
import java.lang.reflect.Array;
import java.util.*;

public class TFIDFSearcher extends Searcher
{
	HashSet<String> allterm; 									// keep all terms from whole documents
	LinkedHashMap<String, Double> idfmap;						// map term -> idf weight
	HashMap<Integer, double[]> docToVector = new HashMap<>();	// map docId -> document vector
	HashMap<Integer, Double> docLength = new HashMap<>(); 		// map docId -> |d|
	List<String> tindex;										// term index
	int numdoc = documents.size();								// N documents from the hole corpus
	/**
	 * Create TFIDF constructor -> initialize idfmap, docToVector, docLength
	 * @param docFilename
	 */
	public TFIDFSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/
		/*
		Compute all term from whole documents
		 */
		allterm = new HashSet<>();
		idfmap = new LinkedHashMap<>();
		for (Document doc : documents) {
			allterm.addAll(doc.getTokens());
			/*
			Compute idf raw into idfmap
		 	*/
			Set<String> distinctTerm = new HashSet<>(doc.getTokens());
			for (String s : distinctTerm) {
				double idfraw = 1.0;
				if(idfmap.containsKey(s)) {
					// document contain term s
					idfraw = idfmap.get(s)+1.0; // increment
					idfmap.replace(s, idfraw);
				} else {
					// new term in idf
					idfmap.put(s, 1.0);
				}
			}
		}
		/*
		 normalize by using formula log10(1+N/df)
		 */
		for (String s :idfmap.keySet()) {
			double idf = Math.log10(1+(numdoc/idfmap.get(s)));
			idfmap.replace(s, idf);
		}

		tindex = new ArrayList<String>(idfmap.keySet());

		/*
		Create document vector for each document
		 */
		double[] vector;
		for (Document doc : documents) {
			vector = new double[allterm.size()];
			double sum = 0;
			for (String s : doc.getTokens()) {
				double tfidf = tfidf(doc.getTokens(), s);
				vector[tindex.indexOf(s)] = tfidf;
			}
			docToVector.put(doc.getId(), vector);
			for (int i = 0; i < vector.length; i++) {
				sum += Math.pow(vector[i], 2);
			}
			double doclength = Math.sqrt(sum);
			docLength.put(doc.getId(), doclength);
		}
		/***********************************************/
	}
	/**
	 *
	 * @param totalterm - to input all total terms in a document
	 * @param term - check term
	 * @return tfweight value
	 */
	public double tfweight(List<String> totalterm, String term) {
		double tfweight = 0.0;
		double tfraw;
		if(totalterm.contains(term)) {
			tfraw = Collections.frequency(totalterm, term);
			return 1+Math.log10(tfraw);
		}
		return tfweight;
	}

	/**
	 *
	 * @param totalterm - to input all total terms
	 * @param term		- check term
	 * @return tf*idf weight
	 */
	public double tfidf(List<String> totalterm, String term) {
		double tf = tfweight(totalterm, term);
		//System.out.println("term" + term);
		double idf = 0;
		if(idfmap.get(term) != null) {
			idf = idfmap.get(term);
		}
		//System.out.println("tfidf" + tf*idf);
		return tf*idf;
	}

	/**
	 *
	 * @param queryvector 	- input query vector
	 * @param docvector		- input document vector
	 * @param querylength	- input query vector length |q|
	 * @param doclength		- input document vector length |d|
	 * @return cosine(q,d)
	 */
	public double cosinesim(double[] queryvector, double[] docvector, double querylength, double doclength) {
		double result = 0.0;
		for (int i = 0; i < queryvector.length; i++) {
			result += queryvector[i]*docvector[i];
		}
		result /= (doclength*querylength);
		return result;
	}
	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/
		List<SearchResult> results = new ArrayList<>(); 	// results of search
		List<String> token_query = tokenize(queryString);	// tokenize the queryString
		// Compute tf for query
		double q[] = new double[allterm.size()];
		for (String token: token_query) {
			if(tindex.indexOf(token) != -1){
				q[tindex.indexOf(token)] = tfidf(token_query, token);
			}
		}
		double sum = 0.0;
		for(int i = 0; i < q.length; i++) {
			sum += Math.pow(q[i],2);
		}
		double qlength = Math.sqrt(sum);

		for (Document doc : documents) {
			double cosine = cosinesim(q, docToVector.get(doc.getId()), qlength, docLength.get(doc.getId()));
			SearchResult temp = new SearchResult(doc, cosine);
			results.add(temp);
		}
		Collections.sort(results);
		/***********************************************/
		return results.subList(0,k);
	}
}
