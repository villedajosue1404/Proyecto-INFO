import java.util.Scanner;

// Clase para el banner tierno del test (Se mantiene igual de fresa 🎀)
class BannerTest {
    private static final int[] COLOR_ROSA = {255, 153, 204};  
    private static final int[] COLOR_LILA = {204, 153, 255}; 

    private static final String[] BANNER_ASCII = {
        "  ✧･ﾟ: *✧･ﾟ:* MEL Y CAMI TEST  *:･ﾟ✧*:･ﾟ✧",
        " ",
        "                   (\\_/)",
        "                  ( •_•)",
        "                 / > ♡ < \\",
        " ",
        "     ████████ ███████ ███████ ████████ ",
        "        ██    ██      ██         ██    ",
        "        ██    █████   ███████    ██    ",
        "        ██    ██           ██    ██    ",
        "        ██    ███████ ███████    ██    ",
        " ",
        "  ♡ ~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~ ♡ "
    };

    private static void imprimirDegradado(String linea) {
        int longitud = linea.length();
        if (longitud == 0) return;
        
        for (int i = 0; i < longitud; i++) {
            double mezcla = (Math.sin((double) i / 4.0) + 1) / 2.0;
            int r = (int) (COLOR_ROSA[0] + mezcla * (COLOR_LILA[0] - COLOR_ROSA[0]));
            int g = (int) (COLOR_ROSA[1] + mezcla * (COLOR_LILA[1] - COLOR_ROSA[1]));
            int b = (int) (COLOR_ROSA[2] + mezcla * (COLOR_LILA[2] - COLOR_ROSA[2]));
            System.out.print("\033[38;2;" + r + ";" + g + ";" + b + "m" + linea.charAt(i));
        }
        System.out.println("\033[0m");
    }

    public static void mostrar() {
        System.out.println();
        for (String linea : BANNER_ASCII) {
            imprimirDegradado(linea);
        }
        System.out.println();
    }
}

// Clase principal de pruebas con Menú y rutas reales
public class TestProyecto {

    public static void iniciar() {
        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        BannerTest.mostrar();

        do {
            System.out.println("\n      🌸 --- PANEL DE PRUEBAS --- 🌸");
            System.out.println("1) Leer archivo de Expresión Regular (.txt)");
            System.out.println("2) Leer archivo de Autómata Finito (.afd)");
            System.out.println("0) Salir");
            System.out.print("🌸 Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        System.out.println("\n🎀 Has seleccionado: Leer Expresión Regular (.txt)");
                        System.out.print("👉 Por favor, ingresa la ruta de tu archivo: ");
                        String rutaTxt = scanner.nextLine();
                        
                        String resultadoTxt = LectorArchivos.leerArchivo(rutaTxt);
                        if (resultadoTxt != null) {
                            System.out.println("\n✅ ¡Éxito! La Expresión Regular encontrada es:");
                            System.out.println("✨ " + resultadoTxt);
                        }
                        break;

                    case 2:
                        System.out.println("\n🎀 Has seleccionado: Leer Autómata Finito (.afd)");
                        System.out.print("👉 Por favor, ingresa la ruta de tu archivo: ");
                        String rutaAfd = scanner.nextLine();
                        
                        String resultadoAfd = LectorArchivos.leerArchivo(rutaAfd);
                        if (resultadoAfd != null) {
                            System.out.println("\n¡Éxito! Contenido del AFD cargado:");
                            System.out.println("✨\n" + resultadoAfd);
                        }
                        break;

                    case 0:
                        System.out.println("\nSaliendo del panel de pruebas... 🎀✨");
                        break;

                    default:
                        System.out.println("\n Opción no válida. Intenta con un número del menú.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("\n ingresa solo números.");
            }

        } while (opcion != 0);
    }
}