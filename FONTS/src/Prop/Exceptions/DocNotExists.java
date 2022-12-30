package Prop.Exceptions;
//cuando la DocumentKey no está en el conjunto
public class DocNotExists extends Exception{
    public DocNotExists(String errorMsg) {
        super(errorMsg);
    }
}
