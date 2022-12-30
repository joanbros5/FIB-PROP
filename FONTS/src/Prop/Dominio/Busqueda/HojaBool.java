package Prop.Dominio.Busqueda;

import Prop.Dominio.DocumentKey;
import Prop.Dominio.Documento;

import java.util.ArrayList;

import Prop.Exceptions.*;

public class HojaBool implements ExpresionBool {
    private String texto;

    /**
     * Constructora
     * @param s => Array de strings con el elemento para inicializar
     * @throws Exception
     */
    public HojaBool (ArrayList<String> s) throws Exception{
        if (s.size() != 1) throw new ErrMidaVector("1");
        texto = s.get(0);
    }

    /**
     * Se imprime por pantalla la variable texto
     */
    public void printExpresion() {
        System.out.print(texto);
    }

    /**
     * Compara dos ExpresionesBool
     * @param e => ExpresionBool a comparar con this
     * @return true => si e es HojaBool y e tiene el mismo texto que this
     */
    public boolean equal(ExpresionBool e) {
        if (e.getClass() == HojaBool.class) {
            if (((HojaBool) e).getStrExpresion().equals(this.texto)) return true;
        }
        return false;
    }

    /**
     * Compara una frase dada de doc con esta expresion booleana
     * @param doc => Documento de donde pertenece frase
     * @param frase => indice de una frase de doc
     * @return true => la frase de doc contiene la variable texto
     */
    public boolean getCumpleCondicionFrase(final Documento doc, int frase) {
        return doc.hasWord(frase, texto);
    }

    /**
     * Se devuelve texto
     * @return
     */
    public String getStrExpresion() {
        return texto;
    }
}
