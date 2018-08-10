package ru.sbt.javaschool.chat;

import ru.sbt.javaschool.ChatBase;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.console;
import static java.lang.System.out;

public class Client extends ChatBase {

    public static void main( String args[] ) {
        port = ChatBase.getPort( args );

        Socket socket = new Socket();
        try {
            InetSocketAddress target = new InetSocketAddress( Inet4Address.getLocalHost(), port );
            out.println( "Connecting to" + target.toString() );
            socket.connect( target );
            out.println( "Connected. " + socket.toString() );

            Scanner scanner = new Scanner(System.in);




            PrintWriter writer = new PrintWriter( socket.getOutputStream() );
            BufferedReader reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

            out.println( "enter login: " );
            String login = scanner.nextLine();
            out.println( "login " + login );
            writer.println( login );
            writer.flush();

            String input = reader.readLine();
            out.println( "System: " + input );
            do {
                out.println("Введите желаемое действие:");
                out.println("   1. получить все сообщения направленные Вам - 'получить';");
                out.println("   2. написать новое сообщение - 'написать';");
                out.println("   3. выйти - 'выход';");
                String command = scanner.nextLine();
                if(command.startsWith("получить")){
                    Message message = Message.builder()
                            .source( login )
                            .target( "" )
                            .text( "" )
                            .command("получить")
                            .build();
                    ObjectOutputStream oos = new ObjectOutputStream( socket.getOutputStream() );
                    oos.writeObject( message );
                    writer.flush();

                    ObjectInputStream ois = new ObjectInputStream( socket.getInputStream() );
                    List<Message> messages = (List<Message>) ois.readObject();
                    System.out.println("Получены сообщения: ");
                    for (Message messageOis : messages) {
                        System.out.println(" - От " + messageOis.getSource() + ": " + messageOis.getText());
                    }
                }else if(command.startsWith("написать")){
                    out.println( "Кому:" );
                    String user = scanner.nextLine();
                    out.println( "Введите сообщение:" );
                    String text = scanner.nextLine();
                    Message message = Message.builder()
                            .source( login )
                            .target( user )
                            .text( text )
                            .command("написать")
                            .build();

                    out.println( "вы ввели " + message );

                    ObjectOutputStream oos = new ObjectOutputStream( socket.getOutputStream() );
                    oos.writeObject( message );

                    writer.flush();
                }else if ( "выход".equalsIgnoreCase( command ) ){
                    Message message = Message.builder()
                            .source( login )
                            .target( "" )
                            .text( "" )
                            .command("выход")
                            .build();
                    ObjectOutputStream oos = new ObjectOutputStream( socket.getOutputStream() );
                    oos.writeObject( message );
                    writer.flush();
                    System.out.println("Выход");
                    break;
                }else{
                    out.println( "Введенная команда не корректна" );
                }
            }
            while ( true );
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
