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

        while (!salir) {
            Banner.imprimirBanner();

            System.out.println(" 1) Pasar ER a AFD (ER -> AFN -> AFD)");
            System.out.println(" 2) Hacer un AFD minimo");
            System.out.println(" 3) Realizar Parsing (Validar cadena)");
            System.out.println(" 4) Realizar test");
            System.out.println(" 0) Salir");
            System.out.print(" Seleccione una opcion: ");

            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    System.out.println("\n--- PASAR ER A AFD ---");
                    System.out.print(" Direccion del archivo ER: ");
                    String rutaER = scanner.nextLine();

                    String er = LectorArchivos.leerArchivo(rutaER);
                    System.out.println("Expresion regular: " + er);

                    System.out.println("\n Construyendo AFN con Thompson...");
                    AFN afn = ParserRegex.parse(er);
                    afn.imprimirAutomata();

                    System.out.println("\n Convirtiendo AFN a AFD (subconjuntos)...");
                    AFD afd = AFD.desdeAFN(afn);
                    afd.imprimir();

                    System.out.println("\n Transiciones:");
                    System.out.println(afd.formatearTransiciones());
                    System.out.println("ER convertida a AFD exitosamente!");
                    break;

                case 2:
                    System.out.println("\n--- CREAR AFD MINIMO ---");
                    System.out.print(" Direccion del archivo AFD: ");
                    String rutaAFD = scanner.nextLine();

                    String contenidoAFD = LectorArchivos.leerArchivo(rutaAFD);
                    AFD afdOriginal = AFD.desdeFormatoArchivo(contenidoAFD);

                    boolean esMin = afdOriginal.esMinimo();
                    System.out.println("\n El AFD ya es minimo? " + (esMin ? "SI" : "NO"));

                    if (!esMin) {
                        System.out.println("\n Minimizando AFD...");
                        AFD afdMinimo = afdOriginal.minimizar();
                        afdMinimo.imprimir();
                        System.out.println("\n Transiciones del AFD minimo:");
                        System.out.println(afdMinimo.formatearTransiciones());
                    }
                    break;

                case 3:
                    System.out.println("\n--- PARSING ---");
                    System.out.print(" Direccion del archivo AFD: ");
                    String rutaAFD2 = scanner.nextLine();

                    String contenidoAFD2 = LectorArchivos.leerArchivo(rutaAFD2);
                    AFD afdParse = AFD.desdeFormatoArchivo(contenidoAFD2);

                    System.out.print(" Ingresa la cadena a evaluar: ");
                    String cadena = scanner.nextLine();

                    System.out.println("\n Evaluando cadena: '" + cadena + "'...");
                    boolean aceptada = afdParse.validar(cadena);

                    if (aceptada) {
                        System.out.println(">>> Resultado: CADENA ACEPTADA");
                    } else {
                        System.out.println(">>> Resultado: CADENA RECHAZADA");
                    }
                    break;

                case 4:
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
