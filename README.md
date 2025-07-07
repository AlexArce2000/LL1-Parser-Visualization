# LL(1) Parser Visualization

Una aplicación web desarrollada en Java Servlets y JSP que permite analizar gramáticas LL(1) de forma interactiva. El usuario puede introducir una gramática y la aplicación calculará y mostrará automáticamente los conjuntos **First** y **Follow** para cada no-terminal, además de generar la **Tabla de Análisis LL(1)** correspondiente.


---

## ▶️ Características Principales

*   **Cálculo Automático:**
    *   Generación de **Conjuntos First**.
    *   Generación de **Conjuntos Follow**.
    *   Construcción de la **Tabla de Análisis Predictiva LL(1)**.
*   **Manejo Flexible de Gramáticas:**
    *   Soporta múltiples producciones por no-terminal en una sola línea usando `|`.
    *   Acepta varias representaciones de la producción vacía (epsilon), como `ε`, `?`, o simplemente un espacio vacío.
*   **Presentación Clara de Resultados:**
    *   La gramática parseada se muestra para confirmación.
    *   Los conjuntos First y Follow se presentan en una tabla comparativa.
    *   La tabla de análisis muestra claramente las producciones o los errores de conflicto.


---

## 🛠️ Tecnologías Utilizadas

*   **Backend:** Java 11+ / Jakarta Servlets 5.0
*   **Frontend:** JavaServer Pages (JSP), HTML5, CSS3
*   **Servidor de Aplicaciones:** Apache Tomcat 9+
*   **Build Tool:** Apache Maven 3.6+


---

## 📖 Modo de Uso

1.  **Ingresar la Gramática:** En la página principal, introduce las producciones de tu gramática en el área de texto.
    *   Escribe **una producción por no-terminal por línea**.
    *   Usa `->` para separar el no-terminal del cuerpo de la producción.
    *   Usa `|` para separar producciones alternativas para el mismo no-terminal.
    *   Para la producción vacía (epsilon), puedes usar `ε`, `?`, o simplemente dejar un espacio vacío después de un `|` (ej: `A -> a | `).

2.  **Ejemplo de Gramática:**
    Puedes copiar y pegar este ejemplo clásico para probar:
    ```
    E -> T E'
    E' -> + T E' | ε
    T -> F T'
    T' -> * F T' | ε
    F -> ( E ) | id
    ```

3.  **Analizar:** Haz clic en el botón "Analizar Gramática".

4.  **Ver Resultados:** Serás redirigido a la página de resultados, donde verás la gramática interpretada, la tabla de conjuntos First/Follow y la tabla de análisis LL(1).

---


### Lógica del Analizador

El análisis se realiza en `GrammarParserServlet.java` siguiendo estos pasos:

1.  **Parseo de la Gramática:** Las producciones de texto se convierten en una estructura de datos `Map<String, List<String>>`.
2.  **Cálculo de Conjuntos First:** Se implementa un algoritmo iterativo que calcula el conjunto `FIRST` para cada no-terminal, manejando terminales, no-terminales y producciones `ε`.
3.  **Cálculo de Conjuntos Follow:** Tras calcular los `FIRST`, otro algoritmo iterativo calcula el conjunto `FOLLOW` para cada no-terminal, aplicando las reglas estándar (símbolo inicial, A -> αBβ, A -> αB).
4.  **Generación de la Tabla de Análisis:** Se construye la tabla `M[A, a]` siguiendo las dos reglas principales de los analizadores LL(1) basadas en los conjuntos `FIRST` y `FOLLOW`.
5.  **Renderizado de Resultados:** Los datos calculados se envían a `result.jsp`, que se encarga de presentarlos al usuario de forma clara y ordenada.

---
