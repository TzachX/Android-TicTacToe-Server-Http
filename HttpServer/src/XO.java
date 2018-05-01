import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Random;

/**
 * Created by hackeru on 29/05/2017.
 */
public class XO implements HttpHandler {
    private int status=400;
    int[][] xo = new int[3][3];
    private byte [] res = "{\"error\":\"Deny\"}".getBytes();
    private boolean isX = true;
    private boolean haswon = false;
    boolean isFirst = true;
    Integer ltr,ltc;
    String lastplayer;

    @Override
    public void handle(HttpExchange http) throws IOException {
        switch (http.getRequestMethod().toUpperCase()){
            case "POST":
                JSONObject json = readJsonFrom(http.getRequestBody());
                if(json != null)
                {
                    try {
                        String question = json.getString("?");
                        if(question.equals("whoami"))
                        {
                            res = checkWho().getBytes();
                        }
                        else if (question.equals("refresh"))
                        {
                            res = refresh().getBytes();
                        }
                        else if(question.equals("move"))
                        {
                            nextMove(json);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case "GET":
                if(haswon)
                {
                    System.out.println("end");
                    res = "gameover".getBytes();
                }
                else
                {
                    System.out.println("haswon is false");
                }
        }
        status=200;
        http.sendResponseHeaders(status,res.length);//Added
        OutputStream os=http.getResponseBody();
        os.write(res);
        os.close();
    }

    private JSONObject readJsonFrom(InputStream is){
        try{
            return new JSONObject(new BufferedReader(new InputStreamReader(is)).readLine());
        }catch(Exception e){
            return null;
        }
    }

    private void nextMove(JSONObject json){
        try
        {
            int row=json.getInt("row");
            ltr = row;
            int col=json.getInt("col");
            ltc = col;
            boolean isX=json.getBoolean("isX");
            lastplayer = isX ? "X" : "O";
            xo[row][col] = isX ? 1 : 2;
            System.out.println(row +" "+ col+ " "+lastplayer);
            if (hasWon(row, col))
            {
                System.out.println("first con");
                this.haswon = true;
            }
            else
            {
                isX = !isX;
                this.isX = isX;//toggle for next player's turn
            }
        }
         catch (JSONException e)
         {
            System.out.println("Wrong JSON format");
         }
    }

    private String checkWho()
    {
        if(isFirst)
        {
            isFirst = false;
            return "X";
        }
        else
        {
            return "O";
        }
    }

    private String refresh()
    {
        String currplayer = isX ? "X" : "O";
        if(ltr != null) {
            return currplayer + "," + lastplayer + "," + ltr + "," + ltc;
        }
        else
        {
            return "n";
        }
    }

    private boolean hasWon(int row, int col){
        int i=0,matches=0;
        final int dist = Math.abs(row-col);//
        final int last = xo.length-1;
        final int current = xo[row][col];
        if(dist == last || row == col ) {
            //diagonal
            for (i = 0, matches = 0; i <= last; i++) {
                if (current == xo[i][i]) {
                    matches++;
                    if (matches == xo.length)
                        return true;
                } else {
                    break;
                }
            }
            //rev diagonal
            for(i=0,matches=0;i<=last;i++) {
                if (current == xo[i][last-i]) {
                    matches++;
                    if (matches == xo.length)
                        return true;
                } else {
                    break;
                }
            }
        }

        // rows
        for(i=0,matches=0;i<=last;i++) {
            if (current == xo[i][col]) {
                matches++;
                if (matches == xo.length)
                    return true;
            } else {
                break;
            }
        }
        //cols
        for(i=0,matches=0;i<=last;i++) {
            if (current == xo[row][i]) {
                matches++;
                if (matches == xo.length)
                    return true;
            } else {
                break;
            }
        }
        return false;
    }

}
