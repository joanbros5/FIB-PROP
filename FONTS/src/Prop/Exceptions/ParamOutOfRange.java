package Prop.Exceptions;
//cuando una variable identificadora no esta dentro del rango de la estructura de datos
public class ParamOutOfRange extends Exception {
    public ParamOutOfRange(String errorMsg) {
        super(errorMsg);
    }
}