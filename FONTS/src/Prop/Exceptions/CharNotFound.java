package Prop.Exceptions;

public class CharNotFound extends Exception{
    public CharNotFound(char c)  {
        super("Caracter "+c+" no encontrado");
    }
}
