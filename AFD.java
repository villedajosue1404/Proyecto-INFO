import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Implementacion de un automata finito determinista AFD
 * Se construye a partir de un AFN usando el metodo de subconjuntos
 * Permite validar cadenas determinar si es minimo y minimizarlo
 * Soporta formato de archivo para guardar y cargar automatas
 */
class AFD {
    Estado estadoInicial;
    ArrayList<Estado> estadosFinales;
    Set<Character> alfabeto;
    Map<Estado, Map<Character, Estado>> transiciones;

    /**
     * Constructor que crea un AFD con los parametros basicos
     * Inicializa el mapa de transiciones como un TreeMap vacio
     * @param inicial el estado inicial del automata
     * @param finales la lista de estados finales de aceptacion
     * @param alfabeto el conjunto de simbolos que reconoce el automata
     */
    public AFD(Estado inicial, ArrayList<Estado> finales, Set<Character> alfabeto) {
        this.estadoInicial = inicial;
        this.estadosFinales = finales;
        this.alfabeto = alfabeto;
        this.transiciones = new TreeMap<>();
    }

    /**
     * Convierte un AFN a AFD usando el metodo de construccion de subconjuntos
     * Cada estado del AFD representa un conjunto de estados del AFN alcanzables
     * El algoritmo calcula la clausura epsilon del estado inicial del AFN
     * Para cada estado del AFD y cada simbolo calcula el cambio y clausura
     * Si el conjunto resultante es nuevo crea un nuevo estado del AFD
     * Repite hasta procesar todos los estados del AFD descubiertos
     * @param afn el automata finito no determinista a convertir
     * @return el AFD equivalente al AFN de entrada
     */
    public static AFD desdeAFN(AFN afn) {
        Set<Character> alfabeto = afn.getAlfabeto();

        // La clausura epsilon del estado inicial del AFN es la raiz del AFD
        Set<Estado> clausuraInicial = afn.clausuraLambda(afn.getEstadoInicial());

        Map<String, Estado> mapaClaves = new HashMap<>();
        Map<Estado, Set<Estado>> mapaRepresentacion = new HashMap<>();

        ArrayList<Estado> finales = new ArrayList<>();
        int contadorEstadosAFD = 0;

        // El primer estado del AFD es la clausura epsilon del inicial del AFN
        Estado estadoInicialAFD = new Estado(contadorEstadosAFD);
        contadorEstadosAFD++;

        String claveInicial = generarClaveUnica(clausuraInicial);
        mapaClaves.put(claveInicial, estadoInicialAFD);
        mapaRepresentacion.put(estadoInicialAFD, new HashSet<>(clausuraInicial));

        if (clausuraInicial.contains(afn.getEstadoFinal())) {
            finales.add(estadoInicialAFD);
        }

        AFD afd = new AFD(estadoInicialAFD, finales, alfabeto);

        // Usamos una cola como BFS para procesar los estados del AFD
        LinkedList<Estado> colaEstados = new LinkedList<>();
        colaEstados.add(estadoInicialAFD);

        while (colaEstados.size() > 0) {
            Estado estadoActualAFD = colaEstados.removeFirst();
            Set<Estado> estadosAFNActual = mapaRepresentacion.get(estadoActualAFD);

            for (char simbolo : alfabeto) {

                // PASO 1 Calculamos los estados alcanzables desde el conjunto actual con el simbolo
                Set<Estado> estadosCambio = afn.cambio(estadosAFNActual, simbolo);

                if (estadosCambio.size() == 0) {
                    // No hay transicion con este simbolo desde el conjunto actual
                } else {

                    // PASO 2 Calculamos la clausura epsilon del conjunto de estados alcanzados
                    Set<Estado> estadosClausura = afn.clausuraLambda(estadosCambio);

                    if (estadosClausura.size() > 0) {
                        String claveConjunto = generarClaveUnica(estadosClausura);
                        Estado destinoAFD = mapaClaves.get(claveConjunto);

                        if (destinoAFD == null) {
                            // Este subconjunto no se ha visto antes entonces creamos un nuevo estado del AFD
                            destinoAFD = new Estado(contadorEstadosAFD);
                            contadorEstadosAFD++;
                            mapaClaves.put(claveConjunto, destinoAFD);
                            mapaRepresentacion.put(destinoAFD, new HashSet<>(estadosClausura));
                            colaEstados.add(destinoAFD);

                            if (estadosClausura.contains(afn.getEstadoFinal())) {
                                finales.add(destinoAFD);
                            }
                        }

                        if (afd.transiciones.get(estadoActualAFD) == null) {
                            afd.transiciones.put(estadoActualAFD, new TreeMap<>());
                        }
                        afd.transiciones.get(estadoActualAFD).put(simbolo, destinoAFD);
                    }
                }
            }
        }

        return afd;
    }

