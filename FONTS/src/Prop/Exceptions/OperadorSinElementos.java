package Prop.Exceptions;

public class OperadorSinElementos extends Exception {
    public OperadorSinElementos()  {
        super("Un operador necesita elementos sobre los que operar");
    }
}
