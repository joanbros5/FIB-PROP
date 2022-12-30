package Prop.Exceptions;

public class VoidConsulta extends Exception{
    public VoidConsulta()  {
        super("Una consulta boleana no puede estar vacía");
    }
}
