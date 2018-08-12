package ru.sbt.javaschool.schoolChat2;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

public class User {
    private final String name;
    private PrintStream streamOut;
    private InputStream streamIn;

    User( String name ) {
        this.name = name;
    }

    User( Socket client, String name ) throws IOException {
        this.streamOut = new PrintStream( client.getOutputStream() );
        this.streamIn = client.getInputStream();
        this.name = name;
    }

    public void sendMessage( String message ) {
        getOutStream().println( message );
    }

    private PrintStream getOutStream( ) {
        return this.streamOut;
    }

    public InputStream getInputStream( ) {
        return this.streamIn;
    }

    public String getName( ) {
        return this.name;
    }

    @Override
    public String toString( ) {
        return getName();
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        User user = (User) o;

        return name.equals( user.name );
    }

    @Override
    public int hashCode( ) {
        return name.hashCode();
    }
}