/**
 * @file ControladorDominio.java
 * @brief Contiene el Controlador de Dominio
 */

package Prop.Dominio;

import Prop.Exceptions.DocNotExists;
import Prop.Exceptions.NullAttr;
import Prop.Exceptions.BadParameter;
import Prop.Dominio.Busqueda.ConsultaBooleana;
import Prop.Exceptions.ParamOutOfRange;
import Prop.Persistencia.ControladorData;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Arnau Moran Riera
 * Clase que contiene los atributos y métodos relacionados con las clases de la capa de Dominio.
 */

public class ControladorDominio {

    /**
     * El controlador, que es singleton, cuenta con los siguientes atributos:
     * CtrlD: Instancia del propio controlador de dominio
     * ca: Objeto de la clase conjAutores que contiene todos los autores del sistema
     * ct: Objeto de la clase conjTfidf que contiene todos los tfidf del sistema
     * cdocs: Objeto de la clase conjDocumentos que contiene todos los documentos del sistema
     * cbool: ArrayList para almacenar todas las expresiones booleanas
     */
    private static ControladorDominio CtrlD;
    private conjAutores ca = new conjAutores();
    private conjTfidf ct = new conjTfidf();
    private conjDocumentos cdocs = new conjDocumentos();
    private ArrayList<ConsultaBooleana> cbool = new ArrayList<>();
    public boolean first;

    public static ControladorDominio getInstance() {
        if (CtrlD == null) {
            CtrlD = new ControladorDominio();
        }
        return CtrlD;
    }

    //setters
    public void setConjAutores(conjAutores nca) {
        this.ca = nca;
    }
    public void setConjDocumentos(conjDocumentos ncd) {
        this.cdocs = ncd;
    }
    public void setConjTfidf(conjTfidf nct) {
        this.ct = nct;
    }
    public void setConjExpresiones(ArrayList<ConsultaBooleana> nce) {
        this.cbool = nce;
    }

    //getters
    public conjAutores getConjAutores() {
        if (this.ca == null) {
            this.ca = new conjAutores();
        }
        return this.ca;
    }
    public conjTfidf getConjTfidf() {
        if (this.ct == null) {
            this.ct = new conjTfidf();
        }
        return this.ct;
    }
    public conjDocumentos getConjDocs() {
        if (this.cdocs == null) {
            this.cdocs = new conjDocumentos();
        }
        return this.cdocs;
    }
    public ArrayList<ConsultaBooleana> getConjExpr() {
        if (this.cbool == null) {
            this.cbool = new ArrayList<>();
        }
        return this.cbool;
    }

    /**
     * Creadora de la clase
     * Se crea la instancia del controlador y se inicializan los atributos.
     */
    private ControladorDominio()
    {
        this.initControlador();
    }

    /**
     * Inicializar el controlador de dominio
     * Los conjuntos de objetos se crean vacíos
     */
    public void initControlador()
    {
        this.ca = new conjAutores();
        this.ct = new conjTfidf();
        this.cdocs = new conjDocumentos();
        this.cbool = new ArrayList<>();
    }

    /**
     * Alta de Documento en el conjunto
     * @param dk La DocumentKey del documento que queremos introducir en el conjunto.
     * @param contenidoFrase Contenido del Documento para la clase conjDocumentos
     * @param contenidoWord Contenido del Documento para la clase conjTfidf
     * @param i Idioma del documento
     * @throws Exception En el caso de que la DocumentKey del Documento ya exista en el conjunto, se lanzará la excepción pertinente.
     */
    public void altaDocumento (DocumentKey dk, ArrayList<String> contenidoFrase, ArrayList<String> contenidoWord, Integer i) throws Exception
    {
        if (i==1 || i==2 || i==3)
        {
            Documento d = new Documento(dk, new ArrayList<>());
            d.getTfidf().setContenido(contenidoWord);
            d.getTfidf().preproceso_texto(i); //calcula tf

            d.setContenido(contenidoFrase);
            cdocs.altaDocumento(d);
            ct.altaDoc(dk, d.getTfidf());
            ca.insert(dk.getAutor());

            System.out.println("Documento creado correctamente");
            System.out.println(d.getTitulo() + " " + d.getAutor());
            System.out.println(d.getContenido());
        } else {
            throw new ParamOutOfRange("Idioma no válido");
        }
    }

