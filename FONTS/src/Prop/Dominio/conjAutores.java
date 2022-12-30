/**
 * @file conjAutores.java
 * @brief Contiene la clase conjAutores
 */

package Prop.Dominio;

import java.util.*;

/**
 * @author Joan Sales de Marcos
 * Clase que contiene los atributos y métodos relacionados con conjAutores.
 */
public class conjAutores {

    /**
     * Cada conjAutores cuenta con los siguientes atributos:
     * children: Un Treemap con todos los carácteres siguientes
     * autorNodo: Un Autor que se usa para obtener las palabras completas en ese nodo
     * isLeaf: Un booleano que indica si este nodo es una palabra completa que existe
     */
    private final TreeMap<Character, conjAutores> children = new TreeMap<>();
    private String autorNodo;
    private boolean isleaf;

    /**
     * Función recursiva auxiliar para llistaAutoresPrefix
     * @param c Nodo desde donde se hace la búsqueda de palabras completas
     * @return devuelve un ArrayList de c (si es hoja) y los nodos hijos de c que sean hojas
     */
    private ArrayList<String> listaHijosHoja(conjAutores c) {

        ArrayList<String> result = new ArrayList<>();

        //Si es una hoja añadir el autor al resultado
        if (c.getIsleaf()) {
            result.add(c.getAutorNodo());
        }

        //Inspeccionar todos los hijos recursivamente
        if (!c.getChildren().isEmpty()) {

            for (Map.Entry<Character, conjAutores> cha : c.getChildren().entrySet()) {
                result.addAll(listaHijosHoja(c.getChildren().get(cha.getKey())));
            }
        }

        return result;
    }

    /**
     * Creadora vacía de la clase
     */
    public conjAutores() {
        isleaf = false;
        autorNodo = "";
    }

    //Getters de la clase
    public boolean getIsleaf() {
        return this.isleaf;
    }
    public String getAutorNodo() {
        return this.autorNodo;
    }
    public TreeMap<Character, conjAutores> getChildren() {
        return this.children;
    }

    //Setters
    public void setIsleaf(boolean b) {
        this.isleaf = b;
    }
    public void setAutorNodo(String a) {
        this.autorNodo = a;
    }

    //Funcionalidades:

    /**
     * Devuelve un autor del conjunto a partir de un nombre
     * @param s String del nombre del Autor que se quiere buscar en el conjunto
     * @return Devuelve el Autor con nombre "s" del conjunto, devuelve un Autor vacío en caso de no existir
     */
    public String getAutorEnConjunto(String s){

        conjAutores curr = this;

        //Recorrer hasta llegar al final
        for (Character c : s.toCharArray()) {

            if (!curr.getChildren().containsKey(c)) {
                return "";
            }

            //Avança al següent fill
            curr = curr.getChildren().get(c);
        }

        //Quan s'acaba la paraula, devolver el Autor
        return curr.getAutorNodo();
    }

    /**
     * Inserta un autor en el Trie, no pasa nada si ya existe
     * @param a Representa el autor a insertar
     */
    public void insert(String a) {
        conjAutores curr = this;

        for (Character c : a.toCharArray()) {

            //DEBUG
            //System.out.println("Inserto letra " + c);

            //No existeix el fill que continui amb aquesta lletra, crear-lo
            if (!curr.getChildren().containsKey(c)) {
                curr.getChildren().put(c, new conjAutores());
            }

            //Avança al següent fill
            curr = curr.getChildren().get(c);
        }

        //Quan s'acaba la paraula, marcar que és una paraula que existeix i afegir l'autor
        curr.setIsleaf(true);
        curr.setAutorNodo(a);
    }

    /**
     * Eliminar un Autor del Trie
     * @param a Representa el Autor a borrar del conjunto
     * @return Devuelve true en caso de haberse borrado el Autor y False en caso contrario.
     */
    public boolean delete(String a) {

        int checkpoint = 0, i = 0;
        conjAutores curr = this;
        conjAutores curraux = curr;


        //Si no está el autor no se puede borrar
        if (!curr.existsAutor(a)) return false;

        //Avanzar puntero hasta el final
        for (Character c : a.toCharArray()) {

            /*  DEBUG
            System.out.println("Letra: " + c);
            System.out.println("i: " + i + " checkpoint: " + checkpoint);
            System.out.println("Hijos: " + curraux.getChildren().size() + " " + curraux.getChildren().keySet());
            */

            //Si encuentra una bifurcación o un isleaf intermedio avanzar checkpoint
            if ((curraux.getChildren().size() > 1) || (curraux.getIsleaf() && i < a.length())) {

                curr = curraux;
                checkpoint = i;
            }

            curraux = curraux.getChildren().get(c);
            ++i;
        }

        //Si el autor es una subpalabra no se puede borrar, simplemente se marca como isleaf falso
        if (!curraux.getChildren().isEmpty()) {
            curraux.setIsleaf(false);
            curraux.setAutorNodo(new String());
        }
        //Si no es una subpalabra entonces se borra desde el checkpoint hasta ahí
        else curr.getChildren().remove(a.toCharArray()[checkpoint]);

        return true;
    }



    //Consultoras

    /**
     * Consulta de si existe un Autor en el conjunto
     * @param a Representa el autor a buscar en el conjunto
     * @return Devuelve True en caso de que exista el Autor "s" en el conjunto y False en caso contrario
     */
    public boolean existsAutor(String a){
        conjAutores curr = this;

        //Recorrer hijos hasta llegar a uno vacío o una hoja
        for (Character c : a.toCharArray()) {

            //No existeix el fill que continui amb aquesta lletra
            if (!curr.getChildren().containsKey(c)) {
                return false;
            }

            //Avança al següent fill
            curr = curr.getChildren().get(c);
        }

        //Quan s'acaba la paraula, retornar si existeix
        return curr.getIsleaf();
    }

    /**
     * Consulta de Autores por prefijo
     * @param s Representa el prefijo de los Autores a buscar
     * @return Devuelve una lista de todos los Autores del conjunto cuyo nombre empieza por s
     */
    public ArrayList<String> llistaAutorsPrefix(String s) {

        conjAutores curr = this;

        //Recorrer hasta nodo s
        for (Character c : s.toCharArray()) {
            if (!curr.getChildren().containsKey(c)) {
                return new ArrayList<>();
            }

            curr = curr.getChildren().get(c);
        }

        //Devolver la lista de él y todos sus hijos que sean palabras válidas
        return curr.listaHijosHoja(curr);
    }
}