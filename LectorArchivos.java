import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Proporciona metodos estaticos para leer archivos del disco
 * Diferencia entre archivos .er y archivos normales
 * Los archivos .er solo devuelven la primera linea
 * Los demas archivos devuelven todo su contenido
 */
public class LectorArchivos {

    /**
     * Lee el contenido completo de un archivo desde la ruta especificada
     * Si el archivo tiene extension .er solo toma la primera linea sin espacios
     * Para cualquier otro archivo devuelve todo el texto con saltos de linea
     * @param ruta la direccion del archivo a leer
     * @return el texto completo del archivo
     * @throws RuntimeException si ocurre un error de entrada o salida
     */
    public static String leerArchivo(String ruta) {
        try {
            List<String> lineas = Files.readAllLines(Paths.get(ruta));

            // Extrae la extension del archivo para decidir como procesarlo
            String extension = ruta.contains(".")
                    ? ruta.substring(ruta.lastIndexOf('.') + 1).toLowerCase()
                    : "";

            // Si es .ER solo tomamos la primera linea sin espacios
            if (extension.equals("er")) {
                return lineas.get(0).trim();
            }

            // Para los demas archivos devolvemos el texto completo
            return String.join("\n", lineas);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
