package ru.sbt.javaschool.schoolChat2;

import java.io.InputStream;
import java.util.Scanner;

public class ClientMessagesHandler implements Runnable {
    private final InputStream inputStream;

    ClientMessagesHandler( InputStream inputStream ) {
        this.inputStream = inputStream;
    }

    public void run( ) {
        try ( Scanner s = new Scanner( inputStream ) ) {
            //wait input messages on InputStream and print their
            while ( s.hasNextLine() ) {
                System.err.println( s.nextLine() );
            }
        }
    }
}