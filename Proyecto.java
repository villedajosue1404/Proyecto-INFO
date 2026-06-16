import java.util.Scanner;

public class Proyecto {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;
        String archivoER = null;
        String archivoAFD = null;

        while (!salir) {
            Banner.imprimirBanner();
            
            System.out.println("🌸 1) Ingresar archivo ER (dirección)");
            System.out.println("🌸 2) Ingresar archivo AFD (dirección)");
            System.out.println("🌸 3) Pasar ER a AFD (ER -> AFN -> AFD)");
            System.out.println("🌸 4) Hacer un AFD mínimo");
            System.out.println("🌸 5) Realizar Parsing (Validar cadena)");
            System.out.println("🎀 6) Realizar test ");
            System.out.println("✨ 0) Salir");
            System.out.print("💕 Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el salto de línea del buffer

            switch (opcion) {
                case 1:
                    System.out.print("\n👉 Ingresa la dirección del archivo ER: ");
                    archivoER = scanner.nextLine();
                    System.out.println("✅ ¡Súper! Archivo ER guardado en memoria 🍬.");
                    break;

                case 2:
                    System.out.print("\n👉 Ingresa la dirección del archivo AFD: ");
                    archivoAFD = scanner.nextLine();
                    System.out.println("✅ ¡Listo! Archivo AFD guardado en memoria 🎀.");
                    break;

                case 3:
                    System.out.println("\n💕 --- PASAR ER A AFD --- 💕");
                    archivoER = verificarArchivo(archivoER, "ER", scanner);
                    System.out.println("\n🛠️🎀 [LÓGICA PENDIENTE] Pasando de ER -> AFN -> AFD usando el archivo: " + archivoER);
                    break;

                case 4:
                    System.out.println("\n💕 --- CREAR AFD MÍNIMO --- 💕");
                    archivoAFD = verificarArchivo(archivoAFD, "AFD", scanner);
                    System.out.println("\n🛠️🎀 [LÓGICA PENDIENTE] Minimizando el AFD usando el archivo: " + archivoAFD);
                    break;

                case 5:
                    System.out.println("\n💕 --- PARSING --- 💕");
                    archivoAFD = verificarArchivo(archivoAFD, "AFD", scanner);
                    
                    System.out.print("👉 Ingresa la cadena a evaluar: ");
                    String cadena = scanner.nextLine();
                    
                    System.out.println("\n🛠️🎀 [LÓGICA PENDIENTE] Evaluando la cadena '" + cadena + "' en el autómata " + archivoAFD);
                    System.out.println("✨ >>> Resultado simulado: ACEPTADA o RECHAZADA");
                    break;
                    
                case 6: 
                    System.out.println("\n🎀 Entrando al área de pruebas... 🎀");
                    TestProyecto.iniciar();
                    break;
                case 0:
                    salir = true;
                    System.out.println("\nSaliendo del programa... ¡Bye bye! 🌸✨");
                    break;
                default:
                    System.out.println("\n Opción no válida. Intenta de nuevo 🥺.");
                    break;
            }
        }
        scanner.close();
    }

    /**
     * Función auxiliar para verificar si hay un archivo cargado y preguntar 
     * al usuario si desea usarlo, sobreescribirlo o ingresar uno nuevo.
     */
    private static String verificarArchivo(String archivoActual, String tipo, Scanner scanner) {
        if (archivoActual != null && !archivoActual.isEmpty()) {
            System.out.print("🤔 ¿Estás segur@ que quieres usar el archivo " + tipo + " que ya está cargado (" + archivoActual + ")? (y/n): ");
            String respuesta = scanner.nextLine().trim().toLowerCase();

            if (respuesta.equals("n")) {
                System.out.print("👉 Ingresa la nueva dirección del archivo " + tipo + " (esto sobreescribirá el anterior): ");
                return scanner.nextLine();
            } else {
                return archivoActual; 
            }
        } else {
            System.out.print("🌸 No hay un archivo " + tipo + " cargado actualmente. Ingresa el nombre o la ruta: ");
            return scanner.nextLine();
        }
    }
}