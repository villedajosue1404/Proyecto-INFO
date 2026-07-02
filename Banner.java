
/**
 * Clase separada solo para darle estilo al título.
 * Imprime letras grandes con un degradado de color suave y 
 * le pone unas flores a los lados para que se vea bien en la consola.*/
class Banner {
    
    // Colores de inicio (Rosa) y fin (Celeste) en formato RGB para el texto
    private static final int[] COLOR_INICIO_RGB = {255, 153, 204};
    private static final int[] COLOR_FIN_RGB = {102, 204, 255};   
    
    private static final String[] BANNER = {
        "  ███╗   ███╗███████╗██╗       ██╗   ██╗   ██████╗ █████╗ ███╗   ███╗██╗  ",
        "  ████╗ ████║██╔════╝██║       ╚██╗ ██╔╝  ██╔════╝██╔══██╗████╗ ████║██║  ",
        "  ██╔████╔██║█████╗  ██║        ╚████╔╝   ██║     ███████║██╔████╔██║██║  ",
        "  ██║╚██╔╝██║██╔══╝  ██║         ╚██╔╝    ██║     ██╔══██║██║╚██╔╝██║██║  ",
        "  ██║ ╚═╝ ██║███████╗███████╗     ██║     ╚██████╗██║  ██║██║ ╚═╝ ██║██║  ",
        "  ╚═╝     ╚═╝╚══════╝╚══════╝     ╚═╝      ╚═════╝╚═╝  ╚═╝╚═╝     ╚═╝╚═╝  "
    };

    /**
     * Imprime una línea de texto pintando letra por letra.
     * Calcula el color exacto entre el inicio y el fin dependiendo de 
     * la posición de la letra para hacer un degradado bien suave.*/
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
        // Le quita el color al final de la línea para no manchar lo demás
        System.out.println("\033[0m");
    }

    /**
     * Dibuja todo el título en la consola.
     * Pone unas flores coloridas arriba, luego el texto con su degradado
     * y remata con otras flores abajo.*/
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