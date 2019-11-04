//Name: Saranphon Phaithoon, Nuengruethai Wuttisak, Komolmarch Treechaiworapong
//Section: 1,3
//ID: 6088114, 6088138, 6088213

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import org.apache.commons.io.FileUtils;

public class SearcherEvaluator {
	private List<Document> queries = null;				//List of test queries. Each query can be treated as a Document object.
	private Map<Integer, Set<Integer>> answers = null;	//Mapping between query ID and a set of relevant document IDs
	
	public List<Document> getQueries() {
		return queries;
	}

	public Map<Integer, Set<Integer>> getAnswers() {
		return answers;
	}

	/**
	 * Load queries into "queries"
	 * Load corresponding documents into "answers"
	 * Other initialization, depending on your design.
	 * @param corpus
	 */
	public SearcherEvaluator(String corpus)
	{
		String queryFilename = corpus+"/queries.txt";
		String answerFilename = corpus+"/relevance.txt";
		
		//load queries. Treat each query as a document. 
		this.queries = Searcher.parseDocumentFromFile(queryFilename);
		this.answers = new HashMap<Integer, Set<Integer>>();
		//load answers
		try {
			List<String> lines = FileUtils.readLines(new File(answerFilename), "UTF-8");
			for(String line: lines)
			{
				line = line.trim();
				if(line.isEmpty()) continue;
				String[] parts = line.split("\\t");
				Integer qid = Integer.parseInt(parts[0]);
				String[] docIDs = parts[1].trim().split("\\s+");
				Set<Integer> relDocIDs = new HashSet<Integer>();
				for(String docID: docIDs)
				{
					relDocIDs.add(Integer.parseInt(docID));
				}
				this.answers.put(qid, relDocIDs);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Returns an array of 3 numbers: precision, recall, F1, computed from the top *k* search results 
	 * returned from *searcher* for *query*
	 * @param query
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getQueryPRF(Document query, Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/
		//System.out.println(query);
		double[] results = new double[3];
		List<SearchResult> retrieved = searcher.search(query.getRawText(), k);  // Retreived results
		List<Integer> docretreived = new ArrayList<>();							// Retreived document
		int numdocretreived = 0; 	// number of document retreived from searcher
		int numrelevantdoc = 0;		// number of relevant document defined by answer
		for (SearchResult sr: retrieved) {
			docretreived.add(sr.getDocument().getId());
		}
		numdocretreived = docretreived.size();
		numrelevantdoc = answers.get(query.getId()).size();
		// find intersection of retreived and relevant documents
		docretreived.retainAll(answers.get(query.getId()));

		// Calculate precision
		double precision = (double) docretreived.size()/numdocretreived;
		results[0] = precision;
		//System.out.println(precision);
		// Calculate recall
		double recall = (double) docretreived.size()/numrelevantdoc;
		results[1] = recall;
		//System.out.println(recall);
		// Calculate f1
		double f1;
		if(precision == 0 && recall == 0) {
			f1 = 0.0;
		}
		else {
			f1 = (double) 2 * precision * recall / (precision+recall);
		}
		results[2] = f1;
		//System.out.println(f1);
		return results;
		/****************************************************************/
	}
	
	/**
	 * Test all the queries in *queries*, from the top *k* search results returned by *searcher*
	 * and take the average of the precision, recall, and F1. 
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getAveragePRF(Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/
		int querysize = queries.size();
		double[] temp = new double[3];
		double[] results = new double[3];
		for (Document doc: queries) {
			temp = this.getQueryPRF(doc, searcher, k);
			results[0] += temp[0];
			results[1] += temp[1];
			results[2] += temp[2];
		}
		for (int i = 0; i < results.length; i++) {
			results[i] /= querysize;
		}
		return results;
		/****************************************************************/
	}
}
