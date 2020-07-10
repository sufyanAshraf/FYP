import tensorflow as tf
from keras.applications.vgg19 import VGG19  
from keras.layers import Dense, Flatten
from keras.models import Model
from keras.preprocessing.image import ImageDataGenerator
import numpy as np
import matplotlib.pyplot as plt
from tensorflow.keras.callbacks import EarlyStopping 
from glob import glob
import pickle


train_path = 'data_new'
valid_path = 'valid'


def create_model():

    vgg = VGG19(input_shape=[224, 224] + [3], weights='imagenet', include_top=False)

    
    a = 0
    for layer in vgg.layers:
        if a== 0:
            layer.name="input"
            a+=1
        layer.trainable = False
        
    # get number of classes
    folders = glob('valid/*')
      
    # new layers added
    x = Flatten()(vgg.output)
    
    layer = Dense(len(folders), activation='softmax', name='dense')(x)
    
    # create a model object
    model = Model(inputs=vgg.input, outputs=layer)
     
    # model.summary()
    
    # tell the model what cost and optimization method to use
    model.compile(
      loss='categorical_crossentropy',
      optimizer='adam',
      metrics=['acc']
    )
    return model


model = create_model()

# Use the Image Data Generator to import the images from the dataset

train_datagen = ImageDataGenerator(zoom_range = 0.2, 
                                   rescale = 1./255,
                                   shear_range = 0.2,
                                   horizontal_flip = True)

test_datagen = ImageDataGenerator(rescale = 1./255)

training_set = train_datagen.flow_from_directory(train_path,
                                                 target_size = (224, 224),
                                                 batch_size = 32,
                                                 class_mode = 'categorical')

valid_set = test_datagen.flow_from_directory(valid_path,
                                            target_size = (224, 224),
                                            batch_size = 32,
                                            class_mode = 'categorical')

earlystop = EarlyStopping(monitor='val_acc', 
                          min_delta=0, #threshold
                          patience=5, 
                          verbose=0, # what to print, default (0)
                          mode='auto') #direction of monitored quantity (min) for loss
 
cp_callback = tf.keras.callbacks.ModelCheckpoint(filepath="out/ck/cp.ckpt",
                                                 save_weights_only=True, period=2, verbose=1)
callbacks = [cp_callback, earlystop]
r = model.fit_generator(training_set, 
                        validation_data=valid_set,
                        steps_per_epoch=len(training_set),
                        epochs=100,
                        validation_steps=len(valid_set), 
                        callbacks=callbacks) 

#accuracies
plt.plot(r.history['acc'], label='train acc')
plt.plot(r.history['val_acc'], label='val acc')
plt.legend()
plt.show()
plt.savefig('out/AccVal_acc')

# loss
plt.plot(r.history['loss'], label='train loss')
plt.plot(r.history['val_loss'], label='val loss')
plt.legend()
plt.show()
plt.savefig('out/LossVal_loss')


model.save('out/model_1.h5')
model.save('out/Model_1')      
model.save_weights('out/weights_model_1.h5')  

with open('out/model.json', 'w') as f:
    f.write(model.to_json())
 
file = open( "out/model_1_acc",'wb') 
pickle.dump(list(r.history['acc']), file) 
file.close()

file = open( "out/model_1_loss",'wb') 
pickle.dump(list(r.history['loss']), file) 
file.close()  

file = open( "out/model_1_val_acc",'wb') 
pickle.dump(list(r.history['val_acc']), file) 
file.close() 

file = open( "out/model_1_val_loss",'wb') 
pickle.dump(list(r.history['val_loss']), file) 
file.close() 
