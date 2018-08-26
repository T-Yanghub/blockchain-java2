package com.yang.blockchain.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yang.blockchain.bean.Block;
import com.yang.blockchain.bean.Massege;
import com.yang.blockchain.bean.NoteBook;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * ClassName:MySokect
 * Description:
 */
public class MySokect extends WebSocketServer {

    private int port;

    public MySokect(int port) {
        super(new InetSocketAddress(port));
        this.port = port;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("webSocket服务器_" + port + "_打开了连接");
}

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("webSocket服务器_" + port + "_关闭了连接");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("webSocket服务器_" + port + "_收到了消息"+message);

        try {
            if ("亲,把你的区块链数据给我一份".equals(message)) {
                NoteBook noteBook = NoteBook.getNoteBook();
                ArrayList<Block> blocks = noteBook.showBlocks();
                ObjectMapper objectMapper = new ObjectMapper();
                String blocksStr = objectMapper.writeValueAsString(blocks);
                Massege massege = new Massege();
                massege.setType(1);
                massege.setContent(blocksStr);
                String msg = objectMapper.writeValueAsString(massege);
                broadcast(msg);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("webSocket服务器_" + port + "_打开了连接发生了错误");
    }

    @Override
    public void onStart() {
        System.out.println("webSocket服务器_" + port + "_打开了连接");
    }

    public void startServer() {
        new Thread(this).start();
    }
}
