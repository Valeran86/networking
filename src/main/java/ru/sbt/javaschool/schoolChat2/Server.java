package ru.sbt.javaschool.schoolChat2;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 * server start with 1 parameter:
 * ip address
 *
 * java -cp target/net.jar ru.sbt.javaschool.schoolChat2.Server 5555
 * */
public class Server {

    private int port;
    private ServerSocket server;
    private final List<User> users;

    public static final String TMPL_MSG_DISCONECTED_USER = "User %s is disconected!";
    public static final String TMPL_MSG_CONECTED_USER = "User %s is conected!";

    public static final User SYSTEM_USER = new User( "SYSTEM" );

    /**
     * @param args contains args[0] - port
     * */
    public static void main( String[] args ) throws IOException {
        try {
            new Server( Integer.parseInt( args[0] ) ).run();
        } catch ( ArrayIndexOutOfBoundsException e ) {
            System.err.println( "Error input parameter! (SERVER_PORT)" );
        } catch ( BindException e ) {
            System.err.println( "Start server error! Port is already used (" + args[0] + ")\nexit..." );
        }
    }

    public Server( int port ) {
        this.port = port;
        this.users = new ArrayList<>();
    }

    private void run( ) throws IOException {
        server = new ServerSocket( port );
        System.out.println( "Server started on " + port + " port." );

        while ( true ) {
            Socket clientSocket = server.accept();

            String name = ( new Scanner( clientSocket.getInputStream() ) ).nextLine();
            System.out.println( "New user: \"" + name + "\" host:" + clientSocket.getInetAddress().getHostAddress() );

            User newUser = new User( clientSocket, name );
            this.users.add( newUser );

            newUser.sendMessage( newUser + ", welcome to schoolChat!" );
            new Thread( new ServerMessageHahdler( this, newUser ) ).start();
        }
    }

    public void logoutUser( User user ) {
        this.users.remove( user );
    }

    public void sendMessageToAllUsers( String msg, User userSender ) {
        for ( User user : this.users ) {
            user.sendMessage( userSender.toString() + " >> " + msg );
        }
    }

    public void sendMessageToUser( String msg, User sender, String recipient ) {
        if ( recipient.equals( sender.getName() ) ) {
            sender.sendMessage( Server.SYSTEM_USER + " >> " + "Нельзя отправлять себе сообщения!" );
            return;
        }

        if ( users
                .stream()
                .filter( t -> t.getName().equals( recipient ) )
                //в одном действии и проверяем наличие адресата в чате и отправляем сообщения обоим пользователям
                // если он есть
                .peek( t -> {
                    t.sendMessage( sender.toString() + " >> (private) " + msg );
                    sender.sendMessage( sender.toString() + " >> (private to " + t.toString() + ") " + msg );
                } )
                .count() == 0 ) {
            sender.sendMessage( Server.SYSTEM_USER + " >> " + "Нет такого пользователя: " + recipient + "!" );

        }
    }
}
