# Documentacion del Proyecto de Automatas

## Descripcion General

Este proyecto implementa un sistema completo para trabajar con
expresiones regulares y automatas finitos
El flujo principal convierte una expresion regular en un
Automata Finito No Determinista AFN usando el algoritmo de Thompson
luego convierte ese AFN en un Automata Finito Determinista AFD
usando el metodo de construccion de subconjuntos
y finalmente puede minimizar el AFD usando el algoritmo de particion
Tambien permite validar cadenas contra el automata generado
ejecutar pruebas unitarias y generar calificaciones

---

## Archivos del Proyecto

### Estado.java
Representa un estado individual dentro de un automata
Cada estado tiene un identificador numerico unico
Implementa la interfaz Comparable para poder ordenarse
dentro de estructuras como TreeMap y TreeSet
Su representacion en texto es q seguido del numero de id
ejemplo q0 q1 q2 etc
Se usa tanto en AFN como en AFD

### AlgoritmoThompson.java
Implementa la construccion de Thompson para expresiones regulares
Contiene un contador estatico de estados que asigna ids unicos
Proporciona los siguientes metodos estaticos

crearSimbolo crea un AFN basico de dos estados
con una transicion directa usando el simbolo dado

concatenar conecta dos AFN en serie
une el estado final del primero con el inicial del segundo
mediante una transicion epsilon

unirO conecta dos AFN en paralelo con el operador OR
crea un nuevo inicio con epsilon a ambos automatas
y un nuevo final que recibe epsilon desde ambos

aplicarEstrella aplica la estrella de Kleene al AFN
agrega epsilon que permite cero o mas repeticiones
tiene un camino directo del nuevo inicio al nuevo fin
y un camino del final original al inicio original

aplicarMas aplica el operador mas al AFN
es como la estrella pero sin el camino directo
por lo que requiere al menos una repeticion

clonarTransiciones copia todas las transiciones de un AFN a otro
es un metodo privado auxiliar para las operaciones anteriores

### ParserRegex.java
Analizador sintactico de expresiones regulares
Usa el metodo de analisis descendente recursivo
El proceso de parsing funciona asi

primero elimina los espacios en blanco de la expresion
luego procesa la expresion completa con estos niveles

procesarExpresion maneja el operador pipe
separa la expresion en terminos unidos por
lee un termino y mientras encuentre pipes
lee otro termino y los une con AlgoritmoThompson.unirO

procesarTermino maneja la concatenacion
lee un factor y mientras el siguiente caracter
pueda iniciar un factor los va concatenando
detecta letras mayusculas minusculas digitos y parentesis

procesarFactor maneja los elementos basicos
caso 1 si encuentra parentesis procesa la expresion interna
despues verifica si vienen operadores estrella o mas
caso 2 si encuentra una letra o digito crea el AFN del simbolo
y verifica si vienen operadores estrella o mas

Los operadores estrella y mas se aplican inmediatamente
despues del factor al que modifican

### AFN.java
Implementacion del automata finito no determinista
Almacena las transiciones en un TreeMap
que asocia cada estado origen a un mapa
donde cada simbolo tiene una lista de estados destino
Esto permite el no determinismo un estado puede tener
multiples destinos para el mismo simbolo

Metodos principales

agregarEstado agrega un estado al mapa de transiciones
agregarTransicion agrega una transicion entre estados con un simbolo
getAlfabeto obtiene todos los simbolos excepto epsilon
imprimirAutomata muestra el automata en consola
formatearTransiciones genera el formato tabla con NT

Metodos para conversion a AFD

clausuraLambda calcula la clausura epsilon desde uno o varios estados
usa una pila para seguir todas las transiciones epsilon
alcanzables desde el estado o conjunto de estados inicial

cambio obtiene los estados destino desde un conjunto de estados
usando un simbolo especifico sin incluir la clausura epsilon

### AFD.java
Implementacion del automata finito determinista
Contiene el estado inicial la lista de estados finales
el alfabeto y el mapa de transiciones
Las transiciones son deterministicas un estado tiene
exactamente un destino por cada simbolo

Metodos estaticos principales

