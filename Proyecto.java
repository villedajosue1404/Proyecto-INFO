import java.util.Scanner;
import util.Banner;

/**
 * Clase principal y menú del programa. 
 * Te deja cargar archivos, convertir Expresiones Regulares a AFD, 
 * minimizar autómatas, validar cadenas y correr pruebas.
 */
public class Proyecto {

    /**
     * Ciclo principal que mantiene el menú vivo. 
     * Atrapa la opción que elijas y manda a llamar a la función que toca.
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
                    // Solo guarda la ruta del archivo ER para usarla luego
                    System.out.print("\n Ingresa la direccion del archivo ER: ");
                    archivoER = scanner.nextLine();
                    System.out.println(" Archivo ER guardado en memoria.");
                    break;

                case 2:
                    // Solo guarda la ruta del archivo AFD para usarla luego
                    System.out.print("\n Ingresa la direccion del archivo AFD: ");
                    archivoAFD = scanner.nextLine();
                    System.out.println(" Archivo AFD guardado en memoria.");
                    break;

                case 3:
                    // Lee la ER, arma el AFN de Thompson y lo pasa directo a AFD
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
                    // Carga un AFD, revisa si ya es mínimo y si no, lo achica
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
                    // Carga el AFD y te dice si acepta o batea tu cadena
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
                    // Salta directo a la sección de pruebas (tests)
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