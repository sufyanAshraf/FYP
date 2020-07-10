# -*- coding: utf-8 -*-
"""
Created on Tue Apr 01 17:39:23 2020

@author: sufyan

run it 'new' envirnment it 
you no need to convert you model in graph_def
"""

import tensorflow as tf
from tensorflow.keras.models import load_model


model = load_model("model_1.h5")

converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite = converter.convert()
open("model_1.tflite","wb").write(tflite)