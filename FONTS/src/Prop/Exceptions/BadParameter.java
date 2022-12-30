package Prop.Exceptions;
//cuando en lugar de una variable de un tipo ponemos otro tipo
public class BadParameter extends Exception {
    public BadParameter(String errorMsg) {
        super(errorMsg);
    }
}
