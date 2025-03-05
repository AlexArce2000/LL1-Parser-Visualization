<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Entrada de Gramática LL(1)</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            padding: 0;
            background-color: #f4f4f9;
            color: #333;
        }
        h1 {
            color: #444;
        }
        form {
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            max-width: 600px;
            margin: 0 auto;
        }
        label {
            font-weight: bold;
            display: block;
            margin-bottom: 8px;
        }
        textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-family: monospace;
            font-size: 14px;
            resize: vertical;
        }
        input[type="submit"] {
            background-color: #28a745;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        input[type="submit"]:hover {
            background-color: #218838;
        }
        .example {
            margin-top: 20px;
            background: #e9ecef;
            padding: 15px;
            border-radius: 4px;
        }
        .example h3 {
            margin-top: 0;
        }
        .example pre {
            margin: 0;
        }
    </style>
</head>
<body>
<h1>Ingrese la Gramática LL(1)</h1>
<form action="parseGrammar" method="post">
    <label for="productions">Producciones (una por línea):</label><br>
    <textarea id="productions" name="productions" rows="10" cols="50" required></textarea><br><br>

    <input type="submit" value="Analizar">
</form>

<div class="example">
    <h3>Ejemplo de gramática LL(1):</h3>
    <pre>
E  -> T E'
E' -> + T E' | ?
T  -> F T'
T' -> * F T' | ?
F  -> ( E ) | id
        </pre>
    <p><strong>Nota:</strong> Usa "?" para representar la producción vacía.</p>
</div>
</body>
</html>