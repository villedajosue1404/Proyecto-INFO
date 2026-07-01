import java.util.Scanner;
import util.Banner;

/**
 * Clase principal del programa punto de entrada del proyecto
 * Presenta un menu interactivo con opciones para cargar archivos
 * Convertir expresiones regulares a AFD minimizar automatas
 * Validar cadenas con parsing y ejecutar pruebas unitarias
 */
public class Proyecto {

    /**
     * Metodo principal que ejecuta el programa en un ciclo infinito
     * Muestra el banner y el menu de opciones en cada iteracion
     * Permite cargar archivos ER y AFD convertir minimizar validar y probar
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;
        String archivoER = null;
        String archivoAFD = null;

        AFN afnActual = null;
        AFD afdActual = null;

        while (!salir) {
            Banner.imprimirBanner();

            System.out.println(" 1) Ingresar archivo ER (direccion)");
            System.out.println(" 2) Ingresar archivo AFD (direccion)");
            System.out.println(" 3) Pasar ER a AFD (ER -> AFN -> AFD)");
            System.out.println(" 4) Hacer un AFD minimo");
            System.out.println(" 5) Realizar Parsing (Validar cadena)");
            System.out.println(" 6) Realizar test");
            System.out.println(" 0) Salir");
            System.out.print(" Seleccione una opcion: ");

            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    // Guarda la ruta del archivo ER para usarlo despues
                    System.out.print("\n Ingresa la direccion del archivo ER: ");
                    archivoER = scanner.nextLine();
                    System.out.println(" Archivo ER guardado en memoria.");
                    break;

                case 2:
                    // Guarda la ruta del archivo AFD para usarlo despues
                    System.out.print("\n Ingresa la direccion del archivo AFD: ");
                    archivoAFD = scanner.nextLine();
                    System.out.println(" Archivo AFD guardado en memoria.");
                    break;

                case 3:
                    // Lee la expresion regular construye el AFN y lo convierte a AFD
                    System.out.println("\n--- PASAR ER A AFD ---");
                    System.out.print(" Direccion del archivo ER: ");
                    archivoER = scanner.nextLine();

                    String contenidoER = LectorArchivos.leerArchivo(archivoER);
                    System.out.println("Expresion regular: " + contenidoER);

                    System.out.println("\n Construyendo AFN con Thompson...");
                    afnActual = ParserRegex.parse(contenidoER);
                    afnActual.imprimirAutomata();

                    System.out.println("\n Convirtiendo AFN a AFD (subconjuntos)...");
                    afdActual = AFD.desdeAFN(afnActual);
                    afdActual.imprimir();

                    System.out.println("\n Transiciones:");
                    System.out.println(afdActual.formatearTransiciones());
                    System.out.println("ER convertida a AFD exitosamente!");
                    break;

                case 4:
                    // Lee un AFD verifica si es minimo y si no lo minimiza
                    System.out.println("\n--- CREAR AFD MINIMO ---");

                    System.out.print(" Direccion del archivo AFD: ");
                    archivoAFD = scanner.nextLine();
                    String contenidoAFD = LectorArchivos.leerArchivo(archivoAFD);
                    afdActual = AFD.desdeFormatoArchivo(contenidoAFD);

                    boolean esMin = afdActual.esMinimo();
                    System.out.println("\n El AFD ya es minimo? " + (esMin ? "SI" : "NO"));

                    if (!esMin) {
                        System.out.println("\n Minimizando AFD...");
                        AFD afdMinimo = afdActual.minimizar();
                        afdMinimo.imprimir();

                        System.out.println("\n Transiciones del AFD minimo:");
                        System.out.println(afdMinimo.formatearTransiciones());

                        System.out.print("\n Quieres usar el AFD minimo como el actual? (y/n): ");
                        String reemplazar = scanner.nextLine().trim().toLowerCase();
                        if (reemplazar.equals("y")) {
                            afdActual = afdMinimo;
                            System.out.println("AFD minimo guardado en memoria!");
                        }
                    }
                    break;

                case 5:
                    // Lee un AFD y evalua si una cadena ingresada por el usuario es aceptada
                    System.out.println("\n--- PARSING ---");

                    System.out.print(" Direccion del archivo AFD: ");
                    archivoAFD = scanner.nextLine();
                    contenidoAFD = LectorArchivos.leerArchivo(archivoAFD);
                    afdActual = AFD.desdeFormatoArchivo(contenidoAFD);

                    System.out.print(" Ingresa la cadena a evaluar: ");
                    String cadena = scanner.nextLine();

                    System.out.println("\n Evaluando cadena: '" + cadena + "'...");
                    boolean aceptada = afdActual.validar(cadena);

                    if (aceptada) {
                        System.out.println(">>> Resultado: CADENA ACEPTADA");
                    } else {
                        System.out.println(">>> Resultado: CADENA RECHAZADA");
                    }
                    break;

                case 6:
                    // Abre el modulo de pruebas unitarias para ejecutar tests
                    System.out.println("\n Entrando al area de pruebas...");
                    TestProyecto.iniciar(scanner);
                    break;
                case 0:
                    salir = true;
                    System.out.println("\nSaliendo del programa... Bye bye!");
                    break;
            }
        }
        scanner.close();
    }
}