    /**
     * Genera una clave unica en texto para un conjunto de estados del AFN
     * Esta clave permite identificar si un subconjunto ya fue procesado
     * Ordena los IDs de los estados y los concatena con comas
     * @param estados el conjunto de estados del AFN
     * @return una cadena unica que representa el conjunto
     */
    private static String generarClaveUnica(Set<Estado> estados) {
        // Extraemos los IDs de todos los estados del conjunto
        ArrayList<Integer> listaIDs = new ArrayList<>();
        for (Estado estado : estados) {
            listaIDs.add(estado.getId());
        }

        // Ordenamos los IDs de menor a mayor usando ordenamiento burbuja
        for (int i = 0; i < listaIDs.size(); i++) {
            for (int j = i + 1; j < listaIDs.size(); j++) {
                if (listaIDs.get(i) > listaIDs.get(j)) {
                    int temporal = listaIDs.get(i);
                    listaIDs.set(i, listaIDs.get(j));
                    listaIDs.set(j, temporal);
                }
            }
        }

        // Construimos la clave en formato q0 q1 q2 etc
        String clave = "";
        for (int i = 0; i < listaIDs.size(); i++) {
            if (i > 0) {
                clave = clave + ",";
            }
            clave = clave + "q" + listaIDs.get(i);
        }

        return clave;
    }

    /**
     * Verifica si una cadena es aceptada por este automata AFD
     * Recorre la cadena caracter por caracter siguiendo las transiciones
     * Devuelve verdadero si al terminar la cadena se encuentra en un estado final
     * Si algun simbolo no pertenece al alfabeto devuelve falso
     * @param cadena la cadena de caracteres a evaluar
     * @return verdadero si la cadena es aceptada falso en caso contrario
     */
    public boolean validar(String cadena) {
        Estado estadoActual = estadoInicial;

        for (int i = 0; i < cadena.length(); i++) {
            char simbolo = cadena.charAt(i);

            // Verificamos si el simbolo actual pertenece al alfabeto del automata
            boolean estaEnAlfabeto = false;
            for (char c : alfabeto) {
                if (c == simbolo) {
                    estaEnAlfabeto = true;
                }
            }
            if (estaEnAlfabeto == false) {
                return false;
            }

            // Verificamos si existe una transicion desde el estado actual con el simbolo
            Map<Character, Estado> transicionesEstado = transiciones.get(estadoActual);
            if (transicionesEstado == null) {
                return false;
            }

            Estado siguienteEstado = transicionesEstado.get(simbolo);
            if (siguienteEstado == null) {
                return false;
            }

            // Avanzamos al siguiente estado segun la transicion
            estadoActual = siguienteEstado;
        }

        // Verificamos si el estado donde terminamos la cadena es un estado final
        for (int i = 0; i < estadosFinales.size(); i++) {
            Estado finalEstado = estadosFinales.get(i);
            if (finalEstado == estadoActual) {
                return true;
            }
        }

        return false;
    }

