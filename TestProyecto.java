import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import util.Banner;
import util.BannerTest;

/**
 * Menú interactivo para correr las pruebas  del proyecto.
 * Evalúa que los AFN, AFD y la validación de cadenas funcionen bien, 
 * y te saca una calificación final guardando los resultados.
 */
public class TestProyecto {

    private static int totalPruebas = 0;
    private static int pruebasPasadas = 0;
    private static int pruebasFallidas = 0;

    /**
     * Arranca el panel de pruebas.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        iniciar(scanner);
        scanner.close();
    }

    /**
     * Menú principal de pruebas. 
     * Te deja elegir si probar un solo archivo, todos a la vez o limpiar resultados.
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
     * Te pide el nombre del test y abre su menú específico.
     */
    private static void testIndividual(Scanner scanner) {
        System.out.print("\n Ingresa el nombre del test: ");
        String nombre = scanner.nextLine().trim();
        menuTest(scanner, nombre);
    }

    /**
     * Busca todos los tests guardados y abre el menú para correrlos todos.
     */
    private static void testTodos(Scanner scanner) {
        String[] tests = descubrirTests();
        menuTestMultiple(scanner, tests);
    }

    /**
     * Menú para correr las pruebas en varios archivos al mismo tiempo.
     * Al final te muestra cuántas pasaste y tu nota sobre 10.
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

            // Muestra la nota final solo si es una prueba calificada
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
     * Menú para hacerle las pruebas a un solo archivo.
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
     * Arma el AFN usando el parser y te muestra por dónde van los caminos.
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
     * Minimiza el AFD y revisa si es exactamente igual al que se esperaba.
     * Luego guarda el resultado.
     */
    private static void ejecutarSoloAFD(String nombre) {
        String erFile = "tests/er/" + nombre + ".er";
        String afdFile = "tests/afd/" + nombre + ".afd";
        String expafdFile = "tests/expafd/" + nombre + ".expafd";

        System.out.println("\n--- AFD minimo: " + nombre + " ---");

        AFD afd = cargarGenerarAFD(nombre);

        // Si no está minimizado, lo minimiza de una
        if (!afd.esMinimo()) {
            System.out.println(" Minimizando...");
            afd = afd.minimizar();
            System.out.println(" [OK] AFD minimizado");
        } else {
            System.out.println(" AFD ya es minimo");
        }

        System.out.println("\n Transiciones (formato solicitado):");
        System.out.println(afd.formatearTransiciones());

        // Revisa si cuadra con lo que el test pedía
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

        // Guarda el resultado en un archivo
        String resultadoAFDFile = "resultados/afd/" + nombre + ".afd";
        new File("resultados/afd/").mkdirs();
        try (FileWriter fw = new FileWriter(resultadoAFDFile)) {
            fw.write(afd.formatearTransiciones());
        } catch (IOException e) {
        }
    }

    /**
     * Pasa las cadenas de texto por el AFD para ver si las acepta o rechaza.
     * Revisa contra el archivo .exparsin para ver si le atinaste.
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
        // Pone a prueba cada cadena
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

        // Guarda el reporte de cadenas
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
     * Ejecuta el paquete completo: AFN, AFD y Parsing de un solo, 
     * dándote tu nota al final.
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
     * Te enseña el AFD tal cual salió de la Expresión Regular, sin minimizarlo.
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
     * Te muestra el antes y el después de aplicarle la minimización a un AFD.
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
     * Genera el AFD o lo carga desde archivo, pero NO lo minimiza.
     */
    private static AFD cargarAFDSinMinimizar(String nombre) {
        String erFile = "tests/er/" + nombre + ".er";
        String afdFile = "tests/afd/" + nombre + ".afd";

        try {
            String er = LectorArchivos.leerArchivo(erFile);
            AFN afn = ParserRegex.parse(er);
            return AFD.desdeAFN(afn);
        // Si falla al armarlo, trata de leerlo de un archivo
        } catch (Exception e) {
            String contenido = LectorArchivos.leerArchivo(afdFile);
            return AFD.desdeFormatoArchivo(contenido);
        }
    }

    /**
     * Genera el AFD y se asegura de que sí o sí esté minimizado antes de devolverlo.
     */
    private static AFD cargarGenerarAFD(String nombre) {
        String erFile = "tests/er/" + nombre + ".er";
        String afdFile = "tests/afd/" + nombre + ".afd";

        AFD afd;
        try {
            String er = LectorArchivos.leerArchivo(erFile);
            AFN afn = ParserRegex.parse(er);
            afd = AFD.desdeAFN(afn);
        // Si falla al armarlo, trata de leerlo de un archivo
        } catch (Exception e) {
            String contenido = LectorArchivos.leerArchivo(afdFile);
            afd = AFD.desdeFormatoArchivo(contenido);
        }
        
        // Verifica y minimiza si hace falta
        if (!afd.esMinimo()) {
            afd = afd.minimizar();
        }
        return afd;
    }

    /**
     * Revisa las carpetas de pruebas para ver qué archivos hay disponibles.
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
     * Borra todo lo que esté en las carpetas de resultados para empezar en blanco.
     */
    private static void limpiarResultados() {
        Banner.mostrar("Borrar resultados");
        borrarDir(new File("resultados/afd/"));
        borrarDir(new File("resultados/parsin/"));
        System.out.println("\n Resultados limpiados.");
    }

    /**
     * Vacía una carpeta borrando archivo por archivo.
     */
    private static void borrarDir(File dir) {
        if (dir.listFiles() != null) {
            for (File f : dir.listFiles()) f.delete();
        }
    }
}