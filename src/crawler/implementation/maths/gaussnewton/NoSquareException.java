/*
 * Title: NoSquareException.java
 * Project: telegramJ
 * Creator: Georgii Mikriukov
 */

/**
 * The author of this code: Ata Amini
 * Taken from here:
 * https://www.codeproject.com/Articles/1175992/Implementation-of-Gauss-Newton-Algorithm-in-Java
 */

package crawler.implementation.maths.gaussnewton;

public class NoSquareException extends Exception {

    public NoSquareException() {
        super();
    }

    public NoSquareException(String message) {
        super(message);
    }

}
