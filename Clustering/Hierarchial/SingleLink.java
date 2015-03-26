package Lab5;



import java.util.*;



public class SingleLink {


	
	String[] myDocs;
	ArrayList<String> termList;
	ArrayList<ArrayList<Doc>> docLists;
	double[] docLength;
	int N;
	
    ArrayList<String[]>document;
    
	public SingleLink(int num) {
		N = num;

	}

	
	public void preprocess(String[] docs) {
		myDocs = docs;
		document=new ArrayList<String[]>();
		termList = new ArrayList<String>();
		docLists = new ArrayList<ArrayList<Doc>>();
		ArrayList<Doc> docList;
		
		for (int i = 0; i < myDocs.length; i++) {
			
			String[] tokens = myDocs[i].split(" ");
			
			document.add(i, tokens);
			String token;
			
			
			for (int j = 0; j < tokens.length; j++) {
				token = tokens[j];
				if (!termList.contains(token)) {
					termList.add(token);
					docList = new ArrayList<Doc>();
					Doc doc = new Doc(i, 1); 
					docList.add(doc);
					docLists.add(docList);
				} else {
					int index = termList.indexOf(token);
					docList = docLists.get(index);

					boolean match = false;

					
					for (Doc doc : docList) {
						if (doc.docId == i) {
							doc.tw++; 
							match = true;
							break;
						}
						
					}
					
					if (!match) {
						Doc doc = new Doc(i, 1);
						docList.add(doc);
					}
				}
			}
		}
		
		int N = myDocs.length;
		docLength = new double[N];
		for (int i = 0; i < termList.size(); i++) {
			docList = docLists.get(i);
			//int df = docList.size();
			Doc doc;
			for (int j = 0; j < docList.size(); j++) {
				doc = docList.get(j);
				//double tfidf = (1 + Math.log(doc.tw))* Math.log(N / (df * 1.0));
				double tfidf = (1 + Math.log(doc.tw));
				docLength[doc.docId] += Math.pow(tfidf, 2);
				doc.tw = tfidf;
				// docList.set(j, doc);
			}
		}
		// update the length
		for (int i = 0; i < N; i++) {
			docLength[i] = Math.sqrt(docLength[i]);
		}
	
		for (int i = 0; i < termList.size(); i++) {
			docList = docLists.get(i);
			Doc doc;
			for (int j = 0; j < docList.size(); j++) {
				doc = docList.get(j);
				doc.tw = doc.tw / docLength[doc.docId];
				

			}
		}
		
		 
		
	}

	public double getSim(ArrayList<Integer>cluster1,ArrayList<Integer> cluster2) {
		
		
		
		double max_cosine=0.0;
		
		for(int docid1:cluster1){
			
			for(int docid2:cluster2){
				
				double score=0.0;
				for(String s:document.get(docid1)){
					
					ArrayList<Doc>doclist;
					int index=termList.indexOf(s);
					doclist=docLists.get(index);
					
					double doc1score=0.0;
					for(Doc doc:doclist){
						
						if(doc.docId==docid1){
							
							doc1score=doc.tw;
							break;
						}
					}
					
					for(Doc doc:doclist){
						
						if(doc.docId==docid2){
							
							score+=doc.tw*doc1score;
							break;
						}
					}
					
					
				}
				
				if(score>max_cosine)
				max_cosine=score;
				
				
			}
			
			
		}
		
		
		
		return max_cosine;
	}

	public void cluster() {

	
		ArrayList<ArrayList<Integer>>clusters=new ArrayList<ArrayList<Integer>>();
		
		for(int i=0;i<myDocs.length;i++){
			
			ArrayList<Integer> c=new ArrayList<Integer>();
			c.add(i);
			clusters.add(c);
		}
		
		int cut=clusters.size();
		
		while(cut>N){
			System.out.println(clusters);
		double higher=0.0;
		int[]c=new int[2]; // c holds values of clusters to merge
		for(int i=0;i<clusters.size();i++){
			
			
			
			ArrayList<Integer>cluster1=clusters.get(i);
			
				for(int j=0;j<clusters.size();j++){
					
					
					if(i!=j){
						
						ArrayList<Integer>cluster2=clusters.get(j);		
						double sim=getSim(cluster1,cluster2);
						if(sim>higher){
							
							higher=sim;
							c[0]=i;
							c[1]=j;
						}
						
					}
					
				}		
			
			
		}
		
		ArrayList<Integer>merged=clusters.get(c[0]);
		
		for(int document:clusters.get(c[1])){
			
			
			merged.add(document);
		}
		
		clusters.remove(c[1]);
		
		cut--;
		
		
		}
		
		
		System.out.println(clusters);
		
	}

	

	


	public static void main(String[] args) {
		
		
		String[] docs = {
				"hot chocolate cocoa beans", 
				"cocoa ghana africa",
				"beans harvest ghana",
				"cocoa butter", 
				"butter truffles",
				"sweet chocolate can", 
				"brazil sweet sugar can",
				"sugar can brazil", 
				"sweet cake icing",
				"cake black forest" };
		
		//Mention the cut as the constructor parameter
		SingleLink sl = new SingleLink(2);

		sl.preprocess(docs);
		sl.cluster();
		/*
		 * Expected result: 
		 * 
		 * Cluster: 0 	0 1 2 3 4 
		 * Cluster: 1 	5 6 7 8 9
		 */
	}
}

