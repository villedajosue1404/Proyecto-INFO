import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

/**
 * Esto es un autómata finito no determinista (AFN).
 * Sirve para guardar el estado inicial y final, y todas las rutas posibles
 * incluyendo saltos vacíos para luego pasarlo a AFD.*/
public class AFN {
    private Estado estadoInicial;
    private Estado estadoFinal;
    
    // Mapa de transiciones asociando cada estado origen a su tabla de símbolos y destinos
    private Map<Estado, Map<Character, List<Estado>>> tablaTransiciones;

    /**
     * Crea el AFN con su estado inicial y final.
     * Deja lista la tabla de transiciones para ir agregando los caminos.*/
    public AFN(Estado estadoInicial, Estado estadoFinal) {
        this.estadoInicial = estadoInicial;
        this.estadoFinal = estadoFinal;
        this.tablaTransiciones = new TreeMap<>(); 
        
        agregarEstado(estadoInicial);
        agregarEstado(estadoFinal);
    }

    /**
     * Mete un estado a la tabla solo si no existe todavía.*/
    public void agregarEstado(Estado e) {
        tablaTransiciones.putIfAbsent(e, new HashMap<>());
    }

    /**
     * Arma un puente entre dos estados usando un símbolo.
     * Como es no determinista, puede haber varios destinos para el mismo origen y símbolo.*/
    public void agregarTransicion(Estado origen, Estado destino, char simbolo) {
        agregarEstado(origen);
        agregarEstado(destino);
        
        Map<Character, List<Estado>> transicionesOrigen = tablaTransiciones.get(origen);
        transicionesOrigen.putIfAbsent(simbolo, new ArrayList<>());
        transicionesOrigen.get(simbolo).add(destino);
    }

    /**
     * Te da el estado inicial del autómata.*/
    public Estado getEstadoInicial() { return estadoInicial; }
    
    /**
     * Te da el estado final del autómata.*/
    public Estado getEstadoFinal() { return estadoFinal; }
    
    /**
     * Te da toda la tabla de caminos armados.*/
    public Map<Estado, Map<Character, List<Estado>>> getTablaTransiciones() { return tablaTransiciones; }

    /**
     * Imprime el AFN en la consola para que lo veas bonito.*/
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
     * Arma la tabla de transiciones en texto.
     * Pone el alfabeto, los estados y a dónde se mueve cada uno.
     * Si no hay camino, se pone un "NT".*/
    public String formatearTransiciones() {
        java.util.List<Estado> listaEstados = new java.util.ArrayList<>(tablaTransiciones.keySet());
        java.util.Collections.sort(listaEstados);

        Set<Character> alfabeto = getAlfabeto();
        boolean tieneEpsilon = false;
        for (Estado est : listaEstados) {
            Map<Character, List<Estado>> trans = tablaTransiciones.get(est);
            if (trans != null && trans.containsKey('z')) {
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
            sb.append("z");
        }
        sb.append("\n");

        // linea 2: numero de estados
        sb.append(listaEstados.size()).append("\n");

        // linea 3: estado final
        sb.append(estadoFinal.getId()).append("\n");

        // lineas de transiciones: una por símbolo
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
                if (trans != null && trans.containsKey('z')) {
                    List<Estado> destinos = trans.get('z');
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
     * Saca todos los símbolos que usa el autómata,
     * ignorando los saltos vacíos (épsilon).*/
    public Set<Character> getAlfabeto() {
        Set<Character> alfabeto = new HashSet<>();
        for (Map<Character, List<Estado>> trans : tablaTransiciones.values()) {
            for (Character c : trans.keySet()) {
                if (c != 'z') {
                    alfabeto.add(c);
                }
            }
        }
        return alfabeto;
    }

    /**
     * Saca todos los estados a los que puedes llegar
     * desde un estado usando puros saltos vacíos.*/
    public Set<Estado> clausuraLambda(Estado estado) {
        Set<Estado> resultado = new HashSet<>();
        Stack<Estado> pila = new Stack<>();
        pila.push(estado);

        // Recorremos la pila buscando estados con transiciones épsilon
        while (!pila.isEmpty()) {
            Estado actual = pila.pop();
            if (!resultado.contains(actual)) {
                resultado.add(actual);
                Map<Character, List<Estado>> trans = tablaTransiciones.get(actual);
                // Si el estado tiene transiciones épsilon las seguimos
                if (trans != null && trans.containsKey('z')) {
                    for (Estado dest : trans.get('z')) {
                        pila.push(dest);
                    }
                }
            }
        }

        return resultado;
    }

    /**
     * Hace lo mismo que el anterior, pero para un grupo entero de estados.
     * Los junta todos en un solo paquete.*/
    public Set<Estado> clausuraLambda(Set<Estado> estados) {
        Set<Estado> resultado = new HashSet<>();
        for (Estado e : estados) {
            resultado.addAll(clausuraLambda(e));
        }
        return resultado;
    }

    /**
     * Se fija a dónde llegas desde un grupo de estados
     * consumiendo un solo símbolo, sin contar los saltos vacíos acá. */
    public Set<Estado> cambio(Set<Estado> estados, char simbolo) {
        Set<Estado> resultado = new HashSet<>();
        // Itera sobre cada estado buscando transiciones con el símbolo
        for (Estado e : estados) {
            Map<Character, List<Estado>> trans = tablaTransiciones.get(e);
            if (trans != null && trans.containsKey(simbolo)) {
                resultado.addAll(trans.get(simbolo));
            }
        }
        return resultado;
    }
}