    /**
     * Baja de Documento del conjunto
     * @param dk La DocumentKey del documento que queremos eliminar
     * @throws Exception En el caso de que la DocumentKey no exista en el conjunto, se lanzará la excepción pertinente.
     */
    public void bajaDocumento (DocumentKey dk) throws Exception
    {
        if(!cdocs.getConjuntoDocumentos().containsKey(dk)) {
            throw new DocNotExists("El documento no existe en el conjunto.");
        }
        else {
            cdocs.bajaDocumento(dk);
            ct.bajaDoc(dk);
            String borrado = dk.getAutor();
            if (cdocs.listatitulos(borrado).isEmpty()) ca.delete(borrado);
        }
    }

    /**
     * Listar los documentos del conjunto
     * @return Devuelve una lista de las DocumentKey de los documentos del conjunto
     * @throws Exception En el caso de que el conjunto esté vacío, se lanzará la excepción pertinente.
     */
    public ArrayList<DocumentKey> listarDocumentos() throws Exception {
        ArrayList<DocumentKey> listaDocs;
        if (cdocs.getConjuntoDocumentos().isEmpty()) {
            return null;
        } else {
            listaDocs = cdocs.getListaDKS();
        }
        return listaDocs;
    }

    /**
     * Modificar el título de un documento
     * @param dk La nueva DocumentKey del documento del que queremos modificar el título
     * @param oldDK La DocumentKey del documento del que queremos modificar el título
     * @throws Exception En el caso de que el conjunto esté vacío, se lanzará la excepción pertinente.
     * @throws Exception En el caso de que la oldDK DocumentKey no exista en el conjunto, se lanzará la excepción pertinente.
     * @throws Exception En el caso de que la nueva DocumentKey del Documento ya exista en el conjunto, se lanzará la excepción pertinente.
     */
    public void modTitulo(DocumentKey dk, DocumentKey oldDK) throws Exception{
        if(cdocs.getConjuntoDocumentos().isEmpty()) {
            throw new NullAttr("El conjunto está vacío");
        }
        cdocs.modTitulo(dk,oldDK);
        ct.bajaDoc(oldDK);
        ct.altaDoc(dk, cdocs.getDoc(dk).getTfidf());
        System.out.println("Documento modificado correctamente");
        System.out.println(dk.getTitulo() + " " + dk.getAutor());
        System.out.println(cdocs.getDoc(dk).getContenido());
    }

    /**
     * Modificar el Autor de un documento
     * @param dk La nueva DocumentKey del documento del que queremos modificar el autor
     * @param oldDK La DocumentKey del documento del que queremos modificar el autor
     * @throws Exception En el caso de que el conjunto esté vacío, se lanzará la excepción pertinente.
     * @throws Exception En el caso de que la oldDK DocumentKey no exista en el conjunto, se lanzará la excepción pertinente.
     * @throws Exception En el caso de que la nueva DocumentKey del Documento ya exista en el conjunto, se lanzará la excepción pertinente.
     */
    public void modAutor(DocumentKey dk, DocumentKey oldDK) throws Exception{
        if(cdocs.getConjuntoDocumentos().isEmpty()) {
            throw new NullAttr("El conjunto está vacío");
        }
        cdocs.modAutor(dk,oldDK);
        ct.bajaDoc(oldDK);
        ct.altaDoc(dk, cdocs.getDoc(dk).getTfidf());
        ca.insert(dk.getAutor());
        if (cdocs.listatitulos(oldDK.getAutor()).isEmpty()) ca.delete(oldDK.getAutor());
        System.out.println("Documento modificado correctamente");
        System.out.println(dk.getTitulo() + " " + dk.getAutor());
        System.out.println(cdocs.getDoc(dk).getContenido());
    }

    /**
     * Modificar el contenido de un documento
     * @param dk La DocumentKey del documento del cual queremos modificar el contenido
     * @param contenidoFrase Nuevo contenido del Documento para la clase conjDocumentos
     * @param contenidoWord Nuevo contenido del Documento para la clase conjTfidf
     * @param i Idioma del document
     * @throws Exception En el caso de que el conjunto esté vacío, se lanzará la excepción pertinente.
     * @throws Exception En el caso de que la DocumentKey no exista en el conjunto, se lanzará la excepción pertinente.
     */
    public void modContenido(DocumentKey dk, ArrayList<String> contenidoWord, ArrayList<String> contenidoFrase, Integer i) throws Exception{
        //añadir a conjunto tfidf
        //añadirle su tfidf del propio documento
        if(cdocs.getConjuntoDocumentos().isEmpty()) {
            throw new NullAttr("El conjunto está vacío");
        }
        cdocs.modContenido(dk,contenidoFrase);
        Documento d = cdocs.getDoc(dk);
        ct.bajaDoc(dk);
        d.getTfidf().setContenido(contenidoWord);
        d.getTfidf().preproceso_texto(i); //calcula tf

        ct.altaDoc(dk, d.getTfidf());
        System.out.println("Documento modificado correctamente");
        System.out.println(dk.getTitulo() + " " + dk.getAutor());
        System.out.println(cdocs.getDoc(dk).getContenido());
    }

