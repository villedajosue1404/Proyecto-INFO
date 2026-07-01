import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

/**
 * Implementacion de un automata finito no determinista AFN
 * Contiene un estado inicial y un estado final
 * Almacena las transiciones en un mapa que asocia estado origen con
 * un mapa de simbolo a lista de estados destino soportando no determinismo
 * Se utiliza como paso intermedio para convertir expresiones regulares a AFD
 */
public class AFN {
    private Estado estadoInicial;
    private Estado estadoFinal;
    
    // Mapa de transiciones asociando cada estado origen a su tabla de simbolos y destinos
    private Map<Estado, Map<Character, List<Estado>>> tablaTransiciones;

    /**
     * Constructor que crea un AFN con los estados inicial y final especificados
     * Inicializa el mapa de transiciones como un TreeMap para mantener el orden
     * Agrega ambos estados al mapa de transiciones
     * @param estadoInicial el estado donde empieza el automata
     * @param estadoFinal el estado donde el automata acepta la cadena
     */
    public AFN(Estado estadoInicial, Estado estadoFinal) {
        this.estadoInicial = estadoInicial;
        this.estadoFinal = estadoFinal;
        this.tablaTransiciones = new TreeMap<>(); 
        
        agregarEstado(estadoInicial);
        agregarEstado(estadoFinal);
    }

    /**
     * Agrega un estado a la tabla de transiciones si no existe ya
     * Utiliza putIfAbsent para evitar sobrescribir estados existentes
     * @param e el estado a agregar
     */
    public void agregarEstado(Estado e) {
        tablaTransiciones.putIfAbsent(e, new HashMap<>());
    }

    /**
     * Agrega una transicion desde un estado origen a un estado destino con un simbolo
     * Si los estados no existen en la tabla los agrega automaticamente
     * Soporta multiples destinos para el mismo origen y simbolo no determinismo
     * @param origen el estado de donde parte la transicion
     * @param destino el estado al que llega la transicion
     * @param simbolo el caracter que activa la transicion
     */
    public void agregarTransicion(Estado origen, Estado destino, char simbolo) {
        agregarEstado(origen);
        agregarEstado(destino);
        
        Map<Character, List<Estado>> transicionesOrigen = tablaTransiciones.get(origen);
        transicionesOrigen.putIfAbsent(simbolo, new ArrayList<>());
        transicionesOrigen.get(simbolo).add(destino);
    }

    /**
     * Devuelve el estado inicial del automata
     * @return el estado inicial
     */
    public Estado getEstadoInicial() { return estadoInicial; }
    
    /**
     * Devuelve el estado final del automata
     * @return el estado final
     */
    public Estado getEstadoFinal() { return estadoFinal; }
    
    /**
     * Devuelve el mapa completo de transiciones del automata
     * @return el mapa de transiciones
     */
    public Map<Estado, Map<Character, List<Estado>>> getTablaTransiciones() { return tablaTransiciones; }

    /**
     * Imprime el automata en la consola para depuracion
     * Muestra el estado inicial el estado final y todas las transiciones
     * Cada transicion se muestra en formato origen --simbolo--> destino
     */
    public void imprimirAutomata() {
        System.out.println("--- AFN Generado ---");
        System.out.println("Inicio: " + estadoInicial);
        System.out.println("Fin: " + estadoFinal);
        System.out.println("Transiciones:");
        
        for (Map.Entry<Estado, Map<Character, List<Estado>>> entrada : tablaTransiciones.entrySet()) {
            Estado origen = entrada.getKey();
            for (Map.Entry<Character, List<Estado>> camino : entrada.getValue().entrySet()) {
                char simbolo = camino.getKey();
                for (Estado destino : camino.getValue()) {
                    System.out.println("  " + origen + " --(" + simbolo + ")--> " + destino);
                }
            }
        }
    }

