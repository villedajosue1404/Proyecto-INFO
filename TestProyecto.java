import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import util.Banner;
import util.BannerTest;

/**
 * Panel de control de pruebas unitarias del proyecto
 * Proporciona menus para ejecutar pruebas individuales o masivas
 * Evalua automatas AFN AFD y parsing de cadenas
 * Calcula calificaciones y guarda resultados en archivos
 */
public class TestProyecto {

    private static int totalPruebas = 0;
    private static int pruebasPasadas = 0;
    private static int pruebasFallidas = 0;

    /**
     * Punto de entrada del panel de pruebas
     * Crea un scanner lo pasa al metodo iniciar y lo cierra al terminar
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        iniciar(scanner);
        scanner.close();
    }

    /**
     * Menu principal del panel de pruebas
     * Se repite en ciclo hasta que el usuario elija salir
     * Ofrece test individual test todos borrar resultados y salir
     * Muestra el banner de presentacion al inicio
     * @param scanner el lector de entrada del usuario
     */
    public static void iniciar(Scanner scanner) {
        int opcion;

        BannerTest.mostrar();

        do {
            System.out.println("\n 1) Test individual");
            System.out.println(" 2) Test todos");
            System.out.println(" 3) Borrar resultados");
            System.out.println(" 0) Salir");
            System.out.print(" Seleccione una opcion: ");

            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1 -> testIndividual(scanner);
                case 2 -> testTodos(scanner);
                case 3 -> limpiarResultados();
                case 0 -> System.out.println("\n Saliendo del panel de pruebas...");
            }
        } while (opcion != 0);
    }

    /**
     * Pide al usuario el nombre del test y abre el menu para un solo test
     * @param scanner el lector de entrada del usuario
     */
    private static void testIndividual(Scanner scanner) {
        System.out.print("\n Ingresa el nombre del test: ");
        String nombre = scanner.nextLine().trim();
        menuTest(scanner, nombre);
    }

    /**
     * Descubre todos los tests disponibles y abre el menu multiple
     * @param scanner el lector de entrada del usuario
     */
    private static void testTodos(Scanner scanner) {
        String[] tests = descubrirTests();
        menuTestMultiple(scanner, tests);
    }

    /**
     * Menu para ejecutar pruebas sobre todos los tests descubiertos
     * Ofrece opciones para ejecutar todas las etapas o solo algunas
     * Muestra la nota final con total de correctas e incorrectas
     * @param scanner el lector de entrada del usuario
     * @param tests el arreglo con los nombres de los tests a ejecutar
     */
    private static void menuTestMultiple(Scanner scanner, String[] tests) {
        int opcion;
        do {
            Banner.mostrar("Test todos - " + tests.length + " archivos");
            System.out.println("\n Aplicar a " + tests.length + " test(s):");
            System.out.println(" 1) Hacer todos los test");
            System.out.println(" 2) Solo AFN");
            System.out.println(" 3) Solo Parsing");
            System.out.println(" 4) Solo AFD");
            System.out.println(" 5) Solo AFD minimo");
            System.out.println(" 6) Pasar AFD a AFD minimo");
            System.out.println(" 0) Volver");
            System.out.print(" Seleccione: ");

            opcion = Integer.parseInt(scanner.nextLine());

            if (opcion == 0) { System.out.println(); continue; }

            totalPruebas = 0;
            pruebasPasadas = 0;
            pruebasFallidas = 0;

            Banner.mostrar(switch (opcion) {
                case 1 -> "Todos los test";
                case 2 -> "Solo AFN";
                case 3 -> "Solo Parsing";
                case 4 -> "Solo AFD";
                case 5 -> "Solo AFD minimo";
                case 6 -> "Pasar AFD a AFD minimo";
                default -> "";
            });

            for (String test : tests) {
                switch (opcion) {
                    case 1 -> ejecutarTodo(test);
                    case 2 -> ejecutarSoloAFN(test);
                    case 3 -> ejecutarSoloParsing(test);
                    case 4 -> ejecutarSoloAFDOriginal(test);
                    case 5 -> ejecutarSoloAFD(test);
                    case 6 -> ejecutarPasarAFDaAFDMin(test);
                }
                System.out.println();
            }

            // Mostramos la nota final solo para las opciones que generan calificacion
            if (opcion == 1 || opcion == 3 || opcion == 5) {
                System.out.println("========================================");
                System.out.println("   NOTA FINAL");
                System.out.println("   Total: " + totalPruebas);
                System.out.println("   Correctas: " + pruebasPasadas);
                System.out.println("   Incorrectas: " + pruebasFallidas);
                double nota = (pruebasPasadas * 10.0) / totalPruebas;
                System.out.printf("   Calificacion: %.2f / 10\n", nota);
                System.out.println("========================================");
            }
        } while (opcion != 0);
    }

    /**
     * Menu para ejecutar pruebas sobre un solo test especifico
     * Ofrece las mismas opciones que el menu multiple
     * Se repite hasta que el usuario elija volver
     * @param scanner el lector de entrada del usuario
     * @param nombre el nombre del test a ejecutar
     */
    private static void menuTest(Scanner scanner, String nombre) {
        int opcion;
        do {
            Banner.mostrar("Test individual - " + nombre);
            System.out.println("\n Test: \"" + nombre + "\"");
            System.out.println(" 1) Hacer todos los test");
            System.out.println(" 2) Solo AFN");
            System.out.println(" 3) Solo Parsing");
            System.out.println(" 4) Solo AFD");
            System.out.println(" 5) Solo AFD minimo");
            System.out.println(" 6) Pasar AFD a AFD minimo");
            System.out.println(" 0) Volver");
            System.out.print(" Seleccione: ");

            opcion = Integer.parseInt(scanner.nextLine());

            if (opcion != 0) {
                Banner.mostrar(switch (opcion) {
                    case 1 -> "Todos los test - " + nombre;
                    case 2 -> "Solo AFN - " + nombre;
                    case 3 -> "Solo Parsing - " + nombre;
                    case 4 -> "Solo AFD - " + nombre;
                    case 5 -> "Solo AFD minimo - " + nombre;
                    case 6 -> "Pasar AFD a AFD minimo - " + nombre;
                    default -> "";
                });
            }

            // Ejecutamos la opcion seleccionada por el usuario
            switch (opcion) {
                case 1 -> ejecutarTodo(nombre);
                case 2 -> ejecutarSoloAFN(nombre);
                case 3 -> ejecutarSoloParsing(nombre);
                case 4 -> ejecutarSoloAFDOriginal(nombre);
                case 5 -> ejecutarSoloAFD(nombre);
                case 6 -> ejecutarPasarAFDaAFDMin(nombre);
                case 0 -> System.out.println();
            }
        } while (opcion != 0);
    }

    /**
     * Lee una expresion regular desde el archivo de test
     * La convierte en AFN usando el parser y muestra las transiciones
     * Si la expresion es vacia no genera AFN y muestra una advertencia
     * @param nombre el nombre del test a ejecutar
     */
    private static void ejecutarSoloAFN(String nombre) {
        String erFile = "tests/er/" + nombre + ".er";

        System.out.println("\n--- AFN: " + nombre + " ---");

        String er = LectorArchivos.leerArchivo(erFile);

        System.out.println(" ER: " + er);

        AFN afn = ParserRegex.parse(er);

        if (afn == null) {
            System.out.println(" [WARN] No se pudo generar AFN para la ER vacia (ε)");
            return;
        }
        System.out.println(" Transiciones:");
        System.out.println(afn.formatearTransiciones());
    }

    /**
     * Genera o carga un AFD lo minimiza y lo compara con el esperado
     * Si el AFD no es minimo lo minimiza automaticamente
     * Compara el resultado contra el archivo .expafd esperado
     * Guarda el resultado en la carpeta resultados/afd/
     * @param nombre el nombre del test a ejecutar
     */
    private static void ejecutarSoloAFD(String nombre) {
        String erFile = "tests/er/" + nombre + ".er";
        String afdFile = "tests/afd/" + nombre + ".afd";
        String expafdFile = "tests/expafd/" + nombre + ".expafd";

        System.out.println("\n--- AFD minimo: " + nombre + " ---");

        AFD afd = cargarGenerarAFD(nombre);

        // Si el automata no es minimo procedemos a minimizarlo
        if (!afd.esMinimo()) {
            System.out.println(" Minimizando...");
            afd = afd.minimizar();
            System.out.println(" [OK] AFD minimizado");
        } else {
            System.out.println(" AFD ya es minimo");
        }

        System.out.println("\n Transiciones (formato solicitado):");
        System.out.println(afd.formatearTransiciones());

        // Comparamos las transiciones obtenidas contra el archivo esperado
        String expafd = LectorArchivos.leerArchivo(expafdFile);
        String obtenido = afd.formatearTransiciones();
        totalPruebas++;
        if (obtenido.trim().equals(expafd.trim())) {
            System.out.println(" [CORRECTO] AFD minimo coincide con el esperado");
            pruebasPasadas++;
        } else {
            System.out.println(" [INCORRECTO] AFD minimo NO coincide");
            System.out.println("  Esperado:");
            System.out.println("  " + expafd.replace("\n", "\n  "));
            System.out.println("  Obtenido:");
            System.out.println("  ");
            System.out.println(afd.formatearTransiciones().replace("\n", "\n  "));
            pruebasFallidas++;
        }

        // Guardamos el resultado obtenido en la carpeta resultados/afd/
        String resultadoAFDFile = "resultados/afd/" + nombre + ".afd";
        new File("resultados/afd/").mkdirs();
        try (FileWriter fw = new FileWriter(resultadoAFDFile)) {
            fw.write(afd.formatearTransiciones());
        } catch (IOException e) {
        }
    }

    /**
     * Lee las cadenas del archivo .txt y las evalua con el AFD
     * Compara cada resultado contra el booleano esperado del .exparsin
     * Lleva el conteo de pruebas correctas e incorrectas
     * Guarda los resultados en la carpeta resultados/parsin/
     * @param nombre el nombre del test a ejecutar
     */
    private static void ejecutarSoloParsing(String nombre) {
        String txtFile = "tests/txt/" + nombre + ".txt";
        String exparsinFile = "tests/exparsin/" + nombre + ".exparsin";

        System.out.println("\n--- Parsing: " + nombre + " ---");

        AFD afd = cargarGenerarAFD(nombre);

        String txt = LectorArchivos.leerArchivo(txtFile);
        String exparsin = LectorArchivos.leerArchivo(exparsinFile);

        String[] cadenas = txt.split("\n", -1);
        String[] esperados = exparsin.split("\n", -1);
        int minLen = Math.min(cadenas.length, esperados.length);

        int pInicio = pruebasPasadas;
        int tInicio = totalPruebas;

        System.out.println(" Validacion de cadenas:");
        // Evaluamos cada cadena contra el automata AFD
        for (int i = 0; i < minLen; i++) {
            String cadena = cadenas[i].trim();
            if (cadena.equals("\u03B5")) cadena = "";
            boolean esperado = Boolean.parseBoolean(esperados[i].trim());

            ResultadoValidacion res = afd.validarConTraza(cadena);
            String display = cadena.isEmpty() ? "(vacio)" : cadena;

            totalPruebas++;
            if (res.aceptada == esperado) {
                pruebasPasadas++;
            } else {
                pruebasFallidas++;
            }
            System.out.println(" " + display + " " + res.aceptada);
        }

        int pDelta = pruebasPasadas - pInicio;
        int tDelta = totalPruebas - tInicio;
        System.out.println(" Parsing: " + pDelta + "/" + tDelta + " correctas");

        // Guardamos los resultados de validacion en la carpeta resultados/parsin/
        String resultadoParsinFile = "resultados/parsin/" + nombre + ".txt";
        new File("resultados/parsin/").mkdirs();
        try (FileWriter fw = new FileWriter(resultadoParsinFile)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < minLen; i++) {
                String cadena = cadenas[i].trim();
                if (cadena.equals("\u03B5")) cadena = "";
                ResultadoValidacion res = afd.validarConTraza(cadena);
                sb.append(cadena).append(":").append(res.aceptada).append("\n");
            }
            fw.write(sb.toString().trim());
        } catch (IOException e) {
        }
    }

    /**
     * Ejecuta todas las etapas del test AFN AFD y Parsing
     * Muestra la nota final con el total de pruebas correctas
     * Calcula la calificacion sobre 10 puntos
     * @param nombre el nombre del test a ejecutar
     */
    private static void ejecutarTodo(String nombre) {
        System.out.println("\n========== TEST COMPLETO: " + nombre + " ==========");

        ejecutarSoloAFN(nombre);
        System.out.println();

        int antesTotal = totalPruebas;
        int antesPasadas = pruebasPasadas;
        int antesFallidas = pruebasFallidas;

        ejecutarSoloAFD(nombre);
        System.out.println();

        ejecutarSoloParsing(nombre);

        int testPruebas = totalPruebas - antesTotal;
        int testPasadas = pruebasPasadas - antesPasadas;
        double nota = (testPasadas * 10.0) / testPruebas;
        System.out.println("\n --- NOTA ---");
        System.out.println(" Total pruebas: " + testPruebas);
        System.out.println(" Correctas: " + testPasadas);
        System.out.println(" Incorrectas: " + (testPruebas - testPasadas));
        System.out.printf(" Calificacion: %.2f / 10\n", nota);
    }

    /**
     * Carga o genera un AFD sin minimizar y muestra sus transiciones
     * Indica si el automata actual ya es minimo o no
     * @param nombre el nombre del test a ejecutar
     */
    private static void ejecutarSoloAFDOriginal(String nombre) {
        String erFile = "tests/er/" + nombre + ".er";
        String afdFile = "tests/afd/" + nombre + ".afd";

        System.out.println("\n--- AFD original: " + nombre + " ---");

        AFD afd = cargarAFDSinMinimizar(nombre);

        System.out.println(" Minimo? " + (afd.esMinimo() ? "SI" : "NO"));
        System.out.println(" Transiciones:");
        System.out.println(afd.formatearTransiciones());
    }

    /**
     * Carga un AFD y lo minimiza mostrando ambos estados
     * Muestra el AFD original y luego el AFD minimizado
     * Si ya es minimo solo muestra el original
     * @param nombre el nombre del test a ejecutar
     */
    private static void ejecutarPasarAFDaAFDMin(String nombre) {
        String erFile = "tests/er/" + nombre + ".er";
        String afdFile = "tests/afd/" + nombre + ".afd";

        System.out.println("\n--- Pasar AFD a AFD minimo: " + nombre + " ---");

        AFD original = cargarAFDSinMinimizar(nombre);

        System.out.println("\n AFD original" + (original.esMinimo() ? " (ya es minimo)" : ""));
        System.out.println(original.formatearTransiciones());

        if (!original.esMinimo()) {
            System.out.println("\n Minimizando...");
            AFD minimo = original.minimizar();
            System.out.println(" AFD minimo:");
            System.out.println(minimo.formatearTransiciones());
        }
    }

    /**
     * Carga o genera un AFD desde el archivo ER del test sin minimizar
     * Si la generacion falla intenta leer un archivo .afd existente
     * @param nombre el nombre del test a cargar
     * @return el AFD generado o cargado sin minimizar
     */
    private static AFD cargarAFDSinMinimizar(String nombre) {
        String erFile = "tests/er/" + nombre + ".er";
        String afdFile = "tests/afd/" + nombre + ".afd";

        try {
            String er = LectorArchivos.leerArchivo(erFile);
            AFN afn = ParserRegex.parse(er);
            return AFD.desdeAFN(afn);
        // Si no se pudo generar desde ER leemos el archivo AFD existente
        } catch (Exception e) {
            String contenido = LectorArchivos.leerArchivo(afdFile);
            return AFD.desdeFormatoArchivo(contenido);
        }
    }

    /**
     * Carga o genera un AFD desde la ER del test y lo minimiza
     * Si la generacion falla intenta leer un archivo .afd existente
     * Garantiza que el resultado sea un AFD minimo
     * @param nombre el nombre del test a cargar
     * @return el AFD minimo generado o cargado
     */
    private static AFD cargarGenerarAFD(String nombre) {
        String erFile = "tests/er/" + nombre + ".er";
        String afdFile = "tests/afd/" + nombre + ".afd";

        AFD afd;
        try {
            String er = LectorArchivos.leerArchivo(erFile);
            AFN afn = ParserRegex.parse(er);
            afd = AFD.desdeAFN(afn);
        // Si no se pudo generar desde ER leemos el archivo AFD existente
        } catch (Exception e) {
            String contenido = LectorArchivos.leerArchivo(afdFile);
            afd = AFD.desdeFormatoArchivo(contenido);
        }
        // Garantizamos que el AFD quede minimizado antes de devolverlo
        if (!afd.esMinimo()) {
            afd = afd.minimizar();
        }
        return afd;
    }

    /**
     * Descubre los nombres de todos los tests disponibles
     * Busca archivos .er en tests/er/ y .afd en tests/afd/
     * Combina los nombres en un conjunto ordenado sin duplicados
     * @return un arreglo con los nombres de los tests ordenados
     */
    private static String[] descubrirTests() {
        java.util.Set<String> nombres = new java.util.TreeSet<>();

        File dirER = new File("tests/er/");
        for (File f : dirER.listFiles()) {
            String nom = f.getName();
            if (nom.endsWith(".er")) {
                nombres.add(nom.substring(0, nom.length() - 3));
            }
        }

        File dirAFD = new File("tests/afd/");
        for (File f : dirAFD.listFiles()) {
            String nom = f.getName();
            if (nom.endsWith(".afd")) {
                nombres.add(nom.substring(0, nom.length() - 4));
            }
        }

        return nombres.toArray(new String[0]);
    }

    /**
     * Elimina todos los resultados guardados de pruebas anteriores
     * Borra las carpetas resultados/afd/ y resultados/parsin/
     * Muestra un mensaje de confirmacion al terminar
     */
    private static void limpiarResultados() {
        Banner.mostrar("Borrar resultados");
        borrarDir(new File("resultados/afd/"));
        borrarDir(new File("resultados/parsin/"));
        System.out.println("\n Resultados limpiados.");
    }

    /**
     * Elimina todos los archivos dentro de un directorio
     * Itera sobre los archivos y los borra uno por uno
     * @param dir el directorio cuyo contenido se va a eliminar
     */
    private static void borrarDir(File dir) {
        for (File f : dir.listFiles()) f.delete();
    }
}
