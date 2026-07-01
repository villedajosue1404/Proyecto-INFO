import java.util.Scanner;

// Clase separada solo para el diseño del título con degradado SUAVE y flores
class Banner {
    
    // Colores de inicio y fin (RGB) para el texto
    private static final int[] COLOR_INICIO_RGB = {255, 153, 204}; // Rosa
    private static final int[] COLOR_FIN_RGB = {102, 204, 255};   // Celeste
    
    private static final String[] BANNER = {
        "  ███╗   ███╗███████╗██╗       ██╗   ██╗   ██████╗ █████╗ ███╗   ███╗██╗  ",
        "  ████╗ ████║██╔════╝██║       ╚██╗ ██╔╝  ██╔════╝██╔══██╗████╗ ████║██║  ",
        "  ██╔████╔██║█████╗  ██║        ╚████╔╝   ██║     ███████║██╔████╔██║██║  ",
        "  ██║╚██╔╝██║██╔══╝  ██║         ╚██╔╝    ██║     ██╔══██║██║╚██╔╝██║██║  ",
        "  ██║ ╚═╝ ██║███████╗███████╗     ██║     ╚██████╗██║  ██║██║ ╚═╝ ██║██║  ",
        "  ╚═╝     ╚═╝╚══════╝╚══════╝     ╚═╝      ╚═════╝╚═╝  ╚═╝╚═╝     ╚═╝╚═╝  "
    };

    /**
     * Imprime una línea de texto carácter por carácter RGB suave
     * @param linea L
     * @param colorInicio 
     * @param colorFin 
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
        // Resetear el color al final de cada línea
        System.out.println("\033[0m");
    }

    public static void imprimirBanner() {
        String amarillo = "\033[38;2;255;255;153m";
        String lila = "\033[38;2;204;153;255m";
        String verde = "\033[38;2;153;255;153m";
        String reset = "\033[0m";

        System.out.println();
        
        // Flores de arriba
        System.out.println("  " + lila + "✿" + verde + " ❀ " + amarillo + "❁" + reset + "                                                      " + amarillo + "❁" + verde + " ❀ " + lila + "✿" + reset);

        for (String linea : BANNER) {
            imprimirLineaConDegradadoSuave(linea, COLOR_INICIO_RGB, COLOR_FIN_RGB);
        }
        
        // Flores de abajo
        System.out.println("  " + amarillo + "❁" + verde + " ❀ " + lila + "✿" + reset + "                                                      " + lila + "✿" + verde + " ❀ " + amarillo + "❁" + reset);
                           
        System.out.println();
    }
}