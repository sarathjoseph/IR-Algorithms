import math
from gaussian import mean, sdev, class_prob, pdf, arg_max, compute_mean_sdev, compute_conditionals


if __name__ == "__main__":
    
    m_classes = {}
    data = map(lambda x:x.rstrip(), open("data").readlines())

    for entry in data:
        data_list = entry.split("\t")
        mail_class = data_list[0]
        caps_percentage = float(data_list[1].strip())
     
        if mail_class in m_classes: m_classes[mail_class].append(caps_percentage)
        else :m_classes[mail_class] = [caps_percentage]
                           
    compute_mean_sdev(m_classes)
    
    for m_class in m_classes:
        values = m_classes[m_class]
        class_prob[m_class] = float(len(values)) / len(data)
    
    for input in [7,10,19]:
        print("Predicted class for incoming mail with " + str(input) + "% capital letters - " + arg_max(compute_conditionals(input, m_classes))[0])
    
    
