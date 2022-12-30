/**
 * @file conjTfidf.java
 * @brief Contiene la clase conjTfidf
 */
package Prop.Dominio;
import Prop.Exceptions.BadParameter;
import Prop.Exceptions.DocAlreadyExists;
import Prop.Exceptions.DocNotExists;
import Prop.Exceptions.NullAttr;

import java.util.*;

/**
 * @author Daniel Ruiz Jiménez
 * Clase que contiene los métodos y atributos del conjunto de tfidf.
 */
public class conjTfidf {
    //tfidf = tf*idf (de cada termino en cada documento)
    /**
     * Cada conjTfidf cuenta con los siguientes atributos:
     * conjuntotfidf: TreeMap que contiene todos los documentos introducidos en el programa. Su key es la DocumentKey de cada documento, y su Value son las estructuras de datos relacionadas con el tf-idf
     * simil: TreeMap que contiene la similaridad de un documento con el resto. Su key es la similaridad entre dos documentos y su value es la documentKey del documento similar a uno dado (usado para una de las funciones de la clase)
     * auxidf: HashMap que contiene todas las palabras del conjunto de documentos y el número de documentos en los que aparece cada una. Lo usaremos como estructura de datos auxiliar para calcular la métrica idf del conjunto
     */
    private final TreeMap<DocumentKey,tfidf> conjuntotfidf;
    private final TreeMap<Double,DocumentKey> simil;
    private final HashMap<String, Double> auxidf; //tendremos todas las palabras de todos los documentos con su frecuencia

