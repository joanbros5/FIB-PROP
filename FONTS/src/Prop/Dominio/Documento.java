/**
 * @file Documento.java
 * @brief Contiene la clase Documento
 */
package Prop.Dominio;
import Prop.Exceptions.NullAttr;
import java.util.*;

/**
 * @author Daniel Ruiz Jiménez
 * Clase que contiene los atributos y métodos relacionados con Documentos.
 */
public class Documento {
    /**
     * Cada Documento cuenta con los siguientes atributos:
     * DocumentKey: Un Pair creado por mí, compuesto del título y del autor del Documento
     * TfIdf: Una clase que cuenta con estructuras de datos y métodos para calcular el TF-IDF. Lo declararemos pero no lo modificaremos en esta clase
     * Contenido: Una ArrayList del contenido del Documento, separado por frases
     */
    private DocumentKey dk;
    private tfidf tfidf;
    private ArrayList<String> Contenido;

    /**
     * Creadora de la clase
     * @param dk La DocumentKey del documento a crear
     * @param Contenido El contenido del Documento a crear
     */
    public Documento(DocumentKey dk, ArrayList<String> Contenido) {
        this.dk = dk;
        this.Contenido = Contenido;
        this.tfidf = new tfidf();
    }
    //Getters de la clase
    public String getTitulo() { return this.dk.getTitulo();}
    public DocumentKey getDK() {return this.dk;}
    public ArrayList<String> getContenido() {return this.Contenido;}
    public String getAutor() {return this.dk.getAutor();}
    public tfidf getTfidf() {return this.tfidf;}
    //setters
    public void setTfidf(tfidf t) {this.tfidf = t;}
    public void setDk(DocumentKey dk) {this.dk = dk;}

    /**
     * Consultora del número de frases de un Documento
     * @return Devuelve el número de frases del contenido de un Documento, es decir, el tamaño del ArrayList contenido
     */
    public Integer getNumFrases() {return Contenido.size();}

    /**
     * Consultora de si un Documento contiene una palabra en la frase i
     * @param i El índice del ArrayList, indicando la frase en la que queremos buscar
     * @param word La palabra que queremos consultar
     * @return Devuelve cierto si la palabra word está en la frase i, falso en caso contrario
     */
    public boolean hasWord(Integer i, String word) {
        return Contenido.get(i).contains(word);
    }

    //Setters
    public void setContenido(ArrayList<String> contenido) {
        Contenido = contenido;
    }

}

