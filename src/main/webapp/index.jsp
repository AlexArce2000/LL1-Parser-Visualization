<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Analizador de Gramáticas LL(1)</title>
    <link rel="icon" href="assets/LL1.jpg" type="image/jpeg">
    <link rel="stylesheet" href="css/styles_index.css">
</head>
<body>

<div class="container">
    <h1>Analizador de Gramáticas LL(1)</h1>
    <p class="subtitle">Introduce tu gramática, una producción por línea, y obtén los conjuntos First, Follow y la tabla de análisis.</p>

    <form action="parseGrammar" method="post">
        <label for="productions">Definición de la Gramática:</label>
        <textarea id="productions" name="productions" rows="10" placeholder="E -> T E'
E' -> + T E' | ε
..." required></textarea>

        <div class="submit-container">
            <input type="submit" value="Analizar Gramática">
        </div>
    </form>

    <div class="example-card">
        <h3>Ejemplo de formato:</h3>
        <pre>
E -> T E'
E' -> + T E' | ε
T -> F T'
T' -> * F T' | ε
F -> ( E ) | id</pre>
        <p><strong>Nota:</strong> Usa "ε" (o déjalo en blanco después de "|") para representar la producción vacía (epsilon). El símbolo "?" también es aceptado.</p>
    </div>
</div>

</body>
</html>