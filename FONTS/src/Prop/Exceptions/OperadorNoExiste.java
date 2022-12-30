package Prop.Exceptions;

public class OperadorNoExiste extends Exception{
    public OperadorNoExiste(String op)  {
        super("El operador dado no existe: "+op);
    }
}
