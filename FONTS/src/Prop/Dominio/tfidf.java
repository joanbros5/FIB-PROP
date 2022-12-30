/**
 * @file tfidf.java
 * @brief Contiene la clase tfidf
 */
package Prop.Dominio;
import Prop.Exceptions.NullAttr;

import java.nio.file.*;
import java.util.*;
import java.io.*;

/**
 * @author Daniel Ruiz Jiménez
 * Clase que contiene los métodos y atributos de la clase tfidf.
 */
public class tfidf {
    /**
     * Cada tfidf cuenta con los siguientes atributos:
     * Contenido: Cuenta con el contenido del Documento
     * Stopw: Lista que cuenta con las stopwords en el idioma que se seleccione (catalán , castellano, inglés)
     * Frec: HashMap que contiene todas las palabras del documento, y con las ocurrencias en éste
     * Tf: TreeMap que contiene cada palabra del documento y su valor TF (term frequency)
     * Idf: TreeMap que contiene cada palabra del Documento con su valor IDF (inverse document frequency). Lo declararemos pero no lo modificaremos en esta clase
     * Completo: Treemap que contiene todas las palabras de todos los documentos, junto con el número de documentos en los que aparece esta palabra
     */
    private ArrayList<String> contenido;
    private final ArrayList<String> stopw;
    private final HashMap<String,Double> frec;
    private TreeMap<String,Double> tf; //clave son cada palabra del contenido, valor es su tf
    private TreeMap<String, Double> idf;
    private TreeMap<String, Double> completo;
    final int CASTELLANO = 2;
    final int INGLES = 1;

    /**
     * Creadora de la clase
     * tf: Nuevo TreeMap que ordena alfabéticamente las keys mediante un Comparator
     * idf: Nuevo TreeMap que ordena alfabéticamente las keys mediante un Comparator
     * Contenido: declaramos una ArrayList vacía, ya que la cogeremos de la clase Documento
     * Stopw: declaramos una ArrayList vacía, ya que lo llenaremos en una de las funciones de esta clase
     * frec: declaramos un HasMap vacío, ya que lo llenaremos en una de las funciones de esta clase
     * completo: Nuevo TreeMap que ordena alfabéticamente las keys mediante un Comparator
     * CASTELLANO, INGLES: Constantes para evitar "magic numbers" en el preproceso del texto (feedback)
     */
    public tfidf() {
        tf = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        idf = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        contenido = new ArrayList<>();
        stopw = new ArrayList<>();
        frec = new HashMap<>();
        completo = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
    }

    //tf-idf de un termino = tf de ese termino en el documento*idf
    //tf = ocurrencias de la palabra/palabras totales del vector(size de tf)

    //getters
    public TreeMap<String, Double> getTf() {return this.tf;}
    public TreeMap<String, Double> getIdf() {return idf;}
    public TreeMap<String, Double> getCompleto() {return completo;}
    //setters
    public void setTf(TreeMap<String,Double> tf) {this.tf = tf;}
    public void setIdf(TreeMap<String,Double> idf) {this.idf = idf;}
    public void setCompleto(TreeMap<String,Double> completo) {this.completo = completo;}
    public void setContenido(ArrayList<String> contenido) {this.contenido = contenido;}

    /**
     * Función que realiza el preproceso del texto para calcular los valores TF-IDF del documento
     * Se realiza el preproceso del texto en etapas:
     * 1. Ya que hemos separado el contenido por palabras, lo primero que hacemos es pasarlas todas a minúsculas, para evitar palabras diferentes(CaseSensitive)
     * 2. Retiramos las stopwords: Dependiendo del idioma en el que esté el texto, se le retiran aquellas palabras más probables de aparecer en el texto para evitar
     * que los documentos sean demasiado similares, ya que estas palabras frecuentes afectarán al resultado
     * 3. Añadimos a frec la frecuencia de cada palabra diferente en el contenido del documento
     * 4. Calculamos el valor TF de cada palabra del documento, dividiendo la frecuencia de la palabra entre el número de palabras diferentes totales del documento
     * @throws Exception en el caso que el contenido del documento esté vacío, se lanzará la excepción pertinente
     */
    public void preproceso_texto(Integer i) throws Exception{
        this.contenido.replaceAll(String::toLowerCase);
        //quitar stopwords con fileReader de los archivos de la web
            //nos interesa comparar ficheros en el mismo idioma
            if (i == INGLES) { //ingles
                if (this.contenido.isEmpty()) throw new NullAttr("El archivo está vacío");
                else {
                    String appWorkingDir = System.getProperty("user.dir");
                    String stopWordsFileRelativePath = "/FONTS/src/Prop/Dominio/Ficherostop/empty-eng.txt";
                    String fileSeparator = FileSystems.getDefault().getSeparator(); // esta línea sirve para que el código funcione tanto en linux/mac como en Windows
                    File f = new File(appWorkingDir + fileSeparator + stopWordsFileRelativePath);
                    BufferedReader buf  = new BufferedReader(new FileReader(f));
                    String line;
                    while ((line = buf.readLine()) != null) {
                        stopw.add(line);
                    }
                    buf.close();
                }

            } else if (i == CASTELLANO) { //español
                if (this.contenido.isEmpty()) throw new NullAttr("El archivo está vacío");
                else {
                    String appWorkingDir = System.getProperty("user.dir");
                    String stopWordsFileRelativePath = "/FONTS/src/Prop/Dominio/Ficherostop/empty-sp.txt";
                    String fileSeparator = FileSystems.getDefault().getSeparator(); // esta línea sirve para que el código funcione tanto en linux/mac como en Windows
                    File f = new File(appWorkingDir + fileSeparator + stopWordsFileRelativePath);
                    BufferedReader buf  = new BufferedReader(new FileReader(f));
                    String line;
                    while ((line = buf.readLine()) != null) {
                        stopw.add(line);
                    }
                    buf.close();
                }
            } else { //catalán
                if (this.contenido.isEmpty()) throw new NullAttr("El archivo está vacío");
                else {
                    String appWorkingDir = System.getProperty("user.dir");
                    String stopWordsFileRelativePath = "/FONTS/src/Prop/Dominio/Ficherostop/empty-ca.txt";
                    String fileSeparator = FileSystems.getDefault().getSeparator(); // esta línea sirve para que el código funcione tanto en linux/mac como en Windows
                    File f = new File(appWorkingDir + fileSeparator + stopWordsFileRelativePath);
                    BufferedReader buf  = new BufferedReader(new FileReader(f));
                    String line;
                    while ((line = buf.readLine()) != null) {
                        stopw.add(line);
                    }
                    buf.close();
                }
            }
            contenido.removeAll(stopw);

            //añadir frecuencia de palabras
            for(String s : contenido) {
                if(frec.containsKey(s)){
                    frec.put(s,frec.get(s)+1);
                }
                else {
                    frec.put(s, 1.0);
                }
            }
        for (String s: contenido) {
            tf.put(s,(frec.get(s)/contenido.size()));
        }
    }
}