    /**
     * Lista de títulos de un autor
     * @param autor El autor cuyos títulos queremos conocer.
     * @return Devuelve una lista de las DocumentKey de los documentos de dicho autor
     * @throws Exception En el caso de que el autor introducido no exista en el conjunto.
     */
    public ArrayList<String> listarTitulos (String autor) throws Exception
    {
        if(autor.isEmpty()) throw new NullAttr("Introduce un autor correcto");
        else return cdocs.listatitulos(autor);
    }

    /**
     * Cargar un documento en el conjunto decumentos desde un directorio deseado
     * @param path Directorio del documento que se quiere importar
     * @throws Exception Al añadir el documento al conjunto se comprueban las excepciones pertinentes
     */
    public void importarDocumento (String path) throws Exception
    {
        ControladorData.cargarDocumento(path);
    }

    /**
     * Guardar un documento del conjunto en un directorio deseado
     * @param path Directorio en el que se desea exportar el documento
     * @param dk DocumentKey del documento que se quiere exportar
     */
    public void exportarDocumento (String path, DocumentKey dk, int format)
    {
        Documento d = cdocs.getDoc(dk);
        ControladorData.guardarDocumento(path, d, format);
    }

    /**
     * Cargar un estado previo del controlador de dominio
     */
    public void cargarEstado () throws Exception
    {
        ControladorData.cargarEstado("log");
    }

    /**
     * Guardar el estado actual del controlador de dominio
     */
    public void guardarEstado ()
    {
        ControladorData.guardarEstado("log");
    }

    /* EXTRA */
    /**
     * Duplicar un documento añadiendo "(copia)" en el nombre del nuevo documento
     * @param dk La DocumentKey del documento que queremos duplicar
     * @throws Exception En el caso de que la DocumentKey no exista en el conjunto, se lanzará la excepción pertinente.
     * @throws Exception En el caso de que la DocumentKey del nuevo documento ya exista en el conjunto, se lanzará la excepción pertinente.
     */
    public void duplicarDocumento (DocumentKey dk) throws Exception
    {
        if(!cdocs.getConjuntoDocumentos().containsKey(dk)) {
            throw new DocNotExists("El documento no existe en el conjunto.");
        }
        Documento d = new Documento(new DocumentKey(dk.getTitulo()+" (copia)",dk.getAutor()),cdocs.getConjuntoDocumentos().get(dk));
        ct.altaDoc(d.getDK(),ct.getValue(dk));
        cdocs.altaDocumento(d);
    }

    /**
     * Duplicar un documento cambiando el autor
     * @param dk La DocumentKey del documento que queremos duplicar
     * @param autor El nuevo autor del documento que queremos duplicar
     * @throws Exception En el caso de que la DocumentKey no exista en el conjunto, se lanzará la excepción pertinente.
     * @throws Exception En el caso de que la DocumentKey del nuevo documento ya exista en el conjunto, se lanzará la excepción pertinente.
     */
    public void josearDocumento (DocumentKey dk, String autor) throws Exception
    {
        if(!cdocs.getConjuntoDocumentos().containsKey(dk)) {
            throw new DocNotExists("El documento no existe en el conjunto.");
        }
        Documento d = new Documento(new DocumentKey(dk.getTitulo(),autor),cdocs.getConjuntoDocumentos().get(dk));
        ct.altaDoc(d.getDK(),ct.getValue(dk));
        ct.bajaDoc(dk);
        cdocs.altaDocumento(d);
        cdocs.bajaDocumento(dk);
        ca.insert(autor);
        if (cdocs.listatitulos(dk.getAutor()).isEmpty()) ca.delete(dk.getAutor());
    }

    /**
     * Crear una nueva expresión booleana
     * @param expr Expresión que se quiere añadir
     */
    public void crearExpresion (String expr) throws Exception
    {
        cbool.add(new ConsultaBooleana(expr));
        System.out.println("Expresión creada con éxito");
    }

    /**
     * Eliminar una expresión booleana
     * @param id Identificador de la expresión que se quiere eliminar
     * @throws Exception El id introducido no identifica ninguna expresión
     */
    public void eliminarExpresion (int id) throws Exception
    {
        if (id >= 0 && id < cbool.size()) {
            cbool.remove(id);
        } else {
            throw new ParamOutOfRange("Identificador inválido");
        }
    }

