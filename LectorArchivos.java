import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class LectorArchivos {

    public static String leerArchivo(String ruta) {
        try {
            List<String> lineas = Files.readAllLines(Paths.get(ruta));
            String[] partes = ruta.split("\\.");
            
            if (partes.length > 1) {
                String extension = partes[partes.length - 1].toLowerCase();
                
                if (extension.equals("txt")) {
                    return procesarTXT(lineas);
                } else if (extension.equals("afd")) {
                    // Une toda la lista en un solo String separado por saltos de línea
                    return String.join("\n", lineas);
                } else {
                    System.out.println(extension + " no soportada. Solo leo .txt o .afd");
                    return null;
                }
            }
            return null;
        } catch (IOException e) {
            System.out.println("[ERROR] No se pudo encontrar o leer el archivo en: " + ruta);
            return null;
        }
    }

    private static String procesarTXT(List<String> lineas) {
        return lineas.get(1).trim();
    }
}