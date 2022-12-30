/**
 * @file DocumentKey.java
 * @brief Contiene la clase DocumentKey
 */
package Prop.Dominio;
import Prop.Exceptions.NullAttr;

import java.lang.*;
/**
 * @author Daniel Ruiz Jiménez
 * Clase que contiene los métodos y atributos de la clase DocumentKey.
 */
public class DocumentKey {
    /**
     * Cada DocumentKey cuenta con los siguientes atributos:
     * Título: título del Documento.
     * Autor: autor del Documento.
     */
    private String Titulo;
    private String Autor;

    /**
     * Creadora de la clase
     * @param titulo El título del documento
     * @param autor El autor del documento
     * @throws Exception en el caso de que uno de los dos parámetros no sea correcto (espacio en blanco o salto de línea), se lanzará la excepción pertinente
     */
    public DocumentKey(String titulo, String autor) throws Exception{
        if(titulo.equals("") || titulo.equals(" ") || titulo.equals("\n")) throw new NullAttr("Introduce un título correcto");
        if(autor.equals("") || autor.equals(" ") || autor.equals("\n")) throw new NullAttr("Introduce un autor correcto");
        this.Titulo = titulo;
        this.Autor = autor;
    }
    //getters
    public String getTitulo() {return Titulo;}
    public String getAutor() { return Autor;}
    //setters
    public void setAutor(String autor) {Autor = autor;}
}