    /**
     * Modificar una expresión booleana
     * @param expr Nueva expresión
     * @param id Identificador de la expresión que se quiere modificar
     * @throws Exception El id introducido no identifica ninguna expresión
     */
    public void modificarExpresion (String expr, int id) throws Exception
    {
        if (id >= 0 && id < cbool.size()) {
            cbool.remove(id);
            this.crearExpresion(expr);
        } else {
            throw new ParamOutOfRange("Identificador inválido");
        }
    }

    //CONSULTAS

    /**
     * Consulta el contenido de un documento
     * @param dk La DocumentKey del documento del cual queremos consultar el contenido
     * @return Devuelve el atributo Contenido de dicho documento
     * @throws Exception En el caso de que la DocumentKey no exista en el conjunto, se lanzará la excepción pertinente.
     */
    public ArrayList<String> Contenido(DocumentKey dk) throws Exception
    {
        if(!cdocs.getConjuntoDocumentos().containsKey(dk)) {
            throw new DocNotExists("El documento no existe en el conjunto.");
        }
        else return cdocs.getContenido(dk);
    }

    /**
     * Lista todos los autores que empiecen por un prefijo dado
     * Si el prefijo es vacío se listan todos los autores
     * @param pre Prefijo para determinar los autores a listar
     * @return Devuelve una lista de todos los Autores del conjunto cuyo nombre empieza por el prefijo
     */
    public ArrayList<String> listarAutores(String pre)
    {
        return ca.llistaAutorsPrefix(pre);
    }

    /**
     * Devuelve una expresión booleana
     * @param id Identificador de la expresión que se quiere modificar
     * @throws Exception El id introducido no identifica ninguna expresión
     */
    public String getExpresion (int id) throws Exception
    {
        if (id < 0 || id >= cbool.size()) {
            throw new ParamOutOfRange("Identificador inválido");
        }
        return cbool.get(id).getStrExpresion();
    }

    /**
     * Obtener los k documentos más similares al documento dado
     * @param dk El documento que queremos comparar con el resto.
     * @param k El número de documentos similares que queremos ver.
     * @throws Exception En el caso de que la DocumentKey no se encuentre en el conjunto
     * @throws Exception En el caso de que el valor k sea mayor que el tamaño del conjunto
     */
    public TreeMap<Double,DocumentKey> consultakDocumentos (DocumentKey dk, int k) throws Exception
    {
        if(!cdocs.getConjuntoDocumentos().containsKey(dk)) {
            throw new DocNotExists("El documento no existe en el conjunto.");
        }
        else {
            if (k >= cdocs.getConjuntoDocumentos().size())
                throw new BadParameter("No se puede obtener este número documentos dado el tamaño del conjunto");
            else return ct.getkDocs(dk, k);
        }
    }

    /**
     * Obtener el resultado de hacer una consulta booleana sobre los documentos
     * @param id Identificador de la expresión que se quiere usar en la consulta
     * @return Devuelve una lista de las DocumentKey de los documentos que cumplen la expresión de la consulta
     * @throws Exception El id introducido no identifica ninguna expresión
     */
    public ArrayList<DocumentKey> resultadoExpresion(int id) throws Exception
    {
        if (id < 0 || id >= cbool.size()) {
            throw new ParamOutOfRange("Identificador inválido");
        }
        return cbool.get(id).getCumpleCondicionFrase(cdocs);
    }

    /**
     * Obtener el resultado de hacer una consulta booleana sobre los documentos
     * @param strExp expresion booleana en formato String
     * @return Devuelve una lista de las DocumentKey de los documentos que cumplen la expresión de la consulta
     */
    public ArrayList<DocumentKey> resultadoExpresion(String strExp) throws Exception
    {
        ConsultaBooleana cb;
        try {
            cb = new ConsultaBooleana(strExp);
            cbool.add(cb);
            return cb.getCumpleCondicionFrase(cdocs);

        }
        catch (Exception var22) {
            Logger.getLogger("new ConsultaBooleana").log(Level.SEVERE, null, var22);
        }
        return null;
    }

    /**
     * Devuelve una lista de todas las expresiones booleanas en formato string
     * @return ArrayList de String
     */
    public ArrayList<String> listarExpresiones()
    {
        ArrayList<String> lista = new ArrayList<>();
        for (ConsultaBooleana cbool:
             cbool) {
            lista.add(cbool.getStrExpresion());
        }
        return lista;
    }
}



