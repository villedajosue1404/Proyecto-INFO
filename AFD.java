import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Autómata Finito Determinista (AFD). 
 * Transforma AFNs, valida cadenas, minimiza estados y lee o escribe en formato .afd.
 */
class AFD {
    Estado estadoInicial;
    ArrayList<Estado> estadosFinales;
    Set<Character> alfabeto;
    Map<Estado, Map<Character, Estado>> transiciones;

    /** Inicia el AFD con su estado inicial, finales y alfabeto. */
    public AFD(Estado inicial, ArrayList<Estado> finales, Set<Character> alfabeto) {
        this.estadoInicial = inicial;
        this.estadosFinales = finales;
        this.alfabeto = alfabeto;
        this.transiciones = new TreeMap<>();
    }

    /** * Convierte un AFN a AFD usando subconjuntos. 
     * Agrupa estados unidos por transiciones vacías  en un solo estado nuevo. 
     */
    public static AFD desdeAFN(AFN afn) {
        Set<Character> alfabeto = afn.getAlfabeto();

        Set<Estado> clausuraInicial = afn.clausuraLambda(afn.getEstadoInicial());

        Map<String, Estado> mapaClaves = new HashMap<>();
        Map<Estado, Set<Estado>> mapaRepresentacion = new HashMap<>();

        ArrayList<Estado> finales = new ArrayList<>();
        int contadorEstadosAFD = 0;

        Estado estadoInicialAFD = new Estado(contadorEstadosAFD);
        contadorEstadosAFD++;

        String claveInicial = generarClaveUnica(clausuraInicial);
        mapaClaves.put(claveInicial, estadoInicialAFD);
        mapaRepresentacion.put(estadoInicialAFD, new HashSet<>(clausuraInicial));

        if (clausuraInicial.contains(afn.getEstadoFinal())) {
            finales.add(estadoInicialAFD);
        }

        AFD afd = new AFD(estadoInicialAFD, finales, alfabeto);

        LinkedList<Estado> colaEstados = new LinkedList<>();
        colaEstados.add(estadoInicialAFD);

        while (colaEstados.size() > 0) {
            Estado estadoActualAFD = colaEstados.removeFirst();
            Set<Estado> estadosAFNActual = mapaRepresentacion.get(estadoActualAFD);

            for (char simbolo : alfabeto) {
                Set<Estado> estadosCambio = afn.cambio(estadosAFNActual, simbolo);

                if (estadosCambio.size() > 0) {
                    Set<Estado> estadosClausura = afn.clausuraLambda(estadosCambio);

                    if (estadosClausura.size() > 0) {
                        String claveConjunto = generarClaveUnica(estadosClausura);
                        Estado destinoAFD = mapaClaves.get(claveConjunto);

                        if (destinoAFD == null) {
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

    /** Crea una clave única  para no repetir grupos de estados. */
    private static String generarClaveUnica(Set<Estado> estados) {
        ArrayList<Integer> listaIDs = new ArrayList<>();
        for (Estado estado : estados) {
            listaIDs.add(estado.getId());
        }

        for (int i = 0; i < listaIDs.size(); i++) {
            for (int j = i + 1; j < listaIDs.size(); j++) {
                if (listaIDs.get(i) > listaIDs.get(j)) {
                    int temporal = listaIDs.get(i);
                    listaIDs.set(i, listaIDs.get(j));
                    listaIDs.set(j, temporal);
                }
            }
        }

        String clave = "";
        for (int i = 0; i < listaIDs.size(); i++) {
            if (i > 0) {
                clave = clave + ",";
            }
            clave = clave + "q" + listaIDs.get(i);
        }

        return clave;
    }

    /** Lee la cadena paso a paso. Si termina en un estado final, devuelve true. */
    public boolean validar(String cadena) {
        Estado estadoActual = estadoInicial;

        for (int i = 0; i < cadena.length(); i++) {
            char simbolo = cadena.charAt(i);

            boolean estaEnAlfabeto = false;
            for (char c : alfabeto) {
                if (c == simbolo) {
                    estaEnAlfabeto = true;
                }
            }
            if (estaEnAlfabeto == false) {
                return false;
            }

            Map<Character, Estado> transicionesEstado = transiciones.get(estadoActual);
            if (transicionesEstado == null) {
                return false;
            }

            Estado siguienteEstado = transicionesEstado.get(simbolo);
            if (siguienteEstado == null) {
                return false;
            }

            estadoActual = siguienteEstado;
        }

        for (int i = 0; i < estadosFinales.size(); i++) {
            Estado finalEstado = estadosFinales.get(i);
            if (finalEstado == estadoActual) {
                return true;
            }
        }

        return false;
    }

    /** Devuelve todos los estados del AFD sin repetir. */
    private Set<Estado> obtenerTodosLosEstados() {
        Set<Estado> todos = new TreeSet<>(transiciones.keySet());
        todos.add(estadoInicial);
        todos.addAll(estadosFinales);
        return todos;
    }

    /** Comprueba si el AFD ya es mínimo viendo si se pueden agrupar más estados. */
    public boolean esMinimo() {
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
        if (grupoFinal.estados.size() > 0) grupos.add(grupoFinal);
        if (grupoNoFinal.estados.size() > 0) grupos.add(grupoNoFinal);

        boolean huboCambio = true;
        while (huboCambio == true) {
            huboCambio = false;
            ArrayList nuevosGrupos = new ArrayList();

            for (int i = 0; i < grupos.size(); i++) {
                Grupo grupoActual = (Grupo) grupos.get(i);
                TreeMap firmas = new TreeMap();

                for (int j = 0; j < grupoActual.estados.size(); j++) {
                    Estado estadoActual = (Estado) grupoActual.estados.get(j);
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

        for (int i = 0; i < grupos.size(); i++) {
            Grupo grupo = (Grupo) grupos.get(i);
            if (grupo.estados.size() > 1) {
                return false;
            }
        }

        return true;
    }

    /** Achica el AFD. Fusiona estados redundantes que hacen lo mismo y arma uno nuevo. */
    public AFD minimizar() {
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
        if (grupoFinal.estados.size() > 0) grupos.add(grupoFinal);
        if (grupoNoFinal.estados.size() > 0) grupos.add(grupoNoFinal);

        boolean huboCambio = true;
        while (huboCambio == true) {
            huboCambio = false;
            ArrayList nuevosGrupos = new ArrayList();

            for (int i = 0; i < grupos.size(); i++) {
                Grupo grupoActual = (Grupo) grupos.get(i);
                TreeMap firmas = new TreeMap();

                for (int j = 0; j < grupoActual.estados.size(); j++) {
                    Estado estadoActual = (Estado) grupoActual.estados.get(j);
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

    /** Convierte el AFD a texto listo para guardarse alfabeto, estados, finales y trans. */
    public String toFormatoArchivo() {
        String resultado = "";

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

        resultado = resultado + transiciones.size() + "\n";

        for (int i = 0; i < estadosFinales.size(); i++) {
            if (i > 0) {
                resultado = resultado + ",";
            }
            resultado = resultado + estadosFinales.get(i).getId();
        }
        resultado = resultado + "\n";

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

    /** Reconstruye un AFD leyendo el texto de un archivo .afd. */
    public static AFD desdeFormatoArchivo(String contenido) {
        String[] lineas = contenido.split("\n");

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

        String segundaLinea = lineas[1].trim();
        int numeroEstados = Integer.parseInt(segundaLinea);

        String terceraLinea = lineas[2].trim();
        String[] idsFinales = terceraLinea.split(",");
        ArrayList<Integer> listaIDsFinales = new ArrayList<>();
        for (int i = 0; i < idsFinales.length; i++) {
            String id = idsFinales[i].trim();
            if (id.isEmpty() == false) {
                listaIDsFinales.add(Integer.parseInt(id));
            }
        }

        Map<Integer, Estado> mapaIDs = new HashMap<>();
        for (int i = 0; i < numeroEstados; i++) {
            mapaIDs.put(i, new Estado(i));
        }

        ArrayList<Estado> estadosFinales = new ArrayList<>();
        for (int i = 0; i < listaIDsFinales.size(); i++) {
            int idFinal = listaIDsFinales.get(i);
            estadosFinales.add(mapaIDs.get(idFinal));
        }

        AFD afd = new AFD(mapaIDs.get(0), estadosFinales, alfabeto);

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
                // Nos la saltamos
            } else {
                char simboloActual = arregloAlfabeto[indiceSimbolo];
                indiceSimbolo = indiceSimbolo + 1;

                String[] pares = linea.split(",");
                for (int j = 0; j < pares.length; j++) {
                    String par = pares[j].trim();
                    if (par.isEmpty()) {
                        // Nos la saltamos
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

    /** Muestra el AFD en consola de forma legible. */
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

    /** Valida la cadena pero también te devuelve la ruta exacta de estados que tomó. */
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

        return new ResultadoValidacion(traza.toString(),