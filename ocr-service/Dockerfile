# 1. Usar una imagen oficial de Python ligera
FROM python:3.10-slim

# 2. Configurar el directorio de trabajo dentro del contenedor
WORKDIR /app

# 3. Instalar Tesseract OCR y el paquete de idioma español a nivel de sistema operativo
RUN apt-get update && \
    apt-get install -y tesseract-ocr tesseract-ocr-spa poppler-utils && \
    rm -rf /var/lib/apt/lists/*

# 4. Copiar las dependencias de Python e instalarlas
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 5. Copiar el resto del código (tu main.py)
COPY . .

# 6. Exponer el puerto 8000 para que pueda comunicarse
EXPOSE 8000

# 7. Comando para arrancar FastAPI con Uvicorn
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
