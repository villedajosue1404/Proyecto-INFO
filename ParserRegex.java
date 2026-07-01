/**
 * Lector de expresiones regulares.
 * Lee el texto paso a paso y arma un AFN usando el algoritmo de Thompson.
 * Soporta letras, números, el O,  | , * , + y paréntesis.*/
class ParserRegex {

    private String textoRegex;
    private int posicionActual;

    /**
     * Comienza el análisis, 
     * Primero le quita los espacios al texto porque no sirven para nada, 
     * y luego pone a trabajar al parser para que arme el AFN completo.*/
    public static AFN parse(String expresion) {
        // Eliminamos los espacios en blanco
        String textoLimpio = "";
        for (int i = 0; i < expresion.length(); i++) {
            char caracter = expresion.charAt(i);
            if (caracter != ' ') {
                textoLimpio = textoLimpio + caracter;
            }
        }

        ParserRegex parser = new ParserRegex(textoLimpio);
        AFN resultado = parser.procesarExpresion();
        return resultado;
    }

    /**
     * Prepara el parser con el texto limpio y 
     * lo pone en la línea de salida (posición cero).
     */
    private ParserRegex(String texto) {
        this.textoRegex = texto;
        this.posicionActual = 0;
    }

    /**
     * Revisa si hay opciones separadas por la barrita |. 
     * Va leyendo los pedazos y los une con la operación O si encuentra una.
     */
    private AFN procesarExpresion() {
        AFN resultado = procesarTermino();

        // Mientras haya un pipe, seguimos uniendo términos
        while (posicionActual < textoRegex.length()) {
            char caracterActual = textoRegex.charAt(posicionActual);
            if (caracterActual == '|') {
                posicionActual = posicionActual + 1; // Saltamos el pipe
                AFN otroTermino = procesarTermino();
                if (otroTermino != null) {
                    if (resultado == null) {
                        resultado = otroTermino;
                    } else {
                        resultado = AlgoritmoThompson.unirO(resultado, otroTermino);
                    }
                }
            } else {
                break;
            }
        }

        return resultado;
    }

    /**
     * Pega los pedazos que van uno detrás del otro concatenación. 
     * Revisa si lo que sigue es una letra, número o paréntesis, y los forma en fila.
     */
    private AFN procesarTermino() {
        AFN primerFactor = procesarFactor();
        if (primerFactor == null) {
            return null;
        }

        // Vemos si el siguiente carácter puede iniciar otro bloque
        while (posicionActual < textoRegex.length()) {
            char siguiente = textoRegex.charAt(posicionActual);
            boolean esLetra = false;
            boolean esDigito = false;
            boolean esParentesis = false;

            if (siguiente >= 'a' && siguiente <= 'z') esLetra = true;
            if (siguiente >= 'A' && siguiente <= 'Z') esLetra = true;
            if (siguiente >= '0' && siguiente <= '9') esDigito = true;
            if (siguiente == '(') esParentesis = true;

            if (esLetra || esDigito || esParentesis) {
                AFN siguienteFactor = procesarFactor();
                if (siguienteFactor != null) {
                    primerFactor = AlgoritmoThompson.concatenar(primerFactor, siguienteFactor);
                }
            } else {
                break;
            }
        }

        return primerFactor;
    }

    /**
     * Agarra un símbolo suelto o un grupo entre paréntesis.
     * Luego se fija si tiene un operador de repetición * o + 
     * pegado al final para aplicárselo de una vez.
     */
    private AFN procesarFactor() {
        if (posicionActual >= textoRegex.length()) {
            return null;
        }

        char caracter = textoRegex.charAt(posicionActual);

        // CASO 1: Grupo entre paréntesis
        if (caracter == '(') {
            posicionActual = posicionActual + 1; // Saltamos el '('

            AFN expresionInterna = procesarExpresion();

            // Verificamos el cierre ')'
            if (posicionActual < textoRegex.length()) {
                char posibleCierre = textoRegex.charAt(posicionActual);
                if (posibleCierre == ')') {
                    posicionActual = posicionActual + 1;
                }
            }

            // Checamos si tiene * o + pegado al grupo
            while (posicionActual < textoRegex.length()) {
                char operador = textoRegex.charAt(posicionActual);
                if (operador == '*') {
                    expresionInterna = AlgoritmoThompson.aplicarEstrella(expresionInterna);
                    posicionActual = posicionActual + 1;
                } else if (operador == '+') {
                    expresionInterna = AlgoritmoThompson.aplicarMas(expresionInterna);
                    posicionActual = posicionActual + 1;
                } else {
                    break;
                }
            }

            return expresionInterna;
        }

        // CASO 2: 'z' es la cadena vacia lambda
        if (caracter == 'z') {
            posicionActual = posicionActual + 1;

            AFN afnLambda = AlgoritmoThompson.crearLambda();

            while (posicionActual < textoRegex.length()) {
                char operador = textoRegex.charAt(posicionActual);
                if (operador == '*') {
                    afnLambda = AlgoritmoThompson.aplicarEstrella(afnLambda);
                    posicionActual = posicionActual + 1;
                } else if (operador == '+') {
                    afnLambda = AlgoritmoThompson.aplicarMas(afnLambda);
                    posicionActual = posicionActual + 1;
                } else break;
            }

            return afnLambda;
        }

        // CASO 3: Es una letra o un digito (excluye 'z' porque ya se uso arriba)
        boolean esLetra = false;
        boolean esDigito = false;

        if (caracter >= 'a' && caracter <= 'y') esLetra = true;
        if (caracter >= 'A' && caracter <= 'Z') esLetra = true;
        if (caracter >= '0' && caracter <= '9') esDigito = true;

        if (esLetra || esDigito) {
            posicionActual = posicionActual + 1; // Consumimos el símbolo

            AFN afnSimbolo = AlgoritmoThompson.crearSimbolo(caracter);

            // Checamos si tiene * o + pegado a la letra/número
            while (posicionActual < textoRegex.length()) {
                char operador = textoRegex.charAt(posicionActual);
                if (operador == '*') {
                    afnSimbolo = AlgoritmoThompson.aplicarEstrella(afnSimbolo);
                    posicionActual = posicionActual + 1;
                } else if (operador == '+') {
                    afnSimbolo = AlgoritmoThompson.aplicarMas(afnSimbolo);
                    posicionActual = posicionActual + 1;
                } else {
                    break;
                }
            }

            return afnSimbolo;
        }

        return null;
    }
}