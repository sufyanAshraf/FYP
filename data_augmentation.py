"""
Created on Fri Jan 24 22:59:54 2020

@author: sufyan
"""

import matplotlib.pyplot as plt 
import numpy as np 
from keras.preprocessing.image import ImageDataGenerator  
import cv2
from skimage import io


def generate_data(end, folderName,no =20):
    image_count = 1
    fn = 'z/'+str(folderName)+'/'
    saveFolder = 'gen/'+str(folderName)+'/'
    no_gen = no
    
    for j in range (1,end): 
        image_path =  fn+str(j)+'.jpg'  
        
        image = np.expand_dims(io.imread(image_path),0) 
        aug_iter = gen.flow(image) 
        aug_images = [next(aug_iter)[0].astype(np.uint8) for i in range(no_gen)]
        
        for i in range(no_gen):    
            cv2.imwrite(saveFolder+"a%d.jpg" % image_count, cv2.cvtColor(aug_images[i], cv2.COLOR_RGB2BGR))
            image_count+=1
        print(image_count-1) 

gen = ImageDataGenerator(rotation_range=10, height_shift_range=0.1,
        width_shift_range=0.1, zoom_range=0.2, shear_range=0.15,  
        channel_shift_range=20., horizontal_flip=True)

 
generate_data(49,8)
