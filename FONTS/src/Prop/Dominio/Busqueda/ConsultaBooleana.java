package Prop.Dominio.Busqueda;

import Prop.Dominio.DocumentKey;
import Prop.Dominio.Documento;
import Prop.Dominio.conjDocumentos;
import Prop.Exceptions.*;
import Prop.Exceptions.VoidConsulta;

import java.util.ArrayList;
import java.util.List;

public class ConsultaBooleana{
    ExpresionBool expresion;

    /**
     * Creadora => crea una ConsultaBooleana donde la expresión es la indicada en strExp
     * @param strExp => String que contiene la expresión booleana
     * @throws Exception => En caso de la expresión esté vacía
     * @throws Exception => Si la expresión está mal construida
     */
    public ConsultaBooleana (String strExp) throws Exception{
        ArrayList<String> exp = translateToArrayList(strExp);
        if (exp.size() == 0) throw new VoidConsulta();
        if (exp.size() > 1) expresion = new OperadorBool(exp);
        else expresion = new HojaBool(exp);

    }

    /**
     * Cambia el formato de una expresión booleana de String a ArrayList<String>
     * @param strExp => String que contiene la expresión booleana
     * @return ArrayList<String> con la misma expresión booleana dada en la entrada
     * @throws Exception Si los parentesis están mal construidos (No se abren o cierran correctamente)
     * @throws Exception Si falta algun caracter que cierre algún conjunto de texto ('"', '}')
     * @throws Exception Si algún operador no tiene algun elemento sobre el que operar
     */
    private ArrayList<String> translateToArrayList(String strExp) throws Exception{
        ArrayList<String> exp = new ArrayList<>();
        int parlvl = 0;
        int size = strExp.length();
        String actWord = "";
        for (int i = 0; i < size; ++i) {
            char c = strExp.charAt(i);

            if (parlvl < 0) throw new ParentesisMalConstruidos();
            else if (c == '(') {
                actWord = checkActWord(exp, actWord);
                exp.add("(");
                ++parlvl;
            }
            else if (c == ')') {
                actWord = checkActWord(exp, actWord);
                exp.add(")");
                --parlvl;
            }
            else if (c == '{') {
                actWord = checkActWord(exp, actWord);
                int preI = i;
                ++i;

                while (i < size && strExp.charAt(i) != '}') ++i;
                if (i == size) throw new CharNotFound('}');

                String[] subStr = strExp.substring(preI+1 ,i).split(" ");
                exp.add("(");
                for (int j = 0; j < subStr.length; ++j) {
                    exp.add(subStr[j]);
                    if (j < subStr.length - 1) exp.add("\\&");
                }
                exp.add(")");
            }
            else if (c == '"') {
                actWord = checkActWord(exp, actWord);
                int preI = i;
                ++i;

                while (i < size && strExp.charAt(i) != '"') ++i;
                if (i == size) throw new CharNotFound('"');

                String subStr = strExp.substring(preI+1 ,i);
                exp.add(subStr);
            }
            else if (c == '|' || c == '&' || c == '!') {
                actWord = checkActWord(exp, actWord);
                if (i == size -1) throw new OperadorSinElementos();
                char nextC = getNextChar(strExp, i);
                if (nextC == '|' || nextC == '&' || nextC == ')') throw new OperadorSinElementos();

                if (c == '|' || c == '&') {
                    if (i == 0) throw new OperadorSinElementos();
                    char preC = getPreChar(strExp, i);
                    if (preC == '(') throw new OperadorSinElementos();
                }
                if (c == '|') exp.add("\\|");
                else if (c == '&') exp.add("\\&");
                else if (c == '!') exp.add("\\!");
            }
            else if (c != ' ') {
                actWord += c;
            }



        }
        checkActWord(exp, actWord);
        return exp;
    }

    /**
     *  Actualiza el array exp con el String actWord si actWord no esta vacio
     * @param exp => ArrayList<String> donde se quiere añadir actWord
     * @param actWord => String a añadir
     * @return
     */
    private String checkActWord(ArrayList<String> exp, String actWord) {
        if (actWord.length() > 0) {
            exp.add(actWord);
            return "";
        }
        return actWord;
    }

    /**
     * Devuelve el siguiente caracter de un String que no sea un espacio en blanco
     * @param strExp => String de donde se quiere sacar el caracter
     * @param pos => Posicion actual (Se miraran posiciones > pos)
     * @return el siguiente caracter != ' '
     * @throws Exception Si no hay ningun caracter diferente de ' ' despues de la posicion pos
     */
    private char getNextChar(String strExp, int pos) throws Exception{
        for (int i = pos + 1; i < strExp.length(); ++i) {
            char actChar = strExp.charAt(i);
            if (actChar != ' ') return actChar;
        }
        throw new CharNotFound('n');
    }

    /**
     * Devuelve el siguiente caracter anterior que no sea ' ' de un String
     * @param strExp => String de donde se quiere sacar el caracter
     * @param pos => Posicion actual (Se miraran posiciones < pos)
     * @return el caracter previo != ' '
     * @throws Exception Si no hay ningun caracter diferente de ' ' antes de la posicion pos
     */
    private char getPreChar(String strExp, int pos) throws Exception{
        for (int i = pos - 1; i >= 0; --i) {
            char actChar = strExp.charAt(i);
            if (actChar != ' ') return actChar;
        }
        throw new CharNotFound('p');
    }

    /**
     * Devuelve la lista de DocumentKey de los documentos de docs que cumlen expresion
     * @param docs => documentos que se compararan con la expresion booleana
     * @return ArrayList<DocumentKey>
     */
    public ArrayList<DocumentKey> getCumpleCondicionFrase(final conjDocumentos docs) {
        ArrayList<DocumentKey> keyList = new ArrayList<>();
        for (final DocumentKey key: docs.getListaDKS()) {
            boolean valid = false;
            final Documento doc = docs.getDoc(key);
            for (int i = 0; !valid && i < doc.getNumFrases(); ++i) {
                if (expresion.getCumpleCondicionFrase(doc, i)) {
                    keyList.add(key);
                    valid = true;
                }
            }
        }
        return  keyList;
    }


    /**
     * Devuelve la expresión booleana de esta consulta en formato String
     * @return String
     */
    public String getStrExpresion() {
        return expresion.getStrExpresion();
    }

    /**
     * Imprime por pantalla la expresión booleana de esta consulta
     */
    public void printConsulta() {
        expresion.printExpresion();
    }

    /**
     * Compara dos consultas booleanas
     * @param c => ConsultaBooleana
     * @return true => esta expresión es igual a la expresión de c
     */
    public boolean equal(ConsultaBooleana c) {
        return expresion.equal(c.getConsulta());
    }

    /**
     * Devuelve la expresión booleana de esta consulta
     * @return ExpresionBool
     */
    public ExpresionBool getConsulta() {
        return expresion;
    }

}