desdeAFN convierte un AFN a AFD usando subconjuntos
el algoritmo funciona asi
1 calcula la clausura epsilon del estado inicial del AFN
2 ese conjunto es el primer estado del AFD
3 para cada estado del AFD y cada simbolo
  a calcula los estados alcanzables con el simbolo cambio
  b calcula la clausura epsilon de esos estados
  c si el conjunto es nuevo crea un nuevo estado del AFD
  d agrega la transicion al mapa del AFD
4 repite hasta procesar todos los estados descubiertos
5 marca como finales los estados que contengan
  el estado final del AFN original

desdeFormatoArchivo reconstruye un AFD desde un string
con el formato de archivo .afd
primero extrae el alfabeto de la linea S= {a,b}
luego lee la cantidad de estados y los IDs finales
finalmente reconstruye las transiciones desde los pares origen-destino

Metodos de instancia

validar recorre una cadena caracter por caracter
siguiendo las transiciones del automata
devuelve verdadero si al terminar la cadena
se encuentra en un estado final

validarConTraza igual que validar pero ademas devuelve
el camino de estados recorrido como q0->q1->q2

esMinimo verifica si el automata ya esta minimizado
usa el algoritmo de particion en grupos
separa inicialmente estados finales de no finales
luego divide grupos cuyos estados tengan firmas diferentes
la firma es el grupo destino para cada simbolo del alfabeto
si todos los grupos tienen un solo estado el AFD es minimo

minimizar aplica el algoritmo de particion completo
paso 1 separa estados finales y no finales
paso 2 refina los grupos hasta que no haya cambios
paso 3 construye un nuevo AFD con un representante por grupo
cada grupo se convierte en un solo estado del nuevo automata

toFormatoArchivo genera el formato de archivo .afd
con el alfabeto la cantidad de estados los finales
y las transiciones en formato origen-destino

formatearTransiciones genera la tabla de transiciones
para mostrar en pantalla en formato solicitado
si faltan transiciones agrega un estado de error
con ID Integer.MAX_VALUE al final de la lista
los estados se renumeran secuencialmente de 0 a n-1
las transiciones faltantes se muestran como E

### LectorArchivos.java
Utilidad para leer archivos del disco
Tiene un solo metodo estatico leerArchivo
Si el archivo tiene extension .er solo devuelve
la primera linea sin espacios en blanco
Para cualquier otro archivo devuelve todo el contenido
con los saltos de linea originales

### Proyecto.java
Clase principal con el menu interactivo del programa
Muestra el banner de MEL Y CAMI y las opciones

Opcion 1 guarda la ruta de un archivo ER
Opcion 2 guarda la ruta de un archivo AFD
Opcion 3 lee una ER genera el AFN con Thompson
lo convierte a AFD con subconjuntos y muestra todo
Opcion 4 lee un archivo AFD verifica si es minimo
y si no lo minimiza mostrando ambos estados
Opcion 5 lee un AFD y evalua una cadena ingresada
por el usuario mostrando si es aceptada o rechazada
Opcion 6 abre el panel de pruebas unitarias
Opcion 0 sale del programa

### TestProyecto.java
Panel de pruebas unitarias del proyecto
Mantiene contadores de pruebas totales pasadas y fallidas
Ofrece menus para ejecutar pruebas individuales o masivas

Menu principal
Test individual pide el nombre y abre el submenu
Test todos descubre todos los tests y abre el submenu multiple
Borrar resultados elimina las carpetas de resultados

Submenu de pruebas
Hacer todos los test ejecuta AFN AFD y parsing
Solo AFN solo genera y muestra el automata no determinista
Solo Parsing evalua las cadenas del archivo .txt
Solo AFD genera el AFD minimo y lo compara con el esperado
Solo AFD minimo igual que el anterior
Pasar AFD a AFD minimo carga un AFD y lo minimiza

Metodos auxiliares

cargarAFDSinMinimizar intenta generar desde ER
si falla lee desde archivo .afd sin minimizar

cargarGenerarAFD genera desde ER y minimiza
si falla lee el .afd y lo minimiza

descubrirTests busca archivos .er y .afd
en las carpetas de tests y devuelve los nombres

### Banner.java
Genera el banner decorativo del equipo MEL Y CAMI
Usa codigos de escape ANSI para colores RGB
El degradado va de rosa a azul de forma suave
Tiene dos variantes imprimirBanner sin subtitulo
y mostrar con subtitulo centrado

