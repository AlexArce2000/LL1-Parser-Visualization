<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Resultados del Análisis LL(1)</title>
</head>
<body>
<h1>Resultados del Análisis LL(1)</h1>

<h2>Gramática:</h2>
<pre>
<%
    Map<String, List<String>> grammar = (Map<String, List<String>>) request.getAttribute("grammar");
    for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
        out.println(entry.getKey() + " -> " + String.join(" | ", entry.getValue()));
    }
%>
</pre>

<h2>Conjuntos First:</h2>
<pre>
<%
    Map<String, Set<String>> firstSets = (Map<String, Set<String>>) request.getAttribute("firstSets");
    for (Map.Entry<String, Set<String>> entry : firstSets.entrySet()) {
        out.println("First(" + entry.getKey() + ") = " + entry.getValue());
    }
%>
</pre>

<h2>Conjuntos Follow:</h2>
<pre>
<%
    Map<String, Set<String>> followSets = (Map<String, Set<String>>) request.getAttribute("followSets");
    for (Map.Entry<String, Set<String>> entry : followSets.entrySet()) {
        out.println("Follow(" + entry.getKey() + ") = " + entry.getValue());
    }
%>
</pre>

<h2>Tabla de Análisis LL(1):</h2>
<pre>
<%
    Map<String, Map<String, String>> parsingTable = (Map<String, Map<String, String>>) request.getAttribute("parsingTable");
    for (Map.Entry<String, Map<String, String>> entry : parsingTable.entrySet()) {
        out.println("No Terminal: " + entry.getKey());
        for (Map.Entry<String, String> tableEntry : entry.getValue().entrySet()) {
            out.println("  " + tableEntry.getKey() + " -> " + tableEntry.getValue());
        }
    }
%>
</pre>

</body>
</html>