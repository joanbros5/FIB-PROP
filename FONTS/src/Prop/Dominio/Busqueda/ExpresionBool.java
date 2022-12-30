package Prop.Dominio.Busqueda;

import Prop.Dominio.DocumentKey;
import Prop.Dominio.Documento;

import java.util.ArrayList;

/*
* Base de una expresión booleana
* */
public interface ExpresionBool {

    /**
     * Imprime por pantalla la expresion booleana
     */
    public void printExpresion();

    /**
     * Compara 2 expresiones booleanas
     * @param a => ExpresionBool a comparar con this
     * @return true => this es igual a la ExpresionBool a
     */
    public boolean equal(ExpresionBool a);

    /**
     * Se devuelve a si mismo en formato String
     * @return => String
     */
    public String getStrExpresion();

    /**
     * Compara una frase dada de doc con esta expresion booleana
     * @param doc => Documento de donde pertenece frase
     * @param frase => indice de una frase de doc
     * @return true => Si frase cumple la condicion de esta consulta
     */
    public boolean getCumpleCondicionFrase(final Documento doc, int frase);
}
