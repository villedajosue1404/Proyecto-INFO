package util;

/**
 * Banner de presentacion del panel de pruebas unitarias
 * Muestra el mensaje MEL Y CAMI TEST con arte ASCII decorativo
 * Utiliza un degradado de colores rosa y lila con ondas
 */
public class BannerTest {
    private static final int[] COLOR_ROSA = {255, 153, 204};
    private static final int[] COLOR_LILA = {204, 153, 255};

    private static final String[] BANNER_ASCII = {
        "  *-': *-':* MEL Y CAMI TEST  *:'-* *:'-*",
        " ",
        "                   (\\_/)",
        "                  ( ._.)",
        "                 / > * < \\",
        " ",
        "     ████████ ███████ ███████ ████████ ",
        "        ██    ██      ██         ██    ",
        "        ██    █████   ███████    ██    ",
        "        ██    ██           ██    ██    ",
        "        ██    ███████ ███████    ██    ",
        " ",
        "  * ~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~ * "
    };

    /**
     * Imprime una linea con degradado ondulante entre rosa y lila
     * Usa una funcion seno para crear un efecto de onda en los colores
     * @param linea el texto a imprimir con el degradado de colores
     */
    private static void imprimirDegradado(String linea) {
        int longitud = linea.length();
        if (longitud == 0) {
            return;
        }

        for (int i = 0; i < longitud; i++) {
            double mezcla = (Math.sin((double) i / 4.0) + 1) / 2.0;
            int r = (int) (COLOR_ROSA[0] + mezcla * (COLOR_LILA[0] - COLOR_ROSA[0]));
            int g = (int) (COLOR_ROSA[1] + mezcla * (COLOR_LILA[1] - COLOR_ROSA[1]));
            int b = (int) (COLOR_ROSA[2] + mezcla * (COLOR_LILA[2] - COLOR_ROSA[2]));
            System.out.print("\033[38;2;" + r + ";" + g + ";" + b + "m" + linea.charAt(i));
        }
        System.out.println("\033[0m");
    }

    /**
     * Muestra el banner completo del panel de pruebas
     * Imprime todas las lineas del arte ASCII con degradado de colores
     */
    public static void mostrar() {
        System.out.println();
        for (int i = 0; i < BANNER_ASCII.length; i++) {
            String linea = BANNER_ASCII[i];
            imprimirDegradado(linea);
        }
        System.out.println();
    }
}
