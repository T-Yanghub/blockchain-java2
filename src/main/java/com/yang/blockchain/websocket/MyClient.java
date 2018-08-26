package com.yang.blockchain.websocket;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yang.blockchain.bean.Block;
import com.yang.blockchain.bean.Massege;
import com.yang.blockchain.bean.NoteBook;
import com.yang.blockchain.bean.Transaction;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;

/**
 * ClassName:MyClient
 * Description:
 */
public class MyClient extends WebSocketClient {
    private String name;

    public MyClient(URI serverUri, String name) {
        super(serverUri);
        this.name = name;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("客户端__" + name + "__打开了连接");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("客户端__" + name + "_接受到消息" + message);
        try {
            NoteBook noteBook = NoteBook.getNoteBook();
            ObjectMapper mapper = new ObjectMapper();
            Massege massege = mapper.readValue(message, Massege.class);
            if (massege.getType() == 1) {
                System.out.println("请求同步");
                JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, Block.class);
                ArrayList<Block> blocks = mapper.readValue(massege.getContent(), javaType);
                noteBook.compareData(blocks);
            } else if (massege.getType() == 2) {
                Transaction transaction = mapper.readValue(massege.getContent(), Transaction.class);
                if (transaction.verify()) {

                    System.out.println("开始挖矿");
                    noteBook.add(massege.getContent());
                }
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("客户端__" + name + "__关闭了连接");
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("客户端__" + name + "__发生了错误");
    }
}
