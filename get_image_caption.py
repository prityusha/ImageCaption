from flask import Flask, request
import caption_it

#captionText = caption_it.show_caption("https://images.unsplash.com/photo-1476994230281-1448088947db?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80")
#print(captionText)

app = Flask(__name__)

@app.route('/')
def home():
    return "bruh"

@app.route('/predict',methods=['POST'])
def predict():
    base_image_url = request.form.get('imageUrl')

    captionText = caption_it.show_caption(base_image_url)
    return captionText


if __name__ == '__main__':
    app.run(debug=False, host='0.0.0.0')