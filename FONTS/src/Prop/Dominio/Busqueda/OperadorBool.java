package Prop.Dominio.Busqueda;

import Prop.Dominio.Documento;

import java.util.*;

import Prop.Exceptions.*;

/**
* Operador de una expresión booleana
* Puede ser:
* * AND => '&'
* * OR => '|'
* * NOT => '!' => FillD = NULL
* */

public class OperadorBool implements ExpresionBool{
    private char operador;
    private ExpresionBool fillE;
    private ExpresionBool fillD;

    private int posicionOperador;

    /**
     * Inicializa la expresion actual
     * @param exp => Expresión booleana
     * @throws Exception si exp está mal construido (Operadores mal formados o inexistentes, mal parenticado...)
     */
    public OperadorBool (ArrayList<String> exp) throws Exception {
        int alterIdx = this.translate(exp);

        boolean found = false;

        char op;


        if (exp.get(posicionOperador) == "\\!") op = '!';
        else  if (exp.get(posicionOperador) == "\\|") op = '|';
        else  if (exp.get(posicionOperador) == "\\&") op = '&';
        else throw new OperadorNoExiste(exp.get(posicionOperador));

        ArrayList<String> fe = new ArrayList<>(exp.subList(0 + alterIdx, posicionOperador));
        ArrayList<String> fd = new ArrayList<>(exp.subList(posicionOperador+1, exp.size() - alterIdx));

        //Caso de AND
        if (op == '!' || op == '&' || op == '|') {
            operador = op;
            //throw Exceptions
            if (fd.size() < 1) throw new ErrMidaVector(">0");
            if (fd.size() == 1)  fillD = new HojaBool(fd);  //Hijo izquierdo es hoja
            else fillD = new OperadorBool(fd);  //Hijo izquierdo es un operador hoja

            if (op != '!') {
                if (fe.size() < 1)  throw new ErrMidaVector(">0");
                if (fe.size() == 1) fillE = new HojaBool(fe);   //Hijo derecho es hoja
                else fillE = new OperadorBool(fe);  //Hijo derecho es un operador hoja
            }
            else {
                fillE = null;
            }
        }
        else {
            throw new OperadorNoExiste(""+op);
        }
    }

    /**
     * Convierte un ArrayList de String en el operador, el hijo izquiertdo y el hijo derecho (En caso de no ser NOT)
     * @param exp => arrayList a traducir
     * @return la cantidad de parentesis iniciales 'extra' que existen en exp
     * @throws Exception si exp está mal construido (Operadores mal formados o inexistentes, mal parenticado...)
     */
    private int translate (ArrayList<String> exp) throws Exception{
        int parlvl, refParlvl; //Se usa para saber si estamos dentro de un parentesis (tambien se usa para saber si los parentesis son correctos)
        parlvl = refParlvl = 0;
        int o, a, n;    //busca los indices del primer or, and y not que se encuentren
        int posOp = -1;
        char op = 0;

        boolean fin = false;
        while (posOp < 0) {     // se rompe el bucle cuando se encuentre algun operador

            boolean hasReachedlvl0 = false;
            for (int i = + refParlvl; i < exp.size() - refParlvl; ++i) {
                String pos = exp.get(i);
                if (parlvl == 0) {

                    if (pos == "\\|") {
                        if (op != '|') {
                            op = '|';
                            posOp = i;
                        }
                    }
                    else if (pos == "\\&") {
                        if (op != '|' && op != '&') {
                            op = '&';
                            posOp = i;
                        }
                    }
                    else if (pos == "\\!") {
                        if (op != '|' && op != '&' && op != '!') {
                            op = '!';
                            posOp = i;
                        }
                    }
                }

                if (pos == "(") ++parlvl;
                if (pos == ")") --parlvl;
                if (parlvl == 0) hasReachedlvl0 = true;
            }

            if (!hasReachedlvl0) throw new OperadorNoEncontrado();
            if (parlvl + refParlvl != refParlvl) throw new ParentesisMalConstruidos();
            ++refParlvl;
            parlvl = 0;
        }

        if (posOp == exp.size()-1) throw new OperadorSinElementos();
        posicionOperador = posOp;
        return refParlvl - 1;
    }


    /**
     * Compara dos Expresiones booleanas
     * @param e => ExpresionBool a comparar con this
     * @return
     */
    public boolean equal(ExpresionBool e) {
        if (e.getClass() == OperadorBool.class) {
            if (((OperadorBool) e).getOperador() == this.operador) {
                if(((OperadorBool) e).getRight().equal(fillD)) {
                    if (fillE == null) return true;
                    else if (((OperadorBool) e).getLeft().equal(fillE)) return true;
                }
            }
        }
        return false;
    }

    /**
     * Compara una frase dada de doc con esta expresion booleana
     * @param doc => Documento de donde pertenece frase
     * @param frase => indice de una frase de doc
     * @return true => frase cumple la condicion de this
     */
    public boolean getCumpleCondicionFrase(final Documento doc, int frase) {
        if (operador == '&') {
            return (fillE.getCumpleCondicionFrase(doc, frase) && fillD.getCumpleCondicionFrase(doc, frase));
        }
        else if (operador == '|') {
            return (fillE.getCumpleCondicionFrase(doc, frase) || fillD.getCumpleCondicionFrase(doc, frase));
        }
        return (!fillD.getCumpleCondicionFrase(doc, frase));

    }

    /**
     * devuelve el hijo derecho
     * @return fillD
     */
    public ExpresionBool getRight() {
        return fillD;
    }

    /**
     * devuelve el hijo hizquierdo
     * @return fillE
     */
    public ExpresionBool getLeft() {
        return fillE;
    }

    /**
     * devuelve el operador actual
     * @return operador
     */
    public char getOperador() {
        return operador;
    }

    /**
     * Se devuelve a si mismo en formato String
     * @return String
     */
    public String getStrExpresion() {
        String ret = "";
        ret += "(";
        if (fillE != null) ret += fillE.getStrExpresion();
        if (operador != '!') ret += (" "+operador+" ");
        else ret += operador;

        if (fillD != null) ret += fillD.getStrExpresion();

        ret += ")";
        return ret;
    }

    /**
     * Se imprime por pantalla junto a sus hijos
     */
    public void printExpresion() {

        System.out.print("(");
        if (fillE != null) fillE.printExpresion();

        if (operador != '!') System.out.print(" "+operador+" ");
        else System.out.print(operador);

        if (fillD != null) fillD.printExpresion();
        System.out.print(")");
    }

}