    /**
     * Obtiene todos los estados que forman parte del automata
     * Incluye los estados que aparecen en las transiciones
     * Incluye el estado inicial y los estados finales
     * @return un conjunto con todos los estados del automata
     */
    private Set<Estado> obtenerTodosLosEstados() {
        Set<Estado> todos = new TreeSet<>(transiciones.keySet());
        todos.add(estadoInicial);
        todos.addAll(estadosFinales);
        return todos;
    }

    /**
     * Determina si este automata AFD ya esta minimizado
     * Ejecuta el algoritmo de particion por grupos
     * Separa inicialmente los estados finales de los no finales
     * Divide los grupos repetidamente segun las firmas de transiciones
     * Si todos los grupos tienen exactamente un estado el AFD es minimo
     * @return verdadero si el AFD es minimo falso si se puede minimizar mas
     */
    public boolean esMinimo() {
        // Separamos los estados en dos grupos finales y no finales
        Grupo grupoFinal = new Grupo();
        Grupo grupoNoFinal = new Grupo();

        for (Estado estado : obtenerTodosLosEstados()) {
            boolean esFinal = false;
            for (int i = 0; i < estadosFinales.size(); i++) {
                if (estadosFinales.get(i) == estado) {
                    esFinal = true;
                }
            }
            if (esFinal == true) {
                grupoFinal.estados.add(estado);
            } else {
                grupoNoFinal.estados.add(estado);
            }
        }

        ArrayList grupos = new ArrayList();
        if (grupoFinal.estados.size() > 0) {
            grupos.add(grupoFinal);
        }
        if (grupoNoFinal.estados.size() > 0) {
            grupos.add(grupoNoFinal);
        }

        // Refinamos los grupos dividiendolos si sus estados tienen firmas diferentes
        boolean huboCambio = true;
        while (huboCambio == true) {
            huboCambio = false;
            ArrayList nuevosGrupos = new ArrayList();

            for (int i = 0; i < grupos.size(); i++) {
                Grupo grupoActual = (Grupo) grupos.get(i);
                TreeMap firmas = new TreeMap();

                for (int j = 0; j < grupoActual.estados.size(); j++) {
                    Estado estadoActual = (Estado) grupoActual.estados.get(j);

                    // La firma es el grupo destino para cada simbolo del alfabeto
                    String firma = "";

                    for (char simbolo : alfabeto) {
                        Map<Character, Estado> transEstado = transiciones.get(estadoActual);
                        Estado destino = null;
                        if (transEstado != null) {
                            destino = transEstado.get(simbolo);
                        }

                        int indiceGrupo = -1;
                        for (int k = 0; k < grupos.size(); k++) {
                            Grupo grupoIteracion = (Grupo) grupos.get(k);
                            if (grupoIteracion.estados.contains(destino)) {
                                indiceGrupo = k;
                                break;
                            }
                        }

                        firma = firma + indiceGrupo + ",";
                    }

                    if (firmas.containsKey(firma) == false) {
                        firmas.put(firma, new Grupo());
                    }

                    Grupo grupoFirma = (Grupo) firmas.get(firma);
                    grupoFirma.estados.add(estadoActual);
                }

                nuevosGrupos.addAll(firmas.values());
                if (firmas.size() > 1) {
                    huboCambio = true;
                }
            }

            grupos = nuevosGrupos;
        }

        // Si algun grupo tiene mas de un estado entonces el AFD no es minimo
        for (int i = 0; i < grupos.size(); i++) {
            Grupo grupo = (Grupo) grupos.get(i);
            if (grupo.estados.size() > 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Construye un AFD minimo equivalente a este automata
     * Usa el algoritmo de particion en grupos o tabla de Myhill Nerode
     * Separa los estados en grupos finales y no finales inicialmente
     * Divide los grupos repetidamente hasta que no se pueda dividir mas
     * Crea un nuevo AFD usando un estado representante por cada grupo
     * @return un nuevo AFD minimizado equivalente al original
     */
    public AFD minimizar() {
        // PASO 1 Separamos los estados en grupos de finales y no finales
        Grupo grupoFinal = new Grupo();
        Grupo grupoNoFinal = new Grupo();

        for (Estado estado : obtenerTodosLosEstados()) {
            boolean esFinal = false;
            for (Estado f : estadosFinales) {
                if (f == estado) { esFinal = true; break; }
            }
            if (esFinal == true) {
                grupoFinal.estados.add(estado);
            } else {
                grupoNoFinal.estados.add(estado);
            }
        }

        ArrayList grupos = new ArrayList();
        if (grupoFinal.estados.size() > 0) {
            grupos.add(grupoFinal);
        }
        if (grupoNoFinal.estados.size() > 0) {
            grupos.add(grupoNoFinal);
        }

        // PASO 2 Refinamos los grupos hasta que no haya cambios
        boolean huboCambio = true;
        while (huboCambio == true) {
            huboCambio = false;
            ArrayList nuevosGrupos = new ArrayList();

            for (int i = 0; i < grupos.size(); i++) {
                Grupo grupoActual = (Grupo) grupos.get(i);
                TreeMap firmas = new TreeMap();

                for (int j = 0; j < grupoActual.estados.size(); j++) {
                    Estado estadoActual = (Estado) grupoActual.estados.get(j);

                    // La firma indica el grupo destino para cada simbolo del alfabeto
                    String firma = "";

                    for (char simbolo : alfabeto) {
                        Map<Character, Estado> transEstado = transiciones.get(estadoActual);
                        Estado destino = null;
                        if (transEstado != null) {
                            destino = transEstado.get(simbolo);
                        }

                        int indiceGrupo = -1;
                        for (int k = 0; k < grupos.size(); k++) {
                            Grupo grupoIteracion = (Grupo) grupos.get(k);
                            if (grupoIteracion.estados.contains(destino)) {
                                indiceGrupo = k;
                                break;
                            }
                        }

                        firma = firma + indiceGrupo + ",";
                    }

                    if (firmas.containsKey(firma) == false) {
                        firmas.put(firma, new Grupo());
                    }

                    Grupo grupoFirma = (Grupo) firmas.get(firma);
                    grupoFirma.estados.add(estadoActual);
                }

                nuevosGrupos.addAll(firmas.values());
                if (firmas.size() > 1) {
                    huboCambio = true;
                }
            }

            grupos = nuevosGrupos;
        }

        // PASO 3 Construimos el AFD minimo con un representante por grupo
        Map<Estado, Estado> mapaRepresentantes = new HashMap<>();

        for (int i = 0; i < grupos.size(); i++) {
            Grupo grupo = (Grupo) grupos.get(i);
            Estado representante = null;
            for (int j = 0; j < grupo.estados.size(); j++) {
                Estado estado = (Estado) grupo.estados.get(j);
                if (representante == null) {
                    representante = estado;
                }
                mapaRepresentantes.put(estado, representante);
            }
        }

        Estado nuevoInicial = mapaRepresentantes.get(estadoInicial);
        ArrayList<Estado> nuevosFinales = new ArrayList<>();
        AFD afdMinimo = new AFD(nuevoInicial, nuevosFinales, alfabeto);

        Set<Estado> visitados = new HashSet<>();

        for (int i = 0; i < grupos.size(); i++) {
            Grupo grupo = (Grupo) grupos.get(i);
            Estado primerEstado = (Estado) grupo.estados.get(0);
            Estado representanteGrupo = mapaRepresentantes.get(primerEstado);

            for (int j = 0; j < estadosFinales.size(); j++) {
                if (grupo.estados.contains(estadosFinales.get(j))) {
                    nuevosFinales.add(representanteGrupo);
                    break;
                }
            }

            if (visitados.contains(representanteGrupo) == false) {
                visitados.add(representanteGrupo);

                Map<Character, Estado> transicionesOriginales = transiciones.get(primerEstado);
                if (transicionesOriginales != null) {
                    for (char simbolo : transicionesOriginales.keySet()) {
                        Estado destinoOriginal = transicionesOriginales.get(simbolo);
                        Estado destinoRepresentante = mapaRepresentantes.get(destinoOriginal);

                        if (afdMinimo.transiciones.get(representanteGrupo) == null) {
                            afdMinimo.transiciones.put(representanteGrupo, new TreeMap<>());
                        }
                        afdMinimo.transiciones.get(representanteGrupo).put(simbolo, destinoRepresentante);
                    }
                }
            }
        }

        return afdMinimo;
    }

    /**
     * Genera la representacion en formato de archivo .afd
     * Primera linea muestra el alfabeto como S= {a,b}
     * Segunda linea muestra la cantidad de estados
     * Tercera linea muestra los IDs de estados finales
     * Lineas siguientes muestran las transiciones por simbolo
     * Las transiciones faltantes se muestran como origen---
     * @return el string con el formato de archivo completo
     */
    public String toFormatoArchivo() {
        String resultado = "";

        // Linea 1 del formato donde se muestra el alfabeto
        resultado = resultado + "S= {";
        boolean primerSimbolo = true;
        for (char simbolo : alfabeto) {
            if (primerSimbolo == false) {
                resultado = resultado + ",";
            }
            resultado = resultado + simbolo;
            primerSimbolo = false;
        }
        resultado = resultado + "}\n";

        // Linea 2 del formato con la cantidad total de estados
        resultado = resultado + transiciones.size() + "\n";

        // Linea 3 con los IDs de los estados finales separados por comas
        for (int i = 0; i < estadosFinales.size(); i++) {
            if (i > 0) {
                resultado = resultado + ",";
            }
            resultado = resultado + estadosFinales.get(i).getId();
        }
        resultado = resultado + "\n";

        // Por cada simbolo del alfabeto generamos una linea de transiciones
        for (char simbolo : alfabeto) {
            boolean primerEstado = true;
            for (Estado estado : transiciones.keySet()) {
                if (primerEstado == false) {
                    resultado = resultado + ",";
                }

                Map<Character, Estado> transEstado = transiciones.get(estado);
                Estado destino = transEstado.get(simbolo);

                if (destino != null) {
                    resultado = resultado + estado.getId() + "-" + destino.getId();
                } else {
                    resultado = resultado + estado.getId() + "---";
                }

                primerEstado = false;
            }
            resultado = resultado + "\n";
        }

        return resultado;
    }

    /**
     * Lee y reconstruye un AFD desde una cadena en formato .afd
     * Extrae el alfabeto de la primera linea con formato S= {a,b}
     * Lee la cantidad de estados y los IDs de estados finales
     * Reconstruye las transiciones desde las lineas de pares origen-destino
     * @param contenido el string completo con el formato .afd
     * @return el AFD reconstruido desde el formato de archivo
     */
    public static AFD desdeFormatoArchivo(String contenido) {
        String[] lineas = contenido.split("\n");

        // Parseamos la linea 1 que contiene el alfabeto en formato S= {a,b}
        String primeraLinea = lineas[0].trim();
        int posicionInicio = primeraLinea.indexOf('{');
        int posicionFin = primeraLinea.indexOf('}');
        String contenidoAlfabeto = primeraLinea.substring(posicionInicio + 1, posicionFin);

        Set<Character> alfabeto = new HashSet<>();
        for (int i = 0; i < contenidoAlfabeto.length(); i++) {
            char caracter = contenidoAlfabeto.charAt(i);
            if (caracter != ',' && caracter != ' ') {
                alfabeto.add(caracter);
            }
        }

        // Parseamos la linea 2 que indica la cantidad de estados
        String segundaLinea = lineas[1].trim();
        int numeroEstados = Integer.parseInt(segundaLinea);

        // Parseamos la linea 3 con los IDs de los estados finales separados por comas
        String terceraLinea = lineas[2].trim();
        String[] idsFinales = terceraLinea.split(",");
        ArrayList<Integer> listaIDsFinales = new ArrayList<>();
        for (int i = 0; i < idsFinales.length; i++) {
            String id = idsFinales[i].trim();
            if (id.isEmpty() == false) {
                listaIDsFinales.add(Integer.parseInt(id));
            }
        }

        // Creamos los estados del AFD usando un mapa de ID a Estado
        Map<Integer, Estado> mapaIDs = new HashMap<>();
        for (int i = 0; i < numeroEstados; i++) {
            mapaIDs.put(i, new Estado(i));
        }

        // Construimos la lista de estados finales a partir de los IDs leidos
        ArrayList<Estado> estadosFinales = new ArrayList<>();
        for (int i = 0; i < listaIDsFinales.size(); i++) {
            int idFinal = listaIDsFinales.get(i);
            estadosFinales.add(mapaIDs.get(idFinal));
        }

        // Finalmente creamos el AFD con los datos extraidos
        AFD afd = new AFD(mapaIDs.get(0), estadosFinales, alfabeto);

        // Procesamos las lineas de transiciones desde la linea 4 en adelante
        int indiceSimbolo = 0;
        Character[] arregloAlfabeto = new Character[alfabeto.size()];
        int temp = 0;
        for (char c : alfabeto) {
            arregloAlfabeto[temp] = c;
            temp = temp + 1;
        }

        for (int i = 3; i < lineas.length; i++) {
            String linea = lineas[i].trim();
            if (linea.isEmpty()) {
                // Si la linea esta vacia simplemente la saltamos
            } else {
                char simboloActual = arregloAlfabeto[indiceSimbolo];
                indiceSimbolo = indiceSimbolo + 1;

                String[] pares = linea.split(",");
                for (int j = 0; j < pares.length; j++) {
                    String par = pares[j].trim();
                    if (par.isEmpty()) {
                        // Si el par esta vacio lo saltamos
                    } else {
                        String[] partes = par.split("-");
                        int desde = Integer.parseInt(partes[0].trim());
                        int hacia = Integer.parseInt(partes[1].trim());

                        Estado estadoDesde = mapaIDs.get(desde);
                        Estado estadoHacia = mapaIDs.get(hacia);

                        if (afd.transiciones.get(estadoDesde) == null) {
                            afd.transiciones.put(estadoDesde, new TreeMap<>());
                        }
                        afd.transiciones.get(estadoDesde).put(simboloActual, estadoHacia);
                    }
                }
            }
        }

        return afd;
    }

    /**
     * Imprime el AFD en la consola para depuracion
     * Muestra el estado inicial los estados finales el alfabeto
     * Muestra todas las transiciones en formato origen --simbolo--> destino
     */
    public void imprimir() {
        System.out.println("--- AFD ---");
        System.out.println("Estado inicial: " + estadoInicial);
        System.out.print("Estados finales: ");
        for (int i = 0; i < estadosFinales.size(); i++) {
            System.out.print(estadosFinales.get(i) + " ");
        }
        System.out.println();
        System.out.println("Alfabeto: " + alfabeto);
        System.out.println("Transiciones:");
        for (Estado estado : transiciones.keySet()) {
            Map<Character, Estado> transEstado = transiciones.get(estado);
            for (char simbolo : transEstado.keySet()) {
                Estado destino = transEstado.get(simbolo);
                System.out.println("  " + estado + " --" + simbolo + "--> " + destino);
            }
        }
    }

    /**
     * Valida una cadena y devuelve el resultado con la traza de estados
     * La traza muestra el camino seguido como q0->q1->q2
     * Util para depuracion y para mostrar el recorrido del automata
     * @param cadena la cadena a validar
     * @return un objeto ResultadoValidacion con la traza y si fue aceptada
     */
    public ResultadoValidacion validarConTraza(String cadena) {
        Estado estadoActual = estadoInicial;
        StringBuilder traza = new StringBuilder();
        traza.append("q").append(estadoActual.getId());

        for (int i = 0; i < cadena.length(); i++) {
            char simbolo = cadena.charAt(i);

            boolean simboloValido = false;
            for (char c : alfabeto) {
                if (c == simbolo) { simboloValido = true; break; }
            }
            if (!simboloValido) {
                return new ResultadoValidacion(traza.toString(), false);
            }

            Map<Character, Estado> transEstado = transiciones.get(estadoActual);
            if (transEstado == null) {
                return new ResultadoValidacion(traza.toString(), false);
            }

            Estado siguiente = transEstado.get(simbolo);
            if (siguiente == null) {
                return new ResultadoValidacion(traza.toString(), false);
            }

            estadoActual = siguiente;
            traza.append("->q").append(estadoActual.getId());
        }

        boolean esFinal = false;
        for (Estado f : estadosFinales) {
            if (f == estadoActual) { esFinal = true; break; }
        }

        return new ResultadoValidacion(traza.toString(), esFinal);
    }

    /**
     * Genera la tabla de transiciones formateada para mostrar
     * Si algun estado no tiene todas las transiciones definidas
     * agrega un estado de error especial con ID Integer.MAX_VALUE
     * Los estados se renumeran secuencialmente y el error siempre es el ultimo
     * Las transiciones faltantes se muestran como E
     * @return el string con la tabla de transiciones formateada
     */
    public String formatearTransiciones() {
        Set<Estado> todosEstados = obtenerTodosLosEstados();
        for (Estado est : transiciones.keySet()) {
            Map<Character, Estado> transE = transiciones.get(est);
            if (transE != null) {
                for (Estado dest : transE.values()) {
                    todosEstados.add(dest);
                }
            }
        }

        if (todosEstados.isEmpty()) return "";

        java.util.List<Estado> listaEstados = new java.util.ArrayList<>(todosEstados);
        java.util.Collections.sort(listaEstados);

        boolean necesitaError = false;
        for (Estado est : listaEstados) {
            for (char simbolo : alfabeto) {
                Map<Character, Estado> transE = transiciones.get(est);
                if (transE == null || !transE.containsKey(simbolo)) {
                    necesitaError = true;
                    break;
                }
            }
            if (necesitaError) break;
        }

        if (necesitaError) {
            listaEstados.add(new Estado(Integer.MAX_VALUE));
        }

        java.util.Map<Integer, Integer> mapaRemapeo = new java.util.TreeMap<>();
        int idx = 0;
        for (Estado e : listaEstados) {
            mapaRemapeo.put(e.getId(), idx);
            idx++;
        }

        StringBuilder sb = new StringBuilder();

        boolean primero = true;
        for (char c : alfabeto) {
            if (!primero) sb.append(",");
            sb.append(c);
            primero = false;
        }
        sb.append("\n");

        sb.append(listaEstados.size()).append("\n");

        boolean primeroF = true;
        for (Estado f : estadosFinales) {
            if (!primeroF) sb.append(",");
            Integer remapeado = mapaRemapeo.get(f.getId());
            if (remapeado != null) {
                sb.append(remapeado);
            }
            primeroF = false;
        }
        sb.append("\n");

        for (char simbolo : alfabeto) {
            boolean primeroE = true;
            for (Estado est : listaEstados) {
                if (!primeroE) sb.append(",");
                Map<Character, Estado> transE = transiciones.get(est);
                if (transE != null && transE.containsKey(simbolo)) {
                    sb.append(mapaRemapeo.get(transE.get(simbolo).getId()));
                } else {
                    sb.append("E");
                }
                primeroE = false;
            }
            sb.append(".\n");
        }

        return sb.toString();
    }
}

/**
 * Almacena el resultado de la validacion de una cadena
 * Contiene la traza con el camino de estados recorrido
 * Indica si la cadena fue aceptada por el automata
 */
class ResultadoValidacion {
    public String traza;
    public boolean aceptada;

    public ResultadoValidacion(String traza, boolean aceptada) {
        this.traza = traza;
        this.aceptada = aceptada;
    }
}

/**
 * Clase auxiliar para agrupar estados durante el algoritmo de minimizacion
 * Almacena una lista de estados que pertenecen al mismo grupo
 * Se utiliza en el metodo de particion para encontrar estados equivalentes
 */
class Grupo {
    ArrayList estados = new ArrayList();
}
