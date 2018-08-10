package ru.sbt.javaschool.chat;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.System.out;

public class ChatThread extends Thread {
    public static final String JOINED_NEW_USER = "зашел новый пользователь";
    public static final String USER_LEFT_US = "покинул чат";
    public static final String SYSTEM = "SYSTEM";
    private final Socket socket;
    private final Queue<Message> messages;
    private final Queue<Message> notifyQueue;
    //private final Map<String, List<Message>> messagesSource;

    public ChatThread( Socket client, Queue<Message> messages, Queue<Message> notifyQueue/*,
                       Map<String, List<Message>> messagesSource*/) {
        this.socket = client;
        this.messages = messages;
        this.notifyQueue = notifyQueue;
    }

    @Override
    public void run() {
        final long id = getId();
        try {
            out.println( id + " Connected client " + socket.getInetAddress().toString() + ":" + socket.getPort() );

            BufferedReader reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
            PrintWriter writer = new PrintWriter( socket.getOutputStream() );

            String login = reader.readLine();
            out.println( id + " login: " + login );
            notifyQueue.add( new Message( SYSTEM, login, JOINED_NEW_USER, "" ) );
            writer.println( "Hello, " + login + "!" );
            for (String activeUser: Server.activeUsers){
                addMessage(new Message( SYSTEM, activeUser, JOINED_NEW_USER+" :"+login, ""));
            }


            Server.activeUsers.add(login);

            writer.flush();

            do {
                ObjectInputStream ois = new ObjectInputStream( socket.getInputStream() );

                Message message = (Message) ois.readObject();

                if( message!=null && "написать".equals(message.getCommand()) ){
                    out.println( id + " Client says " + message );
                    messages.add( message );
                    addMessage(message);
                    out.println( "message: " + Server.messages.size() );
                }else if( message!=null && "получить".equals(message.getCommand()) ){
                    ObjectOutputStream oos = new ObjectOutputStream( socket.getOutputStream() );
                    oos.writeObject( getMessages(message) );
                    writer.flush();
                }else if ( "выход".equalsIgnoreCase( message.getCommand() ) ){
                    Server.activeUsers.remove(message.getSource());
                    for (String activeUser: Server.activeUsers){
                        addMessage(new Message( SYSTEM, activeUser, USER_LEFT_US+" :"+login, ""));
                    }
                    break;
                }
            } while ( true );

            writer.println( "Good bye!" );
            writer.flush();
            notifyQueue.add( new Message( SYSTEM, login, USER_LEFT_US, "" ) );

        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        } finally {
            out.println( id + " Closing client connection..." );
            if ( socket != null ) {
                try {
                    socket.shutdownOutput();
                    socket.shutdownInput();
                    socket.close();
                    out.println( id + " Closed" );
                } catch ( IOException ioe ) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    private void addMessage(final Message message){
        if(Server.messages.containsKey(message.getTarget())){
            Server.messages.get(message.getTarget()).add(message);
            return;
        }
        List<Message> messageList = new CopyOnWriteArrayList<>();
        messageList.add(message);
        Server.messages.put(message.getTarget(),messageList);
    }

    public List<Message> getMessages(final Message message) {
        List<Message> messageList = new ArrayList<>();
        if (Server.messages.containsKey(message.getSource())
                && Server.messages.get(message.getSource()).size() > 0) {
            messageList.addAll(Server.messages.get(message.getSource()));
            Server.messages.get(message.getSource()).clear();
            return messageList;
        }
        Message noMessages = new Message( SYSTEM, SYSTEM, "Нет новых сообщений", "" );
        messageList.add(noMessages);
        return messageList;
    }
}
