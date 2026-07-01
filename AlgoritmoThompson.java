import java.util.List;
import java.util.Map;

/**
 * Implementacion del algoritmo de construccion de Thompson
 * Convierte expresiones regulares en automatas finitos no deterministas AFN
 * Soporta creacion de simbolos concatenacion union estrella de Kleene y mas
 * Cada operacion construye un AFN con estados conectados por epsilon
 */
public class AlgoritmoThompson {
    private static int contadorEstados = 0;

    /**
     * Crea un nuevo estado con un id autoincremental
     * El contador es estatico por lo que los ids son unicos globalmente
     * @return el nuevo estado creado
     */
    private static Estado nuevoEstado() {
        return new Estado(contadorEstados++);
    }

    /**
     * Construye un AFN basico para un solo simbolo del alfabeto
     * Crea un estado inicial y un estado final con una transicion directa
     * @param simbolo el caracter que representa la transicion
     * @return el AFN con dos estados conectados por el simbolo
     */
    public static AFN crearSimbolo(char simbolo) {
        Estado inicio = nuevoEstado();
        Estado fin = nuevoEstado();
        AFN afn = new AFN(inicio, fin);
        afn.agregarTransicion(inicio, fin, simbolo);
        return afn;
    }

    /**
     * Concatena dos AFN en serie uno despues del otro
     * Conecta el estado final del primero con el inicial del segundo mediante epsilon
     * El resultado tiene el inicio del primer AFN y el fin del segundo AFN
     * @param afn1 el primer automata
     * @param afn2 el segundo automata
     * @return el AFN concatenado
     */
    public static AFN concatenar(AFN afn1, AFN afn2) {
        AFN resultado = new AFN(afn1.getEstadoInicial(), afn2.getEstadoFinal());
        clonarTransiciones(afn1, resultado);
        clonarTransiciones(afn2, resultado);
        resultado.agregarTransicion(afn1.getEstadoFinal(), afn2.getEstadoInicial(), 'ε');
        return resultado;
    }

    /**
     * Construye la union de dos AFN usando el operador OR
     * Crea un nuevo estado inicial con transiciones epsilon hacia ambos automatas
     * Crea un nuevo estado final que recibe epsilon desde ambos automatas
     * @param afn1 el primer automata
     * @param afn2 el segundo automata
     * @return el AFN que representa la union de ambos
     */
    public static AFN unirO(AFN afn1, AFN afn2) {
        Estado nuevoInicio = nuevoEstado();
        Estado nuevoFin = nuevoEstado();
        AFN resultado = new AFN(nuevoInicio, nuevoFin);

        clonarTransiciones(afn1, resultado);
        clonarTransiciones(afn2, resultado);

        resultado.agregarTransicion(nuevoInicio, afn1.getEstadoInicial(), 'ε');
        resultado.agregarTransicion(nuevoInicio, afn2.getEstadoInicial(), 'ε');
        resultado.agregarTransicion(afn1.getEstadoFinal(), nuevoFin, 'ε');
        resultado.agregarTransicion(afn2.getEstadoFinal(), nuevoFin, 'ε');

        return resultado;
    }

    /**
     * Aplica la estrella de Kleene al AFN cero o mas repeticiones
     * Agrega epsilon desde el nuevo inicio al nuevo fin
     * Agrega epsilon desde el nuevo inicio al inicio original
     * Agrega epsilon desde el final original al nuevo fin
     * Agrega epsilon desde el final original al inicio original
     * @param afn el automata al que aplicar la estrella
     * @return el AFN con la operacion estrella aplicada
     */
    public static AFN aplicarEstrella(AFN afn) {
        Estado nuevoInicio = nuevoEstado();
        Estado nuevoFin = nuevoEstado();
        AFN resultado = new AFN(nuevoInicio, nuevoFin);

        clonarTransiciones(afn, resultado);

        resultado.agregarTransicion(afn.getEstadoFinal(), afn.getEstadoInicial(), 'ε');
        resultado.agregarTransicion(nuevoInicio, nuevoFin, 'ε');
        resultado.agregarTransicion(nuevoInicio, afn.getEstadoInicial(), 'ε');
        resultado.agregarTransicion(afn.getEstadoFinal(), nuevoFin, 'ε');

        return resultado;
    }

    /**
     * Aplica el operador mas al AFN una o mas repeticiones
     * Similar a la estrella pero sin permitir cero repeticiones
     * No tiene la transicion epsilon directa del inicio al fin
     * @param afn el automata al que aplicar el mas
     * @return el AFN con la operacion mas aplicada
     */
    public static AFN aplicarMas(AFN afn) {
        Estado nuevoInicio = nuevoEstado();
        Estado nuevoFin = nuevoEstado();
        AFN resultado = new AFN(nuevoInicio, nuevoFin);

        clonarTransiciones(afn, resultado);

        resultado.agregarTransicion(afn.getEstadoFinal(), afn.getEstadoInicial(), 'ε');
        resultado.agregarTransicion(nuevoInicio, afn.getEstadoInicial(), 'ε');
        resultado.agregarTransicion(afn.getEstadoFinal(), nuevoFin, 'ε');

        return resultado;
    }

    /**
     * Clona todas las transiciones de un AFN origen a un AFN destino
     * Itera sobre cada estado y cada simbolo copiando los destinos
     * Util para construir nuevos automatas sin perder las transiciones originales
     * @param origen el AFN del cual copiar las transiciones
     * @param destino el AFN al cual agregar las transiciones copiadas
     */
    private static void clonarTransiciones(AFN origen, AFN destino) {
        for (Map.Entry<Estado, Map<Character, List<Estado>>> entrada : origen.getTablaTransiciones().entrySet()) {
            Estado estOrigen = entrada.getKey();
            for (Map.Entry<Character, List<Estado>> camino : entrada.getValue().entrySet()) {
                char sim = camino.getKey();
                for (Estado estDestino : camino.getValue()) {
                    destino.agregarTransicion(estOrigen, estDestino, sim);
                }
            }
        }
    }
}
