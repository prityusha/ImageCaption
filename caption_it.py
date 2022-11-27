# -*- coding: utf-8 -*-
"""Untitled9.ipynb

Automatically generated by Colaboratory.

Original file is located at
    https://colab.research.google.com/drive/1lSyuoro4P_dgEJ9GpRMSNXkjMezql_Ot
"""

import os
import PIL
import numpy as np
import requests
from tqdm.notebook import tqdm

from tensorflow.keras.applications.vgg16 import VGG16, preprocess_input
from tensorflow.keras.preprocessing.image import load_img, img_to_array
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.models import Model
from tensorflow.keras.utils import to_categorical, plot_model
from tensorflow.keras.layers import Input, Dense, LSTM, Embedding, Dropout, add
from tensorflow.keras.models import load_model

def idx_to_word(integer, tokenizer):
    for word, index in tokenizer.word_index.items():
        if index == integer:
            return word
    return None

def pre_run_this_code(photo):
  BASE_DIR = 'dataset'
  with open(os.path.join(BASE_DIR, 'captions.txt'), 'r') as f:
    next(f)
    captions_doc = f.read()
  
  # create mapping of image to captions
  mapping = {}

  # process lines
  for line in tqdm(captions_doc.split('\n')):
      # split the line by comma(,)
      tokens = line.split(',')
      if len(line) < 2:
          continue
      image_id, caption = tokens[0], tokens[1:]
      # remove extension from image ID
      image_id = image_id.split('.')[0]
      # convert caption list to string
      caption = " ".join(caption)
      # create list if needed
      if image_id not in mapping:
          mapping[image_id] = []
      # store the caption
      mapping[image_id].append(caption)

  for key, captions in mapping.items():
      for i in range(len(captions)):
          # take one caption at a time
          caption = captions[i]
          # preprocessing steps
          # convert to lowercase
          caption = caption.lower()
          # delete digits, special chars, etc., 
          caption = caption.replace('[^A-Za-z]', '')
          # delete additional spaces
          caption = caption.replace('\s+', ' ')
          # add start and end tags to the caption
          caption = 'startseq ' + " ".join([word for word in caption.split() if len(word)>1]) + ' endseq'
          captions[i] = caption

  all_captions = []
  for key in mapping:
      for caption in mapping[key]:
          all_captions.append(caption)


  # tokenize the text
  tokenizer = Tokenizer()
  tokenizer.fit_on_texts(all_captions)
  vocab_size = len(tokenizer.word_index) + 1

  max_length = max(len(caption.split()) for caption in all_captions)

  image_ids = list(mapping.keys())
  split = int(len(image_ids) * 0.90)
  train = image_ids[:split]
  test = image_ids[split:]


  inputs1 = Input(shape=(4096,))
  fe1 = Dropout(0.4)(inputs1)
  fe2 = Dense(256, activation='relu')(fe1)
  # sequence feature layers
  inputs2 = Input(shape=(max_length,))
  se1 = Embedding(vocab_size, 256, mask_zero=True)(inputs2)
  se2 = Dropout(0.4)(se1)
  se3 = LSTM(256)(se2)

  # decoder model
  decoder1 = add([fe2, se3])
  decoder2 = Dense(256, activation='relu')(decoder1)
  outputs = Dense(vocab_size, activation='softmax')(decoder2)

  model = Model(inputs=[inputs1, inputs2], outputs=outputs)
  model.compile(loss='categorical_crossentropy', optimizer='adam')

  model = load_model('best_model.h5')
  description = predict_caption(model, photo, tokenizer, max_length)

  return description
  

def predict_caption(model, image, tokenizer, max_length):
    # add start tag for generation process
    in_text = 'startseq'
    # iterate over the max length of sequence
    for i in range(max_length):
        # encode input sequence
        sequence = tokenizer.texts_to_sequences([in_text])[0]
        # pad the sequence
        sequence = pad_sequences([sequence], max_length)
        # predict next word
        yhat = model.predict([image, sequence], verbose=0)
        # get index with high probability
        yhat = np.argmax(yhat)
        # convert index to word
        word = idx_to_word(yhat, tokenizer)
        # stop if word not found
        if word is None:
            break
        # append word as input for generating next word
        in_text += " " + word
        # stop if we reach end tag
        if word == 'endseq':
            break
      
    return in_text

def extract_features(filename):
	# load the model
	model = VGG16()
	# re-structure the model
	model.layers.pop()
	model = Model(inputs=model.inputs, outputs=model.layers[-2].output)
	# load the photo
	image = load_img(filename, target_size=(224, 224))
	# convert the image pixels to a numpy array
	image = img_to_array(image)
	# reshape data for the model
	image = image.reshape((1, image.shape[0], image.shape[1], image.shape[2]))
	# prepare the image for the VGG model
	image = preprocess_input(image)
	# get features
	feature = model.predict(image, verbose=0)
	return feature

def show_caption(image_url):
  
  #!wget -O img1.jpg https://firebasestorage.googleapis.com/v0/b/imagecaption-f60dd.appspot.com/o/pictures%2FJPEG_20221120_151238_6669102990747990536.jpg?alt=media&token=86592fbf-e2df-4914-82c7-83f2f686b8c6
  
  img_data = requests.get(image_url).content
  with open('img1.jpg', 'wb') as handler:
      handler.write(img_data)
  #!wget -O img1.jpg image_link
  photo = extract_features('img1.jpg')
  # generate description
  
  description = pre_run_this_code(photo)

  query = description
  stopwords = ['startseq','endseq']
  querywords = query.split()

  resultwords  = [word for word in querywords if word.lower() not in stopwords]
  result = ' '.join(resultwords)

  return result

