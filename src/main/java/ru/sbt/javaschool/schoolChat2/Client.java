package ru.sbt.javaschool.schoolChat2;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;
/**
 * client start with 2 parameter:
 * ip address
 * port
 * where was satarted server
 * java -cp target/net.jar ru.sbt.javaschool.schoolChat2.Client 127.0.0.1 5555
 * */
public class Client {
    private final String host;
    private final int port;

    public Client( String host, int port ) {
        this.host = host;
        this.port = port;
    }
/**
 * @param args contains args[0] - address, args[1] - port
 * */
    public static void main( String[] args ) throws IOException {
        try {
            new Client( args[0], Integer.parseInt( args[1] ) ).run();
        } catch ( ArrayIndexOutOfBoundsException e ) {
            System.err.println( "Error input parameters! (SERVER_IP SERVER_PORT)" );
        } catch ( ConnectException e ) {
            System.err.println( "Connection error! (" + args[0] + ":" + args[1] + ")\nexit..." );
        }
    }

    private void run( ) throws IOException {
        try ( Socket socket = new Socket( host, port );
              PrintStream output = new PrintStream( socket.getOutputStream() );
              Scanner sc = new Scanner( System.in ) ) {

            System.out.println( "Client successfully connected to server!" );
            System.out.print( "What is your name: " );
            String nickname = sc.nextLine();
            output.println( nickname );

            new Thread( new ClientMessagesHandler( socket.getInputStream() ) ).start();

            while ( sc.hasNextLine() ) {
                output.println( sc.nextLine() );
            }
        }
    }
}

