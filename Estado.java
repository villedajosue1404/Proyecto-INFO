/**
 * Esto es un estado para los autómatas AFN o AFD.
 * Cada uno tiene un ID numérico único. 
 * Sirve para que se puedan ordenar de menor a mayor automáticamente 
 * al meterlos en colecciones como TreeSet o TreeMap.*/
public class Estado implements Comparable<Estado> {
    private int id;

    /**
     * Crea el estado y le asigna su número de ID.*/
    public Estado(int id) {
        this.id = id;
    }

    /**
     * Te da el ID del estado.*/
    public int getId() {
        return id;
    }

    /**
     * Compara el ID de este estado con otro.
     * Sirve para que las colecciones ordenadas sepan quién va primero.*/
    @Override
    public int compareTo(Estado otro) {
        return Integer.compare(this.id, otro.id);
    }

    /**
     * Arma el nombre del estado para imprimirlo bonito.
     * Le pone una 'q' antes del número.*/
    @Override
    public String toString() {
        return "q" + id;
    }
}