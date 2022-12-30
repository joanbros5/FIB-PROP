package Prop.Exceptions;

public class ErrMidaVector extends Exception{
    public ErrMidaVector(String errMsg)  {
        super("Vector con tamaño incorrecto, Expected "+errMsg);
    }
}

