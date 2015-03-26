import java.util.*;

public class NBClassifier {

	HashSet<String> Vocab;
	int[] classcount;
	String[] training_set;
	HashMap<String,double[]>condProb;
	int numClass;
	
	public NBClassifier(String[] trainDocs, int[] trainLabels, int numClass) {
		condProb=new HashMap<String,double[]>();
		training_set = trainDocs;
		Vocab = new HashSet<String>();
		this.numClass=numClass;
		classcount = new int[numClass];
		
		for (int i = 0; i < trainDocs.length; i++) {

			String[] terms = trainDocs[i].split(" ");		
			for (String term : terms) {
				
				Vocab.add(term);
				classcount[trainLabels[i]]++;
				if(!condProb.containsKey(term)){
					double[] p=new double[numClass];					
					p[trainLabels[i]]=2.0;
					condProb.put(term,p);
				}
				else{
					double[]p=condProb.get(term);		
					p[trainLabels[i]]++;
					condProb.put(term,p);
				}
			}
		}
		
		for(String term:Vocab){
	
			for(int k=0;k<numClass;k++){
				double[] temp=condProb.get(term);
				double prob=condProb.get(term)[k]/(classcount[k]+Vocab.size());
				temp[k]=prob;
				condProb.put(term,temp);
			}
			
		}
	
	}

	public int classfiy(String doc) {
		String[] terms=doc.split(" ");
		double[]doc_scores=new double[numClass];
		for(int k=0;k<numClass;k++){
	
		double score=Math.log(classcount[k]/training_set.length);
		
		for(String term:terms){
			if(condProb.containsKey(term)){
				if(condProb.get(term)[k]==0.0) //In the case it is not in this class
					score+=Math.log(1.0/(classcount[k]+Vocab.size()));	
				else
				score+=Math.log(condProb.get(term)[k]);
			}
			else{
				score+=Math.log(1.0/(classcount[k]+Vocab.size()));
			}
		}
		doc_scores[k]=score;
		}
		
		int result=0;
		double higher=-1000.0; //to account for neg log values log values
		for(int r=0;r<doc_scores.length;r++){
			
			if(doc_scores[r]>higher){
		
				result=r;
				higher=doc_scores[r];
			}
				
		}
		return result;
	}

	public static void main(String[] args) {
		
		String[] trainDocs = { "RIT MessageCenter Housing",
				"RIT Flash sale survey", "RIT News Events survey",
				"RIT research Barnes&Nobles deals",
				"Lottery Viagra dating sale survey deals" };

		int[] trainLabels = { 0, 0, 0, 0, 1 };// corresponds
												// to{"Ham","Ham","Ham","Spam"};
		int numClass = 2;

		NBClassifier nb = new NBClassifier(trainDocs, trainLabels, numClass);
		String testDoc = "deals Lottery Flash Viagra";
		if(nb.classfiy(testDoc)==0)
			System.out.println("HAM");
		else
			System.out.println("SPAM");
	}
}
