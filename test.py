# -*- coding: utf-8 -*-
"""
Created on Tue Apr 21 17:02:46 2020

@author: sufyan
"""

import cv2
from tensorflow.keras.models import model_from_json
import numpy as np

json_file = open('out/model.json', 'r')
loaded_model_json = json_file.read()
json_file.close()
new_model = model_from_json(loaded_model_json)
# load weights into new model
new_model.load_weights('out/weights_model_1.h5')  



    
a = ["other",'lahore fort', 'minar-e-pakistan', 'badshahi mosque', "Diwan-i-Am", "guest house", "Naulakha Pavilion","Sheesh Mahal","tomb of Iqbal" ]    

for j in range(11,12):
    img = cv2.imread('t2/'+str(j)+'.jpg')
    
    img = cv2.resize(img, dsize=(224, 224))
    img = np.array(img).reshape(-1, 224, 224, 3)
    img = img/255.0
    
    # new_model.summary()
    output = new_model.predict(img)
    
    # print(output)
    
        
    temp = -255
    c = 0
    target =0
    for i in output[0]:
        
        if i > temp:
            temp = i
            target =c  
        c+=1
 

    print(a[target])