    /**
     * Creadora de la clase
     * conjuntotfidf: se crea un nuevo TreeMap ordenado alfabéticamente por Autor, y en caso de que haya dos documentos escritos por el mismo autor, se ordena alfabéticamente por título mediante un Comparator
     * auxidf: se crea un nuevo HashMap
     * simil: se crea un nuevo TreeMap ordenado descendentemente por el valor de la similaridad entre documentos
     */
    public conjTfidf(){
        conjuntotfidf = new TreeMap<>(new Comparator<DocumentKey>() {
            @Override
            public int compare(DocumentKey o1, DocumentKey o2) {
                int TituloCompare = o1.getTitulo().compareTo(o2.getTitulo());
                int AutorCompare = o1.getAutor().compareTo(o2.getAutor());
                return (AutorCompare == 0) ? TituloCompare
                        : AutorCompare;
            }
        });

        auxidf = new HashMap<>();
        simil = new TreeMap<>(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
               return o2.compareTo(o1);
            }
        });
    }

    //getters
    public TreeMap<DocumentKey,tfidf> getconjTfidf() {return this.conjuntotfidf;}
    public tfidf getValue(DocumentKey dk) {return conjuntotfidf.get(dk);}
    public HashMap<String,Double> getAuxidf() {return this.auxidf;}


    /**
     * alta de Documento en el conjunto
     * @param dk La DocumentKey del documento que queremos introducir en el conjunto
     * @param t La estructura de datos para calcular el tf-idf de ese Documento
     * @throws Exception en el caso de que la DocumentKey ya exista en el conjunto, se lanzará la excepción pertinente
     */
    public void altaDoc(DocumentKey dk,tfidf t) throws Exception{ //añadir un documento al conjunto
        if (conjuntotfidf.containsKey(dk)) throw new DocAlreadyExists("El documento ya existe en el conjunto");
        else conjuntotfidf.put(dk,t);
    }

    /**
     * Baja de Documento del conjunto
     * @param dk La DocumentKey de cuyo documento queremos eliminar
     * @throws Exception En el caso de que la DocumentKey no exista en el conjunto, se lanzará la excepción pertinente
     */
    public void bajaDoc(DocumentKey dk) throws Exception{
        if(!conjuntotfidf.containsKey(dk)) throw new DocNotExists("El documento que buscas no existe");
        else conjuntotfidf.remove(dk);
    }

    /**
     * Relleno del HashMap auxidf
     * De todos los Documentos del conjunto, metemos en la estructura de datos cada palabra diferente de todos los documentos, y en su value añadimos el número de documentos del conjunto que contienen esa palabra.
     * @throws Exception en el caso de que el conjunto se encuentre vacío, se lanzará la excepción pertinente.
     */
    public void rellenarAux() throws Exception{ //nº documentos que contienen cada palabra del conjunto
        if(conjuntotfidf.isEmpty()) throw new NullAttr("El conjunto se encuentra vacío");
        else{
            for (tfidf t : conjuntotfidf.values()) { //cada tfidf del conjunto
                for (String s : t.getTf().keySet()) { //las palabras de cada tfidf sin stopwords y eso
                    if (!auxidf.containsKey(s)) {
                        auxidf.put(s, 1.0);
                    } else {
                        auxidf.put(s, auxidf.get(s) + 1);
                    }
                } //el auxidf está bien, se llena con todas las palabras de todos los documentos y en cada palabra está el numero de documentos donde aparece cada una
            }
        }
    }

    /**
     * Cálculo de la métrica tf-idf de los documentos del conjunto
     * Se realizan los siguientes pasos:
     * 1. Se amplían los HashMaps de tf e idf de cada documento del conjunto, porque para comparar dos vectores deben ser del mismo tamaño, por lo que añadimos a cada uno también las palabras que no tienen de todas las diferentes, con un valor de 0.0
     * 2. Se calcula en este momento la métrica idf de cada Documento, es en este momento porque hasta este punto sabemos que queremos calcular los k documentos más similares que poseemos. Además, necesitamos saber el número exacto de documentos que contienen cada palabra y el tamaño final del conjunto
     * 3. Aprovechamos esta función para calcular la métrica tf-idf entera, multiplicando los valores tf-idf de cada palabra del documento, y lo añadimos a la estructura de datos completo
     * @throws Exception En el caso de que el conjunto se encuentre vacío, se lanzará la excepción pertinente
     */
    public void calculoTFIDF() throws Exception{ //calculo el idf y ya de paso el tfidf
        if(conjuntotfidf.isEmpty()) throw new NullAttr("El conjunto se encuentra vacío");
        else{
            for (DocumentKey dk : conjuntotfidf.keySet()) {
                tfidf t = getValue(dk);
                //cada tfidf de cada elemento del conjunto
                //cada palabra de cada documento
                for (String s2 : auxidf.keySet()) {
                    if (!t.getTf().containsKey(s2)) {
                        t.getTf().put(s2, 0.0);

                    }
                    if (!t.getIdf().containsKey(s2)) {
                        t.getIdf().put(s2, 0.0);
                    }
                }
                for (String s : t.getTf().keySet()) {
                    double tmp = conjuntotfidf.size() / auxidf.get(s); //num documentos/nº docs que contienen el término
                    double idfvalue = Math.log(tmp) / Math.log(2); //logaritmo en base 2 con cambio de base, muy chorras
                    t.getIdf().put(s, idfvalue);
                    double tmp2 = getValue(dk).getIdf().get(s) * getValue(dk).getTf().get(s);
                    getValue(dk).getCompleto().put(s, tmp2);
                }
            }
        }
    }

    /**
     * Obtener los k documentos más similares a uno dado
     * Se realizan los siguientes pasos:
     * 1. Una vez calculados los tf-idf de todos los documentos, se procederá a calcular la similaridad del documento dk con el resto del conjunto
     * 2. Primero obtenemos el tf-idf del documento a comparar (aux1), e iteramos sobre cada elemento del conjunto. Como es inútil comparar un documento con otro con el mismo contenido (él mismo o una copia de éste), dejamos que su similaridad sea 1.0 para ahorrar cálculos
     * 3. Se calcula la similaridad entre dos documentos mediante la similitud coseno
     * 4. Una vez hecho esto se introduce el valor en simil
     * 5. Antes de devolverlos, se quitan del TreeMap aquellos documentos con similaridad 1 (no los hemos considerado relevantes puesto que tienen el mismo contenido exacto)
     * @return un TreeMap de aquellos documentos más similares al dado, ordenados descendentemente según su similaridad
     * @param dk El documento que queremos comparar con el resto
     * @param k El número de documentos similares que queremos ver
     *
     * @throws Exception En el caso de que la DocumentKey no se encuentre en el conjunto o el valor k sea mayor que el tamaño del conjunto (no habŕa k documentos similares), se lanzarán las excepciones pertinentes
     */
    public TreeMap<Double,DocumentKey> getkDocs(DocumentKey dk, Integer k) throws Exception{
        //normalizar los completos
        //calcular similaridad del documento dk con el resto mirando el vector tfidf de cada uno
        //ordenarlo descendentemente
        //imprimir las dk de los k documentos mas similares
        //k tiene que ser maximo el tamaño del conjunto-1
        simil.clear();
        if(!conjuntotfidf.containsKey(dk)) throw new DocNotExists("El documento no existe");
        else if (k >= conjuntotfidf.size()) throw new BadParameter("El valor de k es incorrecto");
        else{
            auxidf.clear();
            rellenarAux(); //lleno el vector grande de todas las palabras del conjunto
            calculoTFIDF();
            TreeMap<String, Double> aux1 = getValue(dk).getCompleto(); //el que querremos comparar con los demás
            for (DocumentKey dc : conjuntotfidf.keySet()) {
                TreeMap<String, Double> aux2 = getValue(dc).getCompleto();
                if (aux1 == aux2) simil.put(1.0, dk); //similitud del documento que queremos con el resto
                else {
                    double similarity = cosineSim(aux1, aux2);
                    simil.put(similarity, dc);
                }
            }
            simil.remove(1.0);
        }
        return simil;
    }

    /**
     * Cálculo de la similaridad de dos documentos
     * @param t1 El conjunto de valores tf-idf del primer documento a comparar
     * @param t2 El conjunto de valores tf-idf del segundo documento a comparar
     * @return Devuelve la similaridad de dos documentos mediante el cálculo de la similitud coseno, que consiste en hacer el producto escalar de cada valor tf-idf de los dos vectores, dividio entre sus módulos multiplicados
     * @throws Exception En el caso de que uno o los dos documentos estén vacíos, se lanzará la excepción pertinente
     */
    public Double cosineSim(TreeMap<String,Double>t1, TreeMap<String,Double> t2) throws Exception{
        //normalizar vectores, habiendo calculado el módulo, dividir cada elemento por ese num (vector unitario)
        //producto escalar de los dos vectores unitarios
        if(t1.isEmpty() || t2.isEmpty()) throw new NullAttr("No se puede realizar el cálculo de la similaridad");
        else{
            double dotProduct = 0.0;
            double normA = 0.0;
            double normB = 0.0;
            for (String s : t1.keySet()) {
                dotProduct += t1.get(s) * t2.get(s);
                normA += Math.pow(t1.get(s), 2);
                normB += Math.pow(t2.get(s), 2);
            }
            return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        }
    }
}
