/**
 * Representa un estado dentro de un automata AFN o AFD
 * Cada estado tiene un identificador unico numerico
 * Implementa Comparable para poder ordenarse en TreeMap y TreeSet
 */
public class Estado implements Comparable<Estado> {
    private int id;

    /**
     * Constructor que asigna el identificador al estado
     * @param id el numero unico que identifica a este estado
     */
    public Estado(int id) {
        this.id = id;
    }

    /**
     * Devuelve el identificador numero de este estado
     * @return el id del estado
     */
    public int getId() {
        return id;
    }

    /**
     * Compara este estado con otro usando su id
     * Permite ordenar los estados de menor a mayor
     * Necesario para usar TreeMap y TreeSet
     * @param otro el estado con el que se compara
     * @return negativo cero o positivo segun el orden
     */
    @Override
    public int compareTo(Estado otro) {
        return Integer.compare(this.id, otro.id);
    }

    /**
     * Devuelve el nombre del estado en formato q mas el id
     * Por ejemplo q0 q1 q2 etc
     * @return la representacion en texto del estado
     */
    @Override
    public String toString() {
        return "q" + id;
    }
}
