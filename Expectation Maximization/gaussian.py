import math

mean = {}
sdev = {}
class_prob = {}

# probability density function 
def pdf(mean, sd, x):
 
 numerator = math.pow(math.e, -(x - mean) ** 2 / (2 * sd ** 2))
 return (1.0 / (math.sqrt(2 * math.pi) * sd)) * numerator

def compute_conditionals(x, distributions):
    # probability of class given x
    prob_class_given_x = {}
    total = 0.0
    
    for gaussian in distributions:
        prob_class_given_x[gaussian] = pdf(mean[gaussian], sdev[gaussian], x) * class_prob[gaussian]
        
        total += prob_class_given_x[gaussian]
 
    for gaussian in distributions:
        prob_class_given_x[gaussian] = prob_class_given_x[gaussian] / total

    return prob_class_given_x

def arg_max(p):
    
    return sorted(zip(p.keys(), p.values()), key=lambda x:x[1], reverse=True)[0]

def compute_mean_sdev(distributions):
    
    for gaussian in distributions:
        values = distributions[gaussian]
        mean[gaussian] = (sum(values)) / len(values)
        sdev[gaussian] = (sum([(x - mean[gaussian]) ** 2 for x in values]) / (len(values) - 1)) ** 0.5
       