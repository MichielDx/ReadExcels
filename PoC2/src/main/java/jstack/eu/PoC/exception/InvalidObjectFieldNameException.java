package jstack.eu.PoC.exception;

/**
 * Created by Ranjit Kaliraj on 8/15/17.
 */
public class InvalidObjectFieldNameException extends RuntimeException{
    public InvalidObjectFieldNameException(String msg) {
        super(msg);
    }
}
