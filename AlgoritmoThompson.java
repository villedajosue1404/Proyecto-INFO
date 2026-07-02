import java.util.List;
import java.util.Map;

/**
 * Esto es el algoritmo de Thompson.
 * Sirve para armar un AFN a partir de expresiones regulares, 
 * uniendo pedacitos con saltos vacíos para formar operaciones 
 * como uniones, concatenaciones o ciclos.*/
public class AlgoritmoThompson {
    private static int contadorEstados = 0;

    /**
     * Crea un estado nuevo y le pone un ID único 
     * para que no se repitan en todo el programa.*/
    private static Estado nuevoEstado() {
        return new Estado(contadorEstados++);
    }

    /**
     * Arma un AFN chiquito para un solo símbolo. 
     * Solo hace un puente directo del estado inicial al final.*/
    public static AFN crearSimbolo(char simbolo) {
        Estado inicio = nuevoEstado();
        Estado fin = nuevoEstado();
        AFN afn = new AFN(inicio, fin);
        afn.agregarTransicion(inicio, fin, simbolo);
        return afn;
    }

    /**
     * Pega dos AFNs en fila. 
     * Une el final del primer autómata con el inicio del segundo 
     * usando un salto vacío.*/
    public static AFN concatenar(AFN afn1, AFN afn2) {
        AFN resultado = new AFN(afn1.getEstadoInicial(), afn2.getEstadoFinal());
        clonarTransiciones(afn1, resultado);
        clonarTransiciones(afn2, resultado);
        resultado.agregarTransicion(afn1.getEstadoFinal(), afn2.getEstadoInicial(), 'ε');
        return resultado;
    }

    /**
     * Une dos AFNs como opciones separadas, un OR. 
     * Crea un inicio y un fin nuevos, y los conecta a las entradas 
     * y salidas de ambos autómatas con saltos.*/
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
     * Le aplica la estrella de Kleene *. 
     * Te deja repetir el autómata las veces que quieras ciclando al inicio, 
     * o saltártelo por completo yendo directo al final.*/
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
     * Le aplica el operador Más +. 
     * Es igual que la estrella te deja repetir en ciclo, pero aquí 
     * sí o sí tienes que pasar por el autómata al menos una vez.*/
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
     * Copia todas las rutas de un AFN a otro. 
     * Sirve para no perder los caminos originales cuando vamos 
     * armando autómatas más grandes juntando varios pedazos.*/
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