package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

  public static void main(String[] args) {
    int num = 0; //number of client threads(two clients will combine to one thread)
    try {
      ServerSocket server = new ServerSocket(12345);
      System.out.printf("Room %d\tServer Address：%s\n", num,
          server.getLocalSocketAddress().toString());
      for (; ; ) {
        System.out.printf("Room %d\tWaiting for the game client to connect the server...\n", num);
        Socket client1 = server.accept(); //Wait for client 1 connection
        BufferedReader in1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
        PrintStream out1 = new PrintStream(client1.getOutputStream());
        out1.printf(
            "Welcome to the room %d, you are player 1, please wait for the other player to connect!\n",
            num);
        out1.flush();
        System.out.printf("ROOM %d: Client 1 connected, waiting for client 2 to connect...\n", num);
        Socket client2 = server.accept(); //Wait for client 2 connection
        BufferedReader in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
        PrintStream out2 = new PrintStream(client2.getOutputStream());
        out2.printf("Welcome to the room %d, you are player 2, the game will start!\n", num);
        System.out.printf("ROOM %d: Client 2 connected, start the room...\n", num);
        out2.flush();
        out1.println("The game will start!");
        out1.flush();
        ServerHandle serverHandle = new ServerHandle(client1, client2);
        serverHandle.start();
        num++;
      }
    } catch (Exception e) {
      System.out.println("Client disconnected");
      System.exit(0);
    }
  }

  public static class ServerHandle extends Thread {

    private Socket client1, client2;
    private BufferedReader in1, in2;
    private PrintStream out1, out2;

    ServerHandle(Socket client1, Socket client2) {
      this.client1 = client1;
      this.client2 = client2;
    }

    @Override
    public void run() {
      super.run();
      try {
        BufferedReader in1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
        PrintStream out1 = new PrintStream(client1.getOutputStream());
        BufferedReader in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
        PrintStream out2 = new PrintStream(client2.getOutputStream());
        for (; ; ) {
          String str1 = in1.readLine();
          //System.out.println("Client 1: "+str1);
          if (str1.contains("23333")) {
            out2.printf("123321%s\n", str1.substring(5));
            int status = str1.charAt(9) - '0';
              if (status == 0) {
                  System.out.println("Tie!");
              } else if (status == 1) {
                  System.out.println("Player 1 wins!");
              } else {
                  System.out.println("Player 2 wins!");
              }
          } else {
            out2.println(str1);
            out2.flush();
          }
          String str2 = in2.readLine();
          //System.out.println("Client 2: "+str2);
          if (str2.contains("23333")) {
            out1.printf("123321%s\n", str2.substring(5));
            //System.out.println("Game over! str2: "+str2);
            int status = str2.charAt(9) - '0';
              if (status == 0) {
                  System.out.println("Tie!");
              } else if (status == 1) {
                  System.out.println("Player 1 wins!");
              } else {
                  System.out.println("Player 2 wins!");
              }
          } else {
            out1.println(str2);
            out1.flush();
          }
        }
      } catch (Exception e) {
        System.out.println("Client disconnected!");
        try{
          client1.close();
          client2.close();
        }catch (Exception e1) {
        }
        //System.exit(0);
      }
    }
  }


}