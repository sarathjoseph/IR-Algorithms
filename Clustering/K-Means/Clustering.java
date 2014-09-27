package Lab5;

import java.util.*;



public class Clustering {


	
	String[] myDocs;
	ArrayList<String> termList;
	ArrayList<ArrayList<Doc>> docLists;
	double[] docLength;
	int N;
	
    ArrayList<String[]>document;
    
	public Clustering(int num) {
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
							doc.tw++; // increase word count
							match = true;
							break;
						}

					}
					// if no match, add a new document id along with the
					// position number
					if (!match) {
						Doc doc = new Doc(i, 1);
						docList.add(doc);
					}
				}
			}
		}// end with parsing

		// LBE07: compute the tf-idf term weights and the doc lengths
		int N = myDocs.length;
		docLength = new double[N];
		for (int i = 0; i < termList.size(); i++) {
			docList = docLists.get(i);
			int df = docList.size();
			Doc doc;
			for (int j = 0; j < docList.size(); j++) {
				doc = docList.get(j);
				double tfidf = (1 + Math.log(doc.tw))* Math.log(N / (df * 1.0));
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

	public double computeCosine(int documentId,ArrayList<Integer> cluster) {
			
		double cosine_sim=0.0;
	
		for(int id:cluster){  
					
			for(String s:document.get(id)){
				
				ArrayList<Doc>doclist;
				int index=termList.indexOf(s);
				doclist=docLists.get(index);
				
				//get input docId score
				double doc_score=0.0;
				for(Doc doc:doclist){
					
					if(doc.docId==documentId){
						doc_score=doc.tw;
						break;
					}
					
				}
				
				for(Doc doc:doclist){
					
					if(id==doc.docId){
						
						cosine_sim+=doc.tw*doc_score;
						break;
						
					}
					
				}
				
			}		
			
		}
	
		return cosine_sim/cluster.size();
	}
	
	
	public void cluster() {

		ArrayList<Integer>c1=new ArrayList<Integer>();
		ArrayList<Integer>c2=new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>>clusters=new ArrayList<ArrayList<Integer>>();
		
		//Intialize two clusters
		
		c1.add(0);
		c2.add(8);
		
		clusters.add(c1);
		clusters.add(c2);
		
		Boolean change=true;
		int iter=0;
		System.out.println(clusters);
		while(change){
			

		//	System.out.println(clusters);
		iter++;
		System.out.println("Iteration "+iter);
		change=false;	
		
		for(int s=0;s<myDocs.length;s++){
			
			Double higher=0.0;
			Double cosine=0.0;
			int cluster_num=0;
			for(int r=0;r<N;r++){
				
			cosine=computeCosine(s,clusters.get(r));
			if(cosine>higher){
				higher=cosine;
				cluster_num=r;
			}
			
			}
			ArrayList<Integer>c=clusters.get(cluster_num);
			
			if(!c.contains(s)){
				for(int r=0;r<N;r++){
					ArrayList<Integer>cluster=clusters.get(r);
					if(cluster.contains(s)){
					cluster.remove(s);
					break;
					}
				}
				c.add(s);
				System.out.println(clusters);
				change=true;
			}
			
			
			
			
			}
		
		}	
			
		System.out.println(clusters);
		System.out.println("\nNumber of iterations:"+iter);
		

	}

	public String toString() {
		String matrixString = new String();
		ArrayList<Doc> docList;
		for (int i = 0; i < termList.size(); i++) {
			matrixString += String.format("%-15s", termList.get(i));
			docList = docLists.get(i);
			for (int j = 0; j < docList.size(); j++) {
				matrixString += docList.get(j) + "\t";
			}
			matrixString += "\n";
		}
		return matrixString;
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
		
		Clustering c = new Clustering(2);

		c.preprocess(docs);
		c.cluster();
		/* Expected result: 
		 * 
		 * Cluster: 0 	0 1 2 3 4 
		 * Cluster: 1 	5 6 7 8 9
		 */
	}
}

/**
 * 
 * @author qyuvks Document class for the vector representation of a document
 */
class Doc {

	int docId;
	double tw;

	public Doc(int did, double weight) {
		docId = did;
		tw = weight;
	}

	public String toString() {
		String docIdString = docId + ":" + tw;
		return docIdString;
	}
}