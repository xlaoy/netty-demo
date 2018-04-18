package com.xlaoy.netty.demo01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Administrator on 2018/4/16 0016.
 */
public class TimeServer {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8000);
            System.out.println("服务器启动，监听8080");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("来了新链接");
                new Thread(new TimeHandler(socket)).start();
            }
        } finally {
            if(serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    public static class TimeHandler implements Runnable {

        private Socket socket;

        public TimeHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            BufferedReader reader = null;
            PrintWriter writer = null;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
                while (true) {
                    String content = reader.readLine();
                    if(content == null || "".equals(content)) {
                        break;
                    }
                    if("get time".equalsIgnoreCase(content)) {
                        writer.println(LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));
                    }
                }
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
}
