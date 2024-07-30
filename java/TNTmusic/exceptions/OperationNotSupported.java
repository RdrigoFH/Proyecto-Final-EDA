package TNTmusic.exceptions;

public class OperationNotSupported extends RuntimeException{
    public OperationNotSupported(){super("Operacion no Soportada");}

    public OperationNotSupported(String msg){super(msg);}
}
