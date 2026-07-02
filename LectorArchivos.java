import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Esto es una herramienta para leer archivos del disco.
 * Sirve para sacar el texto dependiendo de la extensión, 
 * si es .er solo agarra la primera línea, y si es otra cosa, 
 * te devuelve todo el contenido completo.*/
public class LectorArchivos {

    /**
     * Lee el archivo desde la ruta que le pases. 
     * Si es un archivo .er, saca solo la primera línea y le quita 
     * los espacios de los lados. 
     * Para los demás, te devuelve todo el 
     * texto armadito con sus saltos de línea. Si no encuentra el archivo 
     * o algo falla, tira un error.
     */
    public static String leerArchivo(String ruta) {
        try {
            List<String> lineas = Files.readAllLines(Paths.get(ruta));

            // Saca la extensión para ver qué tipo de archivo es
            String extension = ruta.contains(".")
                    ? ruta.substring(ruta.lastIndexOf('.') + 1).toLowerCase()
                    : "";

            // Si es un .er, nos quedamos solo con la primera línea limpia
            if (extension.equals("er")) {
                return lineas.get(0).trim();
            }

            // Para cualquier otro, juntamos todo con saltos de línea
            return String.join("\n", lineas);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}