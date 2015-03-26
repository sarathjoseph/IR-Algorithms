import math
from gaussian import mean,sdev,class_prob, pdf,arg_max, compute_mean_sdev,compute_conditionals
from random import shuffle
import time


def make_empty_cluster(cluster):
    d={}
    for key in cluster.keys():d[key]=[]
    return d

if __name__=="__main__":
    
    # Number of clusters
    K=3
    clusters={}
        
    data=map(lambda x:x.rstrip(),open("data").readlines())
    data_points=[]
    for entry in data:
            data_points.append(float(entry.split("\t")[1].strip()))
    
    #initialize probabilities and assign points to clusters randomly
    #shuffle(data_points)
    n=len(data_points)
    partioned_data=[ data_points[i::K] for i in range(K)]
    
    for i in range(K):
        class_prob[i]=1.0/K
        clusters[i]=partioned_data[i]
    
    compute_mean_sdev(clusters)
    
    start = time.time()
    
    # Number of iterations 
    N=3000
    
    for x in range(N):
        
        updated_clusters=make_empty_cluster(clusters)
        
    # calculates the sum of conditionals * data_point (Used in later calutations)
        total_ci_xi=make_empty_cluster(clusters)
    # calculates the sum of conditionals themselves(For use in calculations later)
        total_ci=make_empty_cluster(clusters)
        conditionals_map={}
        for cluster in clusters:
            for point in clusters[cluster]:
                    results=compute_conditionals(point,clusters)
                    
                    gaussian=arg_max(results)[0]
                    updated_clusters[gaussian].append(point)
                
                    for c in results:
                        ci=results[c]
                        # The key is a tuple that hashes to a unique value for conditional
                        conditionals_map[(point,c)]=ci
                        total_ci[c].append(ci)
                        total_ci_xi[c].append(ci*point)
   
    # update mean and priors            
        for gaussian in mean:
            s=sum(total_ci[gaussian])
            mean[gaussian]=sum(total_ci_xi[gaussian])/s
            class_prob[gaussian]=s/len(total_ci[gaussian])
       
    # update variance (std dev) 
        for gaussian in sdev:
            
            #numerator=sum([compute_conditionals(p,clusters)[gaussian]*((p-mean[gaussian])**2) for p in data_points])
            numerator=sum([conditionals_map[(p,gaussian)]*((p-mean[gaussian])**2) for p in data_points])
            
            sdev[gaussian]=(numerator/sum(total_ci[gaussian]))**0.5
        
          # update clusters 
        clusters=updated_clusters
    
    end= time.time()
   
    print("Time take for "+str(N) +" iterations : "+str(round(end-start,2))+" seconds\n")
    print("Converged Mean is ")
    print(mean) 
    
    print("\nCluster 0 , 1 and 2 members respectively")
    for cluster in clusters:
        print(sorted(clusters[cluster]))
        
       
       