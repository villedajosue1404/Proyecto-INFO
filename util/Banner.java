package util;

/**
 * Genera el banner decorativo del equipo MEL Y CAMI
 * Utiliza colores en degradado de rosa a azul
 * Muestra el logotipo del proyecto en un marco con adornos
 * Se usa tanto en el menu principal como en los submenus de pruebas
 */
public class Banner {

    private static final int[] COLOR_INICIO_RGB = {255, 153, 204};
    private static final int[] COLOR_FIN_RGB = {102, 204, 255};

    private static final String[] BANNER_ASCII = {
        "  ███╗   ███╗███████╗██╗       ██╗   ██╗   ██████╗ █████╗ ███╗   ███╗██╗  ",
        "  ████╗ ████║██╔════╝██║       ╚██╗ ██╔╝  ██╔════╝██╔══██╗████╗ ████║██║  ",
        "  ██╔████╔██║█████╗  ██║        ╚████╔╝   ██║     ███████║██╔████╔██║██║  ",
        "  ██║╚██╔╝██║██╔══╝  ██║         ╚██╔╝    ██║     ██╔══██║██║╚██╔╝██║██║  ",
        "  ██║ ╚═╝ ██║███████╗███████╗     ██║     ╚██████╗██║  ██║██║ ╚═╝ ██║██║  ",
        "  ╚═╝     ╚═╝╚══════╝╚══════╝     ╚═╝      ╚═════╝╚═╝  ╚═╝╚═╝     ╚═╝╚═╝  "
    };

    /**
     * Imprime una linea de texto con degradado de color
     * Cada caracter tiene un color distinto en el espectro
     * El degradado va del color inicial al color final de forma suave
     * @param linea el texto a imprimir con colores
     * @param colorInicio el color RGB inicial del degradado
     * @param colorFin el color RGB final del degradado
     */
    private static void imprimirLineaConDegradadoSuave(String linea, int[] colorInicio, int[] colorFin) {
        int len = linea.length();
        if (len == 0) return;

        for (int i = 0; i < len; i++) {
            double factor = (double) i / (len - 1);

            int r = (int) (colorInicio[0] + factor * (colorFin[0] - colorInicio[0]));
            int g = (int) (colorInicio[1] + factor * (colorFin[1] - colorInicio[1]));
            int b = (int) (colorInicio[2] + factor * (colorFin[2] - colorInicio[2]));

            System.out.print("\033[38;2;" + r + ";" + g + ";" + b + "m" + linea.charAt(i));
        }
        System.out.println("\033[0m");
    }

    /**
     * Muestra el banner principal sin subtitulo adicional
     * Dibuja un marco con adornos de flor alrededor del logotipo
     * Se utiliza en el menu principal del programa Proyecto
     */
    public static void imprimirBanner() {
        int ancho = anchoMaximo();
        String tapa = "❀" + "━".repeat(ancho + 2) + "❀";
        String piso = "❀" + "━".repeat(ancho + 2) + "❀";

        imprimirLineaConDegradadoSuave(tapa, COLOR_INICIO_RGB, COLOR_FIN_RGB);

        for (String linea : BANNER_ASCII) {
            int diff = ancho - linea.length();
            String dentro = "┃ " + linea + " ".repeat(diff) + " ┃";
            imprimirLineaConDegradadoSuave(dentro, COLOR_INICIO_RGB, COLOR_FIN_RGB);
        }

        imprimirLineaConDegradadoSuave(piso, COLOR_INICIO_RGB, COLOR_FIN_RGB);
        System.out.println();
    }

    /**
     * Muestra el banner con un subtitulo centrado debajo del logotipo
     * El subtitulo se muestra entre virgulillas dentro del marco
     * Ejemplo "~ Solo AFN - simple_a ~"
     * @param subtitulo el texto a mostrar centrado debajo del banner
     */
    public static void mostrar(String subtitulo) {
        int ancho = anchoMaximo();

        String tapa = "❀" + "━".repeat(ancho + 2) + "❀";
        String piso = "❀" + "━".repeat(ancho + 2) + "❀";

        imprimirLineaConDegradadoSuave(tapa, COLOR_INICIO_RGB, COLOR_FIN_RGB);

        for (String linea : BANNER_ASCII) {
            int diff = ancho - linea.length();
            String dentro = "┃ " + linea + " ".repeat(diff) + " ┃";
            imprimirLineaConDegradadoSuave(dentro, COLOR_INICIO_RGB, COLOR_FIN_RGB);
        }

        // Colocamos el subtitulo centrado horizontalmente en el marco
        String sub = "~ " + subtitulo + " ~";
        int esp = (ancho - sub.length()) / 2;
        if (esp < 0) esp = 0;
        String subCentrado = " ".repeat(esp) + sub + " ".repeat(ancho - esp - sub.length());
        String lineaSub = "┃ " + subCentrado + " ┃";
        imprimirLineaConDegradadoSuave(lineaSub, COLOR_INICIO_RGB, COLOR_FIN_RGB);

        imprimirLineaConDegradadoSuave(piso, COLOR_INICIO_RGB, COLOR_FIN_RGB);
        System.out.println();
    }

    /**
     * Calcula el ancho de la linea mas larga del banner ASCII
     * Se usa para determinar el tamano del marco decorativo
     * @return el numero de caracteres de la linea mas larga
     */
    private static int anchoMaximo() {
        int max = 0;
        for (String l : BANNER_ASCII) {
            if (l.length() > max) max = l.length();
        }
        return max;
    }
}
