/**
 * Analizador sintactico de expresiones regulares
 * Usa el metodo de analisis descendente recursivo
 * Convierte la expresion en un AFN usando la construccion de Thompson
 * Soporta letras mayusculas y minusculas digitos operador pipe estrella mas y parentesis
 */
class ParserRegex {

    private String textoRegex;
    private int posicionActual;

    /**
     * Metodo estatico principal para analizar una expresion regular
     * Limpia la expresion quitando los espacios en blanco
     * Crea un parser nuevo y procesa la expresion completa
     * @param expresion la cadena que contiene la expresion regular
     * @return el AFN equivalente a la expresion regular
     */
    public static AFN parse(String expresion) {
        // Eliminamos los espacios en blanco porque no tienen significado
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
     * Constructor privado que inicializa el parser con el texto ya limpio
     * La posicion actual empieza en cero
     * @param texto la expresion regular sin espacios en blanco
     */
    private ParserRegex(String texto) {
        this.textoRegex = texto;
        this.posicionActual = 0;
    }

    /**
     * Procesa una expresion completa separada por el operador pipe
     * Lee un termino y luego mientras haya pipes lee otro termino y los une
     * Si no hay pipe devuelve el termino unico
     * @return el AFN de la expresion o null si esta vacia
     */
    private AFN procesarExpresion() {
        AFN resultado = procesarTermino();

        // Mientras haya un pipe en la posicion actual seguimos uniendo terminos
        while (posicionActual < textoRegex.length()) {
            char caracterActual = textoRegex.charAt(posicionActual);
            if (caracterActual == '|') {
                posicionActual = posicionActual + 1; // Avanzamos para saltar el pipe
                AFN otroTermino = procesarTermino();
                if (otroTermino != null) {
                    if (resultado == null) {
                        resultado = otroTermino;
                    } else {
                        resultado = AlgoritmoThompson.unirO(resultado, otroTermino);
                    }
                }
            } else {
                // Ya no hay mas pipes entonces salimos del ciclo
            }
        }

        return resultado;
    }

    /**
     * Procesa un termino que es una secuencia de factores concatenados
     * Lee el primer factor y mientras haya mas factores los va concatenando
     * Detecta si el siguiente caracter puede iniciar un factor
     * @return el AFN del termino o null si no hay ningun factor
     */
    private AFN procesarTermino() {
        AFN primerFactor = procesarFactor();
        if (primerFactor == null) {
            return null;
        }

        // Verificamos si el siguiente caracter puede iniciar un factor
        while (posicionActual < textoRegex.length()) {
            char siguiente = textoRegex.charAt(posicionActual);
            boolean esLetra = false;
            boolean esDigito = false;
            boolean esParentesis = false;

            if (siguiente >= 'a' && siguiente <= 'z') {
                esLetra = true;
            }
            if (siguiente >= 'A' && siguiente <= 'Z') {
                esLetra = true;
            }
            if (siguiente >= '0' && siguiente <= '9') {
                esDigito = true;
            }
            if (siguiente == '(') {
                esParentesis = true;
            }

            if (esLetra == true || esDigito == true || esParentesis == true) {
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
     * Procesa un factor que puede ser un simbolo individual una expresion entre
     * parentesis o una letra o digito cada uno opcionalmente seguido de estrella o mas
     * @return el AFN del factor o null si no hay mas caracteres que procesar
     */
    private AFN procesarFactor() {
        if (posicionActual >= textoRegex.length()) {
            return null;
        }

        char caracter = textoRegex.charAt(posicionActual);

        // CASO 1: Grupo entre parentesis procesamos el interior y luego operadores
        if (caracter == '(') {
            posicionActual = posicionActual + 1; // Avanzamos para saltar el parentesis que abre

            AFN expresionInterna = procesarExpresion();

            // Verificamos que haya un parentesis que cierra la expresion
            if (posicionActual < textoRegex.length()) {
                char posibleCierre = textoRegex.charAt(posicionActual);
                if (posibleCierre == ')') {
                    posicionActual = posicionActual + 1; // Avanzamos para saltar el parentesis que cierra
                }
            }

            // Aplicamos los operadores estrella o mas si aparecen despues del grupo
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

        // CASO 2: El caracter es una letra o digito del alfabeto
        boolean esLetra = false;
        boolean esDigito = false;

        if (caracter >= 'a' && caracter <= 'z') {
            esLetra = true;
        }
        if (caracter >= 'A' && caracter <= 'Z') {
            esLetra = true;
        }
        if (caracter >= '0' && caracter <= '9') {
            esDigito = true;
        }

        if (esLetra == true || esDigito == true) {
            posicionActual = posicionActual + 1; // Avanzamos para consumir el simbolo

            AFN afnSimbolo = AlgoritmoThompson.crearSimbolo(caracter);

            // Aplicamos estrella o mas si aparecen despues del simbolo
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

        // Si llegamos aqui es que no se pudo procesar ningun factor valido
        return null;
    }
}
