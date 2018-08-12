package ru.sbt.javaschool.schoolChat2;

import java.util.Scanner;

import static java.lang.String.format;

public class ServerMessageHahdler implements Runnable {
    private final Server server;
    private final User user;

    ServerMessageHahdler( Server server, User user ) {
        this.server = server;
        this.user = user;
        this.server.sendMessageToAllUsers( format( Server.TMPL_MSG_CONECTED_USER, user ), Server.SYSTEM_USER );
    }

    public void run( ) {
        try ( Scanner sc = new Scanner( this.user.getInputStream() ) ) {
            while ( sc.hasNextLine() ) {
                String message = sc.nextLine();
                if ( message.charAt( 0 ) == '@' ) {
                    if ( message.contains( " " ) ) {
                        int firstSpace = message.indexOf( " " );
                        String recipient = message.substring( 1, firstSpace );
                        message = message.substring( firstSpace + 1, message.length() );
                        server.sendMessageToUser( message, user, recipient );
                    }
                } else {
                    server.sendMessageToAllUsers( message, user );
                }
            }

            server.logoutUser( user );
            this.server.sendMessageToAllUsers( format( Server.TMPL_MSG_DISCONECTED_USER, user ), Server.SYSTEM_USER );
        }
    }
}