### BannerTest.java
Banner del panel de pruebas unitarias
Usa un degradado ondulante entre rosa y lila
con una funcion seno para el efecto de onda
Muestra arte ASCII con un conejito y el texto TEST

---

## Estructura de Directorios

Proyecto-INFO/
  Estado.java            definicion de estado de automata
  AlgoritmoThompson.java construccion de AFN desde ER
  ParserRegex.java       analizador de expresiones regulares
  AFN.java               automata finito no determinista
  AFD.java               automata finito determinista
  LectorArchivos.java    lectura de archivos
  Proyecto.java          menu principal del programa
  TestProyecto.java      panel de pruebas unitarias
  README.md              formato de salida de transiciones
  DOCUMENTACION.md       esta documentacion
  util/
    Banner.java          banner decorativo principal
    BannerTest.java      banner del panel de pruebas
  tests/
    er/                  archivos de expresiones regulares
    afd/                 archivos de automatas AFD esperados
    expafd/              archivos de AFD minimo esperado
    txt/                 archivos de cadenas para parsing
    exparsin/            archivos de resultados esperados de parsing
  resultados/
    afd/                 resultados generados de AFD
    parsin/              resultados generados de parsing

---

## Flujo del Proceso Paso a Paso

Paso 1 Expresion Regular a AFN
Se lee el archivo .er que contiene la expresion regular
El ParserRegex analiza la expresion con analisis descendente recursivo
Para cada simbolo llamado a AlgoritmoThompson.crearSimbolo
Para la concatenacion llama a AlgoritmoThompson.concatenar
Para el operador llama a AlgoritmoThompson.unirO
Para la estrella llama a AlgoritmoThompson.aplicarEstrella
Para el mas llama a AlgoritmoThompson.aplicarMas
El resultado es un AFN con transiciones epsilon y multiples caminos

Paso 2 AFN a AFD metodo de subconjuntos
AFD.desdeAFN recibe el AFN
Calcula la clausura epsilon del estado inicial
Esa clausura es el primer estado del AFD
Usa una cola BFS para procesar estados del AFD
Para cada estado y cada simbolo del alfabeto
calcula cambio desde el conjunto actual con el simbolo
luego clausura epsilon del resultado
Si el conjunto es nuevo crea un estado del AFD
Agrega la transicion al mapa del AFD
Los estados que contienen el estado final del AFN original
se marcan como estados finales del AFD

Paso 3 Minimizacion del AFD opcional
AFD.minimizar recibe el AFD
Separa estados en grupos finales y no finales
Repite la particion hasta que no haya cambios
Cada grupo tiene estados con la misma firma
La firma es el grupo destino para cada simbolo
Construye un nuevo AFD con un representante por grupo

Paso 4 Validacion de cadenas
AFD.validar recibe la cadena a evaluar
Recorre cada caracter de la cadena
Verifica que el simbolo este en el alfabeto
Sigue la transicion desde el estado actual
Si en algun punto no hay transicion rechaza
Al terminar la cadena verifica si es estado final

Paso 5 Pruebas unitarias
TestProyecto descubre los tests disponibles
Para cada test lee la expresion regular
Genera el AFN y lo muestra
Convierte a AFD y lo minimiza
Compara contra el resultado esperado
Evalu cada cadena del archivo .txt
Compara cada resultado contra el .exparsin
Calcula la calificacion sobre 10 puntos

---

## Formato de Archivos de Test

tests/er/N.er contiene la expresion regular en una sola linea
tests/afd/N.afd contiene el AFD en formato de archivo
tests/expafd/N.expafd contiene el AFD minimo esperado
tests/txt/N.txt contiene las cadenas a evaluar una por linea
tests/exparsin/N.exparsin contiene true o false por linea
  indicando si cada cadena debe ser aceptada

## Formato de salida de transiciones AFD

Primera linea simbolos del alfabeto separados por coma
Segunda linea cantidad total de estados
Tercera linea IDs de estados finales separados por coma
Lineas siguientes una por simbolo del alfabeto
Cada linea tiene el destino para cada estado en orden
separado por coma y termina con punto
Las transiciones faltantes se marcan como E
