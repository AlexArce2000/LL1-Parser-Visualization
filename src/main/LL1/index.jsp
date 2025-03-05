<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
  <title>Entrada de Gramática LL(1)</title>
</head>
<body>
<h1>Ingrese la Gramática LL(1)</h1>
<form action="parseGrammar" method="post">
  <label for="startSymbol">Símbolo de inicio:</label>
  <input type="text" id="startSymbol" name="startSymbol" required><br><br>

  <label for="productions">Producciones (una por línea):</label><br>
  <textarea id="productions" name="productions" rows="10" cols="50" required></textarea><br><br>

  <label for="inputString">Cadena a analizar:</label>
  <input type="text" id="inputString" name="inputString"><br><br>

  <input type="submit" value="Analizar">
</form>
</body>
</html>