    /**
     * Genera una representacion en texto de las transiciones en formato tabla
     * Similar al formato usado por AFD con NT para indicar que no hay transicion
     * Incluye el alfabeto el numero de estados el estado final y las transiciones
     * Las transiciones epsilon se incluyen al final si existen
     * @return el string formateado con todas las transiciones
     */
    public String formatearTransiciones() {
        java.util.List<Estado> listaEstados = new java.util.ArrayList<>(tablaTransiciones.keySet());
        java.util.Collections.sort(listaEstados);

        Set<Character> alfabeto = getAlfabeto();
        boolean tieneEpsilon = false;
        for (Estado est : listaEstados) {
            Map<Character, List<Estado>> trans = tablaTransiciones.get(est);
            if (trans != null && trans.containsKey('ε')) {
                tieneEpsilon = true;
                break;
            }
        }

        StringBuilder sb = new StringBuilder();

        // linea 1: alfabeto
        boolean primero = true;
        for (char c : alfabeto) {
            if (!primero) sb.append(",");
            sb.append(c);
            primero = false;
        }
        if (tieneEpsilon) {
            if (!primero) sb.append(",");
            sb.append("ε");
        }
        sb.append("\n");

        // linea 2: numero de estados
        sb.append(listaEstados.size()).append("\n");

        // linea 3: estado final
        sb.append(estadoFinal.getId()).append("\n");

        // lineas de transiciones: una por simbolo
        for (char simbolo : alfabeto) {
            boolean primeroE = true;
            for (Estado est : listaEstados) {
                if (!primeroE) sb.append(",");
                Map<Character, List<Estado>> trans = tablaTransiciones.get(est);
                if (trans != null && trans.containsKey(simbolo)) {
                    List<Estado> destinos = trans.get(simbolo);
                    boolean primeroD = true;
                    for (Estado d : destinos) {
                        if (!primeroD) sb.append(" ");
                        sb.append(d.getId());
                        primeroD = false;
                    }
                } else {
                    sb.append("NT");
                }
                primeroE = false;
            }
            sb.append(".\n");
        }

        // linea de epsilon (si existe)
        if (tieneEpsilon) {
            boolean primeroE = true;
            for (Estado est : listaEstados) {
                if (!primeroE) sb.append(",");
                Map<Character, List<Estado>> trans = tablaTransiciones.get(est);
                if (trans != null && trans.containsKey('ε')) {
                    List<Estado> destinos = trans.get('ε');
                    boolean primeroD = true;
                    for (Estado d : destinos) {
                        if (!primeroD) sb.append(" ");
                        sb.append(d.getId());
                        primeroD = false;
                    }
                } else {
                    sb.append("NT");
                }
                primeroE = false;
            }
            sb.append(".\n");
        }

        return sb.toString();
    }

    /**
     * Obtiene el conjunto de simbolos del alfabeto que usa el automata
     * Excluye el caracter epsilon porque no es parte del alfabeto de entrada
     * Itera sobre todas las transiciones recolectando los simbolos distintos
     * @return el conjunto de simbolos del alfabeto
     */
    public Set<Character> getAlfabeto() {
        Set<Character> alfabeto = new HashSet<>();
        for (Map<Character, List<Estado>> trans : tablaTransiciones.values()) {
            for (Character c : trans.keySet()) {
                if (c != 'ε') {
                    alfabeto.add(c);
                }
            }
        }
        return alfabeto;
    }

    /**
     * Calcula la clausura epsilon desde un estado individual
     * La clausura epsilon incluye el mismo estado y todos los estados
     * alcanzables mediante una o mas transiciones epsilon
     * Usa una pila para realizar un recorrido en profundidad
     * @param estado el estado desde el cual calcular la clausura
     * @return el conjunto de estados alcanzables via epsilon
     */
    public Set<Estado> clausuraLambda(Estado estado) {
        Set<Estado> resultado = new HashSet<>();
        Stack<Estado> pila = new Stack<>();
        pila.push(estado);

        // Recorremos la pila buscando estados con transiciones epsilon
        while (!pila.isEmpty()) {
            Estado actual = pila.pop();
            if (!resultado.contains(actual)) {
                resultado.add(actual);
                Map<Character, List<Estado>> trans = tablaTransiciones.get(actual);
                // Si el estado tiene transiciones epsilon las seguimos
                if (trans != null && trans.containsKey('ε')) {
                    for (Estado dest : trans.get('ε')) {
                        pila.push(dest);
                    }
                }
            }
        }

        return resultado;
    }

    /**
     * Calcula la clausura epsilon para un conjunto de estados
     * Combina las clausuras individuales de cada estado en el conjunto
     * Util para la conversion de AFN a AFD por subconjuntos
     * @param estados el conjunto de estados desde los cuales calcular
     * @return la union de todas las clausuras epsilon del conjunto
     */
    public Set<Estado> clausuraLambda(Set<Estado> estados) {
        Set<Estado> resultado = new HashSet<>();
        for (Estado e : estados) {
            resultado.addAll(clausuraLambda(e));
        }
        return resultado;
    }

    /**
     * Calcula los estados alcanzables desde un conjunto de estados con un simbolo
     * Para cada estado en el conjunto busca si existe una transicion con el simbolo
     * No incluye la clausura epsilon eso se hace despues
     * @param estados el conjunto de estados de partida
     * @param simbolo el caracter de la transicion
     * @return los estados destino alcanzados con el simbolo
     */
    public Set<Estado> cambio(Set<Estado> estados, char simbolo) {
        Set<Estado> resultado = new HashSet<>();
        // itera kada estado buscando transisiones kon el simbolo
        for (Estado e : estados) {
            Map<Character, List<Estado>> trans = tablaTransiciones.get(e);
            if (trans != null && trans.containsKey(simbolo)) {
                resultado.addAll(trans.get(simbolo));
            }
        }
        return resultado;
    }
}
