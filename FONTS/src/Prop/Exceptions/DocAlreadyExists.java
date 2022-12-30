package Prop.Exceptions;

//cuando en el conjunto ya contiene la DocumentKey
public class DocAlreadyExists extends Exception{
    public DocAlreadyExists(String errorMsg) {
        super(errorMsg);
    }
}
