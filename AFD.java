import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Esto es un automata finito determinista (AFD).
 * se puede crear desde un AFN kon el metodo de subkonjuntos,
 * validar kadenas, minimisarlo kon particiones,
 * y guardarlo/leerlo en formato .afd.
 */
class AFD {
    Estado estadoInicial;
    ArrayList<Estado> estadosFinales;
    Set<Character> alfabeto;
    Map<Estado, Map<Character, Estado>> transiciones;

    /**
     * esto kreA un AFD kon el estado inicial, los finales, y el alfabeto.
     */
    public AFD(Estado inicial, ArrayList<Estado> finales, Set<Character> alfabeto) {
        this.estadoInicial = inicial;
        this.estadosFinales = finales;
        this.alfabeto = alfabeto;
        this.transiciones = new TreeMap<>();
    }

    /**
     * esto konvierte un AFN a AFD usando el metodo de subkonjuntos.
     * kada estado del AFD es un monton de estados del AFN ke se pueden alkansar kon λ-serradura.
     * <ul>
     *   <li>kalkula la λ-serradura del estado inicial del AFN para el primer estado del AFD</li>
     *   <li>para kada estado del AFD i kada simbolo, kalkula kambio lueguito λ-serradura</li>
     *   <li>si ese monton de estados no se a visto antes, kreA un estado nuevo del AFD</li>
     *   <li>sigue asiendo esto asta ke no kedan estados del AFD sin prosesar</li>
     * </ul>
     */
    public static AFD desdeAFN(AFN afn) {
        Set<Character> alfabeto = afn.getAlfabeto();

        // la λ-serradura del estado inicial del AFN es la raiz del AFD
        Set<Estado> clausuraInicial = afn.clausuraLambda(afn.getEstadoInicial());

        Map<String, Estado> mapaClaves = new HashMap<>();
        Map<Estado, Set<Estado>> mapaRepresentacion = new HashMap<>();

        ArrayList<Estado> finales = new ArrayList<>();
        int contadorEstadosAFD = 0;

        // el primer estado del AFD es la λ-serradura del inicial del AFN
        Estado estadoInicialAFD = new Estado(contadorEstadosAFD);
        contadorEstadosAFD++;

        String claveInicial = generarClaveUnica(clausuraInicial);
        mapaClaves.put(claveInicial, estadoInicialAFD);
        mapaRepresentacion.put(estadoInicialAFD, new HashSet<>(clausuraInicial));

        if (clausuraInicial.contains(afn.getEstadoFinal())) {
            finales.add(estadoInicialAFD);
        }

        AFD afd = new AFD(estadoInicialAFD, finales, alfabeto);

        // usamos una cola como BFS para ir prosesando los estados del AFD
        LinkedList<Estado> colaEstados = new LinkedList<>();
        colaEstados.add(estadoInicialAFD);

        while (colaEstados.size() > 0) {
            Estado estadoActualAFD = colaEstados.removeFirst();
            Set<Estado> estadosAFNActual = mapaRepresentacion.get(estadoActualAFD);

            for (char simbolo : alfabeto) {

                // PASO 1: kambio — desde los estados aktuales kon el simbolo
                Set<Estado> estadosCambio = afn.cambio(estadosAFNActual, simbolo);

                if (estadosCambio.size() == 0) {
                    // no ai transision
                } else {

                    // PASO 2: λ-serradura del monton de estados alkansado
                    Set<Estado> estadosClausura = afn.clausuraLambda(estadosCambio);

                    if (estadosClausura.size() > 0) {
                        String claveConjunto = generarClaveUnica(estadosClausura);
                        Estado destinoAFD = mapaClaves.get(claveConjunto);

                        if (destinoAFD == null) {
                            // este subkonjunto no se a visto → kreA un estado nuevo del AFD
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
     * ase una klabe unika para un monton de estados del AFN,
     * para identifikar los estados del AFD en el mapa.
     * ordena los IDs i los junta tipo "q0,q1,...".
     */
    private static String generarClaveUnica(Set<Estado> estados) {
        // sakamos los IDs de los estados
        ArrayList<Integer> listaIDs = new ArrayList<>();
        for (Estado estado : estados) {
            listaIDs.add(estado.getId());
        }

        // ordenamos los IDs del mas chiko al mas grande
        for (int i = 0; i < listaIDs.size(); i++) {
            for (int j = i + 1; j < listaIDs.size(); j++) {
                if (listaIDs.get(i) > listaIDs.get(j)) {
                    int temporal = listaIDs.get(i);
                    listaIDs.set(i, listaIDs.get(j));
                    listaIDs.set(j, temporal);
                }
            }
        }

        // kon estos IDs armamos la klabe komo texto
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
     * revisa si una kadena es aseptada por este AFD.
     * va komiendose los simbolos uno por uno;
     * buelve true si al terminar estamos en un estado final
     * i todos los simbolos son validos.
     */
    public boolean validar(String cadena) {
        Estado estadoActual = estadoInicial;

        for (int i = 0; i < cadena.length(); i++) {
            char simbolo = cadena.charAt(i);

            // vemos si el simbolo esta en el alfabeto
            boolean estaEnAlfabeto = false;
            for (char c : alfabeto) {
                if (c == simbolo) {
                    estaEnAlfabeto = true;
                }
            }
            if (estaEnAlfabeto == false) {
                return false;
            }

            // vemos si ai una transision desde el estado aktual kon ese simbolo
            Map<Character, Estado> transicionesEstado = transiciones.get(estadoActual);
            if (transicionesEstado == null) {
                return false;
            }

            Estado siguienteEstado = transicionesEstado.get(simbolo);
            if (siguienteEstado == null) {
                return false;
            }

            // pasamos al siquiente estado
            estadoActual = siguienteEstado;
        }

        // vemos si el estado donde terminamos es final
        for (int i = 0; i < estadosFinales.size(); i++) {
            Estado finalEstado = estadosFinales.get(i);
            if (finalEstado == estadoActual) {
                return true;
            }
        }

        return false;
    }

    /**
     * agarra todos los estados del AFD (los ke estan en las llaves de transisiones,
     * mas el inicial i los finales).
     */
    private Set<Estado> obtenerTodosLosEstados() {
        Set<Estado> todos = new TreeSet<>(transiciones.keySet());
        todos.add(estadoInicial);
        todos.addAll(estadosFinales);
        return todos;
    }

    /**
     * dishke si este AFD ya esta minimisado.
     * korre todo el algoritmo de partir en grupos i buelve
     * false si ai algun grupo kon mas de un estado.
     * <ul>
     *   <li>al principio aparta los estados finales de los no finales</li>
     *   <li>una i otra vez parte los grupos kada vez ke los estados tengan firmas distintas</li>
     *   <li>si todos los grupos tienen solo 1 estado, el AFD es minimo</li>
     * </ul>
     */
    public boolean esMinimo() {
        // primero los partimos en finales i no finales
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

        // ahora afinamos: partimos los grupos si sus estados tienen firmas diferentes
        boolean huboCambio = true;
        while (huboCambio == true) {
            huboCambio = false;
            ArrayList nuevosGrupos = new ArrayList();

            for (int i = 0; i < grupos.size(); i++) {
                Grupo grupoActual = (Grupo) grupos.get(i);
                TreeMap firmas = new TreeMap();

                for (int j = 0; j < grupoActual.estados.size(); j++) {
                    Estado estadoActual = (Estado) grupoActual.estados.get(j);

                    // la firma: pa kada simbolo, el numero del grupo a donde yega
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

        // si ay un grupo kon mas de 1 estado, entonses no es minimo
        for (int i = 0; i < grupos.size(); i++) {
            Grupo grupo = (Grupo) grupos.get(i);
            if (grupo.estados.size() > 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * buelve un AFD minimo equivalente usando el metodo de partir en grupos.
     * <ul>
     *   <li>primero partimos los estados finales i no finales</li>
     *   <li>una y otra vez partimos los grupos si sus estados tienen firmas distintas</li>
     *   <li>armamos un AFD nuevo kon un solo estado representante por grupo</li>
     * </ul>
     */
    public AFD minimizar() {
        // 1. primero los partimos: finales i no finales
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

        // 2. afinamos: partimos grupos asta ke no kambie nada
        boolean huboCambio = true;
        while (huboCambio == true) {
            huboCambio = false;
            ArrayList nuevosGrupos = new ArrayList();

            for (int i = 0; i < grupos.size(); i++) {
                Grupo grupoActual = (Grupo) grupos.get(i);
                TreeMap firmas = new TreeMap();

                for (int j = 0; j < grupoActual.estados.size(); j++) {
                    Estado estadoActual = (Estado) grupoActual.estados.get(j);

                    // la firma: (a ke grupo yega kon kada simbolo)
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

        // 3. armamos el AFD minimo: un representante por grupo
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
     * guarda este AFD en formato de archivo .afd.
     * lineas: alfabeto (S= {a,b}), kontador de estados, IDs de finales,
     * i despues una linea de transisiones por simbolo.
     */
    public String toFormatoArchivo() {
        String resultado = "";

        // linea 1: el alfabeto komo S= {a,b}
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

        // linea 2: kuantos estados ai
        resultado = resultado + transiciones.size() + "\n";

        // linea 3: los IDs de los estados finales separados por koma
        for (int i = 0; i < estadosFinales.size(); i++) {
            if (i > 0) {
                resultado = resultado + ",";
            }
            resultado = resultado + estadosFinales.get(i).getId();
        }
        resultado = resultado + "\n";

        // las lineas de transisiones: una linea pa kada simbolo del alfabeto
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
     * lee un AFD desde un string formato .afd.
     * sakA el alfabeto, kontador de estados, estados finales, i las transisiones.
     */
    public static AFD desdeFormatoArchivo(String contenido) {
        String[] lineas = contenido.split("\n");

        // linea 1: S= {a,b}
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

        // linea 2: kuantos estados ai
        String segundaLinea = lineas[1].trim();
        int numeroEstados = Integer.parseInt(segundaLinea);

        // linea 3: los IDs de los estados finales
        String terceraLinea = lineas[2].trim();
        String[] idsFinales = terceraLinea.split(",");
        ArrayList<Integer> listaIDsFinales = new ArrayList<>();
        for (int i = 0; i < idsFinales.length; i++) {
            String id = idsFinales[i].trim();
            if (id.isEmpty() == false) {
                listaIDsFinales.add(Integer.parseInt(id));
            }
        }

        // kreA los estados del AFD
        Map<Integer, Estado> mapaIDs = new HashMap<>();
        for (int i = 0; i < numeroEstados; i++) {
            mapaIDs.put(i, new Estado(i));
        }

        // asemos una lista de los estados finales
        ArrayList<Estado> estadosFinales = new ArrayList<>();
        for (int i = 0; i < listaIDsFinales.size(); i++) {
            int idFinal = listaIDsFinales.get(i);
            estadosFinales.add(mapaIDs.get(idFinal));
        }

        // pork fin kreA el AFD
        AFD afd = new AFD(mapaIDs.get(0), estadosFinales, alfabeto);

        // las lineas de transisiones (de la linea 4 en adelante)
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
                // nos saltamos la linea bacia
            } else {
                char simboloActual = arregloAlfabeto[indiceSimbolo];
                indiceSimbolo = indiceSimbolo + 1;

                String[] pares = linea.split(",");
                for (int j = 0; j < pares.length; j++) {
                    String par = pares[j].trim();
                    if (par.isEmpty()) {
                        // nos la saltamos
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
     * muestra el AFD en la konsola pa ke la persona lo lea.
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
     * revisa si una kadena es valida i buelve el resultado
     * i tambien el kamino de estados (ej. "q0->q1->q2").
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
     * ase la tabla de transisiones pa mostrarla, i si falta alguna
     * le mete un estado de error.
     * renombra los estados i usa "E" pa el estado de error.
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
 * esto garda el resultado de validar kon traza:
 * el kamino de estados ke se siguio i si la kadena fue aseptada.
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
 * esta klase es pa agrupar estados en el algoritmo
 * de partir grupos kuando minimisamos el AFD.
 */
class Grupo {
    ArrayList estados = new ArrayList();
}
