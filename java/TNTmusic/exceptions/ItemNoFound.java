package TNTmusic.exceptions;

public class ItemNoFound extends Exception{
    public ItemNoFound(){
        super();
    }

    public ItemNoFound(String msg){
        super(msg);
    }
}