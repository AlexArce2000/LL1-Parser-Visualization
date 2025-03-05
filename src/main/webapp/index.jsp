<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Entrada de Gramática LL(1)</title>
</head>
<body>
<h1>Ingrese la Gramática LL(1)</h1>
<form action="/parseGrammar" method="post">
    <label for="productions">Producciones (una por línea):</label><br>
    <textarea id="productions" name="productions" rows="10" cols="50" required></textarea><br><br>

    <input type="submit" value="Analizar">
</form>
</body>
</html>
