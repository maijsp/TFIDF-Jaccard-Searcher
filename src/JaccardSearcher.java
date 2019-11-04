//Name: Saranphon Phaithoon, Nuengruethai Wuttisak, Komolmarch Treechaiworapong
//Section: 1,3
//ID: 6088114, 6088138, 6088213

import java.util.*;

public class JaccardSearcher extends Searcher{

	public JaccardSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/

		/***********************************************/
	}

	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/
		List<SearchResult> result = new ArrayList<SearchResult>();
		List<String> token_query = tokenize(queryString);
        //System.out.println(result);
        //System.out.println(token_query);
        double score = -1;
		for (Document doc : documents) {
		    if(!doc.getTokens().isEmpty()|| !token_query.isEmpty()) {
                HashSet<String> upper_inter = new HashSet<>(doc.getTokens());
                HashSet<String> lower_union = new HashSet<>(doc.getTokens());
                upper_inter.retainAll(token_query);
                lower_union.addAll(token_query);
                score = (double) upper_inter.size() / lower_union.size();
                SearchResult docscore = new SearchResult(doc, score);
                result.add(docscore);
            }
            else {
                score = 0.0;
                SearchResult docscore = new SearchResult(doc, score);
                result.add(docscore);
            }
        }
        Collections.sort(result);
		return result.subList(0,k);
		/***********************************************/
	}
}
