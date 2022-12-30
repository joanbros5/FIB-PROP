/**
 * @file conjDocumentos.java
 * @brief Contiene la clase conjDocumentos
 */
package Prop.Dominio;
import Prop.Exceptions.DocAlreadyExists;
import Prop.Exceptions.DocNotExists;
import Prop.Exceptions.NullAttr;
import com.sun.source.tree.Tree;

import javax.print.Doc;
import javax.swing.text.Document;
import java.lang.reflect.Array;
import java.util.*;

/**
 * @author Daniel Ruiz Jiménez
 * Clase que contiene los atributos y métodos relacionados con el conjunto de Documentos.
 */
public class conjDocumentos {
    /**
     * Cada conjDocumentos cuenta con los siguientes atributos:
     * conjuntoDocumentos: TreeMap que contiene todos los documentos introducidos en el programa. Su key es la DocumentKey de cada documento, y su Value es el contenido de cada documento
     */
    private final TreeMap<DocumentKey, ArrayList<String>> conjuntoDocumentos;

    /**
     * Creadora de la clase
     * conjuntoDocumentos: se crea un nuevo TreeMap ordenado alfabéticamente por Autor, y en caso de que haya dos documentos escritos por el mismo autor, se ordena alfabéticamente por título mediante un Comparator
     */
    public conjDocumentos() {
        this.conjuntoDocumentos = new TreeMap<>(new Comparator<>() {
            @Override
            public int compare(DocumentKey o1, DocumentKey o2) {
                int TituloCompare = o1.getTitulo().compareTo(o2.getTitulo());
                int AutorCompare = o1.getAutor().compareTo(o2.getAutor());

                return (AutorCompare == 0) ? TituloCompare
                        : AutorCompare;
            }
        });
    }

    /**
     * Alta de Documento en el conjunto
     * @param doc El documento que queremos añadir al conjunto
     * @throws Exception En el caso de que la DocumentKey del Documento se halle ya en el conjunto, se lanzará la excepción pertinente
     */
    public void altaDocumento(Documento doc) throws Exception{ //añadir un documento al conjunto
        //si ya existe, da error
        if (conjuntoDocumentos.containsKey(doc.getDK())) {
           throw new DocAlreadyExists("El documento ya existe en el conjunto!");
        }
        else {
            conjuntoDocumentos.put(doc.getDK(),doc.getContenido());
        }
    }

    /**
     * Baja de documento del conjunto
     * @param dk El documento que queremos eliminar
     * @throws Exception En el caso que la DocumentKey del Documento no se halle en el conjunto, se lanzará la excepción pertinente
     */
    public void bajaDocumento(DocumentKey dk) throws Exception{ //quitar un documento del conjunto, buscarlo y eliminarlo
        //si no existe o está vacío, da error
        if (conjuntoDocumentos.containsKey(dk)){
            conjuntoDocumentos.remove(dk); //creo que si no está pues sale un mensaje solo
        }
        else throw new DocNotExists("No existe el documento en el conjunto");
    }
    //getters
    public TreeMap<DocumentKey, ArrayList<String>> getConjuntoDocumentos() {
        return this.conjuntoDocumentos;
    }
    public Documento getDoc(DocumentKey dk) {return new Documento(dk,conjuntoDocumentos.get(dk));}
    public Integer getSize() {
        return conjuntoDocumentos.size();
    }
    public ArrayList<DocumentKey> getListaDKS() {
        ArrayList<DocumentKey> adk = new ArrayList<>();
        for (DocumentKey dk : conjuntoDocumentos.keySet()) {
            adk.add(dk);
        }
        return adk;
    }

    /**
     * Obtener el contenido de un Documento dado su título y autor (DocumentKey)
     * @param dk La DocumentKey cuyo contenido queremos saber
     * @return Devuelve el contenido del documento en cuestión
     * @throws Exception En el caso que el Documento no exista en el conjunto, se lanzará la excepción pertinente
     */
    public ArrayList<String> getContenido(DocumentKey dk) throws Exception {
        //Buscar elemento
        if(!conjuntoDocumentos.containsKey(dk)) throw new DocNotExists("El documento no existe");
        else return new ArrayList<>(conjuntoDocumentos.get(dk)); //obtener value dada key
    }

    /**
     * Modificación de título de un Documento
     * @param dk La nueva DocumentKey con el título nuevo (a introducir en el sistema)
     * @param oldDK La vieja DocumentKey (a eliminar del sistema)
     * @throws Exception En el caso de que la antigua DocumentKey no exista en el sistema, se lanzará la excepción pertinente
     */
    public void modTitulo(DocumentKey dk, DocumentKey oldDK) throws Exception{
        if (!conjuntoDocumentos.containsKey(oldDK)) throw new DocNotExists("El documento no existe en el conjunto");
        Documento oldD = getDoc(oldDK);
        Documento d = new Documento(dk,oldD.getContenido());
        bajaDocumento(oldDK);
        altaDocumento(d);
    }

    /**
     * Modificación de autor de un Documento
     * @param dk La nueva DocumentKey con el autor nuevo (a introducir en el sistema)
     * @param oldDK La vieja DocumentKey (a eliminar del sistema)
     * @throws Exception En el caso de que la antigua DocumentKey no exista en el sistema, se lanzará la excepción pertinente
     */
    public void modAutor(DocumentKey dk, DocumentKey oldDK) throws Exception{
        if (!conjuntoDocumentos.containsKey(oldDK)) throw new DocNotExists("El documento no existe en el conjunto");
        Documento oldD = getDoc(oldDK);
        Documento d = new Documento(dk,oldD.getContenido());
        bajaDocumento(oldDK);
        altaDocumento(d);
    }

    /**
     * Modificación de contenido de un Documento
     * @param dk La DocumentKey del documento a modificar
     * @param nuevoCont El nuevo contenido a introducir en el documento
     * @throws Exception En el caso que la DocumentKey no exista, se lanzará la excepción pertinente
     */
    public void modContenido(DocumentKey dk, ArrayList<String> nuevoCont) throws Exception{
        if (!conjuntoDocumentos.containsKey(dk)) throw new DocNotExists("El documento no existe en el conjunto");
        Documento d = getDoc(dk);
        d.setContenido(nuevoCont);
        bajaDocumento(dk);
        altaDocumento(d);
    }

    /**
     * Lista de títulos de un autor
     * @param autor El autor cuyos títulos queremos conocer
     * @return Una lista de los títulos de ese autor. Puede ser vacía
     * @throws Exception En el caso que el conjunto esté vacío, se lanzará la excepción pertinente
     */
    public ArrayList<String> listatitulos(String autor) throws Exception{
        if(conjuntoDocumentos.isEmpty()) throw new NullAttr("El conjunto está vacío");
        ArrayList<String> listaTitulos = new ArrayList<>();
        for(DocumentKey dk : conjuntoDocumentos.keySet()) {
            if (dk.getAutor().equals(autor))  listaTitulos.add(dk.getTitulo());
        }
        return listaTitulos;
    }
}


