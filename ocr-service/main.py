from fastapi import FastAPI, UploadFile, File
from pdf2image import convert_from_bytes
import pytesseract
import re

app = FastAPI(
    title="Servicio de OCR - Tecniseguros",
    description="Microservicio de extracción de texto para PDFs"
)

def estructurar_datos_reclamo(texto_crudo):
    datos = {
        "clave_busqueda": {
            "placa": None,
            "taller_nombre": None
        },
        "reclamo": {
            "fecha_ocurrencia": None,
            "hora_ocurrencia": None,
            "nombre_conductor": None,
            "descripcion_danos": None
        },
        "tercero": {
            "involucrado": False,
            "propietario": None,
            "descripcion": None
        }
    }
    
    # --- CLAVES DE BÚSQUEDA ---
    placa_match = re.search(r'\b[P|C|M|N|A|V]\d{3,4}[A-Z]{0,2}\b', texto_crudo)
    if placa_match:
        datos["clave_busqueda"]["placa"] = placa_match.group(0)
        
    taller_match = re.search(r'Taller de Reparación\s*([^\n\.]+)', texto_crudo)
    if taller_match:
        datos["clave_busqueda"]["taller_nombre"] = taller_match.group(1).strip()

    # --- DATOS DEL RECLAMO PRINCIPAL ---
    fecha_match = re.search(r'Fecha de Ocurrencia:\s*([^\n]+)', texto_crudo)
    if fecha_match:
        datos["reclamo"]["fecha_ocurrencia"] = fecha_match.group(1).strip()
        
    hora_match = re.search(r'Hora del Evento:\s*(\d{2}:\d{2})', texto_crudo)
    if hora_match:
        datos["reclamo"]["hora_ocurrencia"] = hora_match.group(1)
        
    # FIX: Buscamos el nombre del conductor ubicando el formato del DUI salvadoreño abajo de él.
    conductor_match = re.search(r'\n([A-Za-zÁÉÍÓÚáéíóúÑñ\s]+)\n\d{8}-\d', texto_crudo)
    if conductor_match:
        datos["reclamo"]["nombre_conductor"] = conductor_match.group(1).strip()

    # FIX: Capturamos todo el bloque de texto de los daños y limpiamos la etiqueta suelta
    danos_asegurado_match = re.search(r'Tipo de Daño Descripción de Daños\s*(.*?)\s*Daños Preexistentes', texto_crudo, re.DOTALL)
    if danos_asegurado_match:
        texto_limpio = danos_asegurado_match.group(1).replace('Vehículo Asegurado', '').replace('\n', ' ')
        datos["reclamo"]["descripcion_danos"] = re.sub(r'\s+', ' ', texto_limpio).strip()

    # --- DATOS DEL TERCERO ---
    tercero_match = re.search(r'Conductor\s*/\s*Propietario\s+([^\n]+)', texto_crudo, re.IGNORECASE)
    if tercero_match:
        valor_tercero = tercero_match.group(1).strip()
        if valor_tercero.upper() not in ["N/A", "NINGUNO", "NO APLICA", "NO HAY", "-", ""]:
            datos["tercero"]["involucrado"] = True
            datos["tercero"]["propietario"] = valor_tercero
            
            # FIX: Capturamos el bloque entre la placa del tercero y la pregunta del seguro
            danos_tercero_match = re.search(r'Placa / Marca / Modelo[^\n]+\n+(.*?)\n+¿Posee Seguro\?', texto_crudo, re.DOTALL)
            if danos_tercero_match:
                 texto_limpio = danos_tercero_match.group(1).replace('Daños Materiales', '').replace('\n', ' ')
                 datos["tercero"]["descripcion"] = re.sub(r'\s+', ' ', texto_limpio).strip()
                 
    return datos

@app.post("/api/extract")
async def extract_text(file: UploadFile = File(...)):
    try:
        # Leer los bytes del archivo PDF subido
        pdf_bytes = await file.read()
        
        # Convertir el PDF a una lista de imágenes (una por página)
        images = convert_from_bytes(pdf_bytes)
        
        extracted_text = ""
        
        # ¡ESTE ES EL BLOQUE QUE FALTABA!
        # Procesar cada página de forma secuencial con OCR
        for i, image in enumerate(images):
            text = pytesseract.image_to_string(image, lang='spa')
            extracted_text += f"--- Inicio de Página {i+1} ---\n{text}\n"
        
        # Ahora sí, estructuramos el texto que acabamos de extraer
        datos_estructurados = estructurar_datos_reclamo(extracted_text)
            
        return {
            "status": "success",
            "filename": file.filename,
            "datos_extraidos": datos_estructurados,
            "raw_text": extracted_text 
        }
        
    except Exception as e:
        return {
            "status": "error", 
            "message": f"Hubo un error procesando el documento: {str(e)}"
        }
