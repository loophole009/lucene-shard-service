from fastapi import FastAPI, WebSocket, WebSocketDisconnect, Request
from fastapi.responses import HTMLResponse
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
from typing import List
import uvicorn

# === CONFIG ===
#MODEL_PATH = "output/ecommerce-miniLM"

# === LOAD MODEL ===
model = SentenceTransformer("all-MiniLM-L6-v2")

# === FASTAPI SETUP ===
app = FastAPI(title="Embedding Service")

class EmbedRequest(BaseModel):
    texts: List[str]

class EmbedResponse(BaseModel):
    embeddings: List[List[float]]

@app.post("/embed", response_model=EmbedResponse)
async def embed(request: EmbedRequest):
    vectors = model.encode(request.texts, normalize_embeddings=True).tolist()
    return {"embeddings": vectors}

@app.get("/health")
async def health_check():
    return {"status": "ok"}

# === WEBSOCKET ENDPOINT ===
@app.websocket("/ws/embed")
async def websocket_embed(websocket: WebSocket):
    await websocket.accept()
    try:
        while True:
            text = await websocket.receive_text()
            embedding = model.encode([text], normalize_embeddings=True)[0].tolist()
            await websocket.send_json({"embedding": embedding})
    except WebSocketDisconnect:
        print("WebSocket disconnected")
        
        
@app.get("/", response_class=HTMLResponse)
async def get_html():
    return """
    <!DOCTYPE html>
    <html>
    <body>
        <h3>WebSocket Embed Test</h3>
        <input id="text" type="text" />
        <button onclick="send()">Send</button>
        <pre id="result"></pre>
        <script>
            const ws = new WebSocket("ws://localhost:6000/ws/embed");
            ws.onmessage = event => {
                document.getElementById("result").innerText = JSON.stringify(JSON.parse(event.data), null, 2);
            };
            function send() {
                const input = document.getElementById("text").value;
                ws.send(input);
            }
        </script>
    </body>
    </html>
    """


# === RUN APP ===
if __name__ == "__main__":
    uvicorn.run("embed_service:app", host="0.0.0.0", port=6000, reload=False)



## embedding_server.py
#from flask import Flask, request, jsonify
#from sentence_transformers import SentenceTransformer
#
#model = SentenceTransformer('all-MiniLM-L6-v2')  # or any other
#
#app = Flask(__name__)
#
#@app.route('/embed', methods=['POST'])
#def embed():
#    data = request.json
#    sentences = data['texts']
#    embeddings = model.encode(sentences).tolist()
#    return jsonify(embeddings)
#
#if __name__ == '__main__':
#    app.run(port=5000)
