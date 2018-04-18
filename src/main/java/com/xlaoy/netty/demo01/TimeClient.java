package com.xlaoy.netty.demo01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Administrator on 2018/4/16 0016.
 */
public class TimeClient {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("127.0.0.1", 8000);
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("get time");
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String content = reader.readLine();
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(reader != null) {
                    reader.close();
                }
                if(writer != null) {
                    writer.close();
                }
                if(socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
