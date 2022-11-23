package application.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import com.google.gson.*;

public class Controller implements Initializable {
    private static final int PLAY_1 = 1;
    private static final int PLAY_2 = 2;
    private static final int EMPTY = 0;
    private static final int BOUND = 90;
    private static final int OFFSET = 15;
    private static int status =-1;

    @FXML
    private Pane base_square;

    @FXML
    private Rectangle game_panel;

    private static boolean TURN = false;

    private static final int[][] chessBoard = new int[3][3];
    private static final boolean[][] flag = new boolean[3][3];
    BufferedReader in;
    PrintStream out;
    private int player=-1;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket client = new Socket();
            client.setKeepAlive(true);
            System.out.println("Connecting to the server, please wait!");
            client.connect(new InetSocketAddress(Inet4Address.getLocalHost(), 12345));
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintStream(client.getOutputStream());
            String str;
            str=in.readLine();
            System.out.println(str);
            if(str.contains("player 1")) {
                TURN=true;
                player=1;
            }
            if(str.contains("player 2")) {
                TURN = false;
                player=2;
            }
            if(TURN){
                str=in.readLine();
                System.out.println(str);
            }

        } catch (Exception e) {
            if(e.getMessage().contains("Connection reset")){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Connection reset");
                alert.setContentText("The connection has been reset by the server(disconnect server), please restart the game!");
                alert.showAndWait();
                System.out.println("The connection has been reset by the server(disconnect server), please restart the game!");
                System.exit(0);
            }
        }
        game_panel.setOnMouseMoved(event -> {
            int x,y;
            try{
                if(!TURN){
                    String str="";
                    str = in.readLine();
                    System.out.println(str);
                    if(str.contains("win")){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Game Over");
                        alert.setHeaderText("Game Over");
                        alert.setContentText("You lose!");
                        alert.showAndWait();
                        System.exit(0);
                    }
                    if(str!=null){
                        if(!str.contains("123321")){
                            x=str.charAt(0)-'0';
                            y=str.charAt(2)-'0';
                        }
                        else{
                            x=str.charAt(6)-'0';
                            y=str.charAt(8)-'0';
                            status=str.charAt(10)-'0';
                            refreshBoard(x,y);
                            System.out.println("status: "+status);
                            if(status==1) {
                                System.out.println("Player1 win!");
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Game Over");
                                alert.setHeaderText("Player1 Win!");
                                alert.showAndWait();
                                clear();
                                System.exit(0);
                            }else if(status==2) {
                                System.out.println("Player2 win!");
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Game Over");
                                alert.setHeaderText("Player2 Win!");
                                alert.showAndWait();
                                clear();
                                System.exit(0);
                            }else if(status==0) {
                                System.out.println("Tie!");
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Game Over");
                                alert.setHeaderText("Tie");
                                alert.showAndWait();
                                clear();
                                System.exit(0);
                            }
                        }
                        if(refreshBoard(x,y)){
                            TURN=!TURN;
                        }
                    }
                }
            }catch (Exception e){
                try {
                    if (e.getMessage().contains("Connection reset")) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Connection reset");
                        alert.setContentText("The connection has been reset by the server(disconnect server), please restart the game!");
                        alert.showAndWait();
                        System.out.println("The connection has been reset by the server(disconnect server), please restart the game!");
                        System.exit(0);
                    }
                }
                catch (Exception e1){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Connection reset");
                    alert.setContentText("The connection has been reset by the server(disconnect server), please restart the game!");
                    alert.showAndWait();
                    System.out.println("The connection has been reset by the server(disconnect server), please restart the game!");
                    System.exit(0);
                }
            }


        });
        game_panel.setOnMouseClicked(event -> {
            int x = (int) (event.getX() / BOUND);
            int y = (int) (event.getY() / BOUND);
            if(TURN==true) {
                if(refreshBoard(x,y)){
                    try{
                        status=checkWin(player);
                        if(status<0) {
                            out.println(x+","+y);
                            System.out.println(x+","+y);
                            out.flush();

                        }
                        else {
                            out.println("23333"+x+","+y+","+String.valueOf(status));
                            System.out.println("23333"+x+","+y+","+String.valueOf(status));
                            out.flush();
                            if(status==1) {
                                System.out.println("Player1 win!");
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Game Over");
                                alert.setHeaderText("Player1 Win!");
                                alert.showAndWait();
                                clear();
                                System.exit(0);
                            }else if(status==2) {
                                System.out.println("Player2 win!");
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Game Over");
                                alert.setHeaderText("Player2 Win!");
                                alert.showAndWait();
                                clear();
                                System.exit(0);
                            }else if(status==0) {
                                System.out.println("Tie!");
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Game Over");
                                alert.setHeaderText("Tie");
                                alert.showAndWait();
                                clear();
                                System.exit(0);
                            }
                        }
                        TURN=!TURN;
                    }
                    catch (Exception e) {
                        if (e.getMessage().contains("Connection reset")) {
                            System.out.println("Occur error when in my turn:" + TURN);
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Connection reset");
                            alert.setContentText("The connection has been reset by the server(disconnect server), please restart the game!");
                            alert.showAndWait();
                            System.out.println("The connection has been reset by the server(disconnect server), please restart the game!");
                            System.exit(0);
                        }
                    }
                }

            }

        });
    }

    private boolean refreshBoard(int x, int y) {
        if (chessBoard[x][y] == EMPTY) {
            chessBoard[x][y] = TURN ? PLAY_1 : PLAY_2;
            drawChess();
            return true;
        }
        return false;
    }

    private void drawChess() {
        for (int i = 0; i < chessBoard.length; i++) {
            for (int j = 0; j < chessBoard[0].length; j++) {
                if (flag[i][j]) {
                    // This square has been drawing, ignore.
                    continue;
                }
                switch (chessBoard[i][j]) {
                    case PLAY_1:
                        drawCircle(i, j);
                        break;
                    case PLAY_2:
                        drawLine(i, j);
                        break;
                    case EMPTY:
                        // do nothing
                        break;
                    default:
                        System.err.println("Invalid value!");
                }
            }
        }
    }

    private void drawCircle(int i, int j) {
        Circle circle = new Circle();
        base_square.getChildren().add(circle);
        circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
        circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
        circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
        circle.setStroke(Color.RED);
        circle.setFill(Color.TRANSPARENT);
        flag[i][j] = true;
    }

    private void drawLine(int i, int j) {
        Line line_a = new Line();
        Line line_b = new Line();
        base_square.getChildren().add(line_a);
        base_square.getChildren().add(line_b);
        line_a.setStartX(i * BOUND + OFFSET * 1.5);
        line_a.setStartY(j * BOUND + OFFSET * 1.5);
        line_a.setEndX((i + 1) * BOUND + OFFSET * 0.5);
        line_a.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_a.setStroke(Color.BLUE);

        line_b.setStartX((i + 1) * BOUND + OFFSET * 0.5);
        line_b.setStartY(j * BOUND + OFFSET * 1.5);
        line_b.setEndX(i * BOUND + OFFSET * 1.5);
        line_b.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_b.setStroke(Color.BLUE);
        flag[i][j] = true;
    }



    private void clear(){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                chessBoard[i][j]=0;
                flag[i][j]=false;
            }
        }
        base_square.getChildren().clear();
    }

    public String save(){
        String result=new String();
        Gson gson = new Gson();
        result=gson.toJson(this.chessBoard);
        return result;
    }


    public static int checkWin(int player) {
        int count = 0;
        int[][] t = new int[8][3];//[num][empty][1-player][2-player] num:0-2 column, 3-5 row, 6-7 cross
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 3; j++) {
                t[i][j] = 0;
            }
        }//initialize
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 0 && j == 0) {
                    t[0][chessBoard[i][j]]++;
                    t[3][chessBoard[i][j]]++;
                    t[6][chessBoard[i][j]]++;
                } else if (i == 0 && j == 1) {
                    t[0][chessBoard[i][j]]++;
                    t[4][chessBoard[i][j]]++;
                } else if (i == 0 && j == 2) {
                    t[0][chessBoard[i][j]]++;
                    t[5][chessBoard[i][j]]++;
                    t[7][chessBoard[i][j]]++;
                } else if (i == 1 && j == 0) {
                    t[1][chessBoard[i][j]]++;
                    t[3][chessBoard[i][j]]++;
                } else if (i == 1 && j == 1) {
                    t[1][chessBoard[i][j]]++;
                    t[4][chessBoard[i][j]]++;
                    t[6][chessBoard[i][j]]++;
                    t[7][chessBoard[i][j]]++;
                } else if (i == 1 && j == 2) {
                    t[1][chessBoard[i][j]]++;
                    t[5][chessBoard[i][j]]++;
                } else if (i == 2 && j == 0) {
                    t[2][chessBoard[i][j]]++;
                    t[3][chessBoard[i][j]]++;
                    t[7][chessBoard[i][j]]++;
                } else if (i == 2 && j == 1) {
                    t[2][chessBoard[i][j]]++;
                    t[4][chessBoard[i][j]]++;
                } else {
                    t[2][chessBoard[i][j]]++;
                    t[5][chessBoard[i][j]]++;
                    t[6][chessBoard[i][j]]++;
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            if (t[i][1] == 3){
                if(player==1) return 1;
                else return 2;
            }
            else if (t[i][2] == 3) {
                if(player==2) return 2;
                else return 1;
            }
            else if (t[i][1] >= 1 && t[i][2] >= 1) count++;
        }
        if (count == 8) return 0;
        else return -1;
    }//0:no one win, 1: player 2 win, 2: player 1 win, -1: no result

}
