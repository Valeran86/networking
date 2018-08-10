package ru.sbt.javaschool.chat;

import ru.sbt.javaschool.ChatBase;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.System.out;

/*
* Разработать клиент-серверное приложение SchoolChat
Вход в систему по логину – дополнительных проверок не надо делать
Историю чатов хранить не нужно
Сервер понимает 2 команды:
Отправить сообщение пользователю
Получить все сообщения адресованные текущему пользователю
Клиент при получении сообщения просто выводит информацию:
«user >> message_text»
 * При входе нового пользователя все участники чата получают нотификацию (сообщение от пользователя system)
*
* */

public class Server extends ChatBase {
    public static final Map<String, List<Message>> messages = new ConcurrentHashMap<>();
    private static final Queue<Message> notifyQueue = new LinkedBlockingQueue<>();
    private static final Queue<Message> messagesQueue = new LinkedBlockingQueue<>();
    public static final Queue<String> activeUsers = new LinkedBlockingQueue<>();

    public static void main( String[] args ) {
        port = ChatBase.getPort(args);

        //TODO register multicast from notifyQueue
        //TODO receive message from current connected clients

        //TODO put message from `messagesQueue` to `messages`
        //TODO send to Client all messages for it on connect


        try {
            ServerSocket serverSocket = new ServerSocket(port);

            out.println("Register listener on " + serverSocket.getInetAddress().toString() + ":" + port);
            do{
                out.println("Waiting new client...");
                Socket client = serverSocket.accept();

                out.println( "notifyQueue\n" + notifyQueue + "\n" );
                out.println( "messagesQueue\n" + messagesQueue + "\n" );

                ChatThread teller = new ChatThread( client, messagesQueue, notifyQueue );

                out.println("Start talking...");
                teller.start();
            }
            while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
