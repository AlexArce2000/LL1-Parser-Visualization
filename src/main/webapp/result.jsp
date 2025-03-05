<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Resultado del Análisis LL(1)</title>
</head>
<body>
<h1>Resultado del Análisis LL(1)</h1>

<h2>Conjunto FIRST</h2>
<table border="1">
    <tr><th>No Terminal</th><th>FIRST</th></tr>
    <%
        Map<String, Set<String>> firstSets = (Map<String, Set<String>>) request.getAttribute("firstSets");
        for (Map.Entry<String, Set<String>> entry : firstSets.entrySet()) {
    %>
    <tr>
        <td><%= entry.getKey() %></td>
        <td><%= entry.getValue() %></td>
    </tr>
    <% } %>
</table>

<h2>Conjunto FOLLOW</h2>
<table border="1">
    <tr><th>No Terminal</th><th>FOLLOW</th></tr>
    <%
        Map<String, Set<String>> followSets = (Map<String, Set<String>>) request.getAttribute("followSets");
        for (Map.Entry<String, Set<String>> entry : followSets.entrySet()) {
    %>
    <tr>
        <td><%= entry.getKey() %></td>
        <td><%= entry.getValue() %></td>
    </tr>
    <% } %>
</table>

<h2>Tabla de Análisis M</h2>
<table border="1">
    <tr><th>Estado</th><th>Símbolo</th><th>Producción</th></tr>
    <%
        Map<String, Map<String, String>> parsingTable = (Map<String, Map<String, String>>) request.getAttribute("parsingTable");
        for (Map.Entry<String, Map<String, String>> entry : parsingTable.entrySet()) {
            for (Map.Entry<String, String> innerEntry : entry.getValue().entrySet()) {
    %>
    <tr>
        <td><%= entry.getKey() %></td>
        <td><%= innerEntry.getKey() %></td>
        <td><%= innerEntry.getValue() %></td>
    </tr>
    <% } } %>
</table>

</body>
</html>
