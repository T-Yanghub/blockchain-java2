package com.yang.blockchain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yang.blockchain.BlockchainApplication;
import com.yang.blockchain.bean.Block;
import com.yang.blockchain.bean.Massege;
import com.yang.blockchain.bean.NoteBook;
import com.yang.blockchain.bean.Transaction;
import com.yang.blockchain.websocket.MyClient;
import com.yang.blockchain.websocket.MySokect;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.PostConstruct;

/**
 * ClassName:BlockController
 * Description:
 */
@RestController
public class BlockController {
    NoteBook noteBook = NoteBook.getNoteBook();

    /*开启项目的时候开启一个服务端*/
    private MySokect server;


    /*添加首页*/
    @PostMapping("/addGenesis")
    public String addGenesis() {
        return "";
    }

    /*添加记录*/
    @PostMapping("/addNote")
    public String addNote(Transaction transaction) {
        System.out.println(transaction.senderPublicKey);
        System.out.println(transaction.signaturedData);
       /* System.out.println("执行科添加");
        System.out.println(content);*/
        try {
            Massege massege = new Massege();
            ObjectMapper objectMapper = new ObjectMapper();
            String transactionStr = objectMapper.writeValueAsString(transaction);
            massege.setContent(transactionStr);
            massege.setType(2);
            String msg = objectMapper.writeValueAsString(massege);
            server.broadcast(msg);

            if (transaction.verify()) {


                noteBook.add(transactionStr);
                return "添加数据成功";
            } else {
                throw new RuntimeException("交易数据有问题");
            }

        } catch (Exception e) {
            return "添加数据失败" + e.getMessage();
        }


    }

    /*查看内容*/
    @RequestMapping("/showlist")
    public ArrayList<Block> showlist() {
        ArrayList<Block> blocks = noteBook.showBlocks();
        System.out.println("执行了");
        return blocks;
    }

    /* 保存数据*/
    @PostMapping("/saveToDisk")
    public String saveToDisk() {
        return "";
    }


    // 校验数据
    @RequestMapping(value = "/check")
    public String check() {
        String check = noteBook.check();

        if (StringUtils.isEmpty(check)) {
            return "数据是安全的";
        }
        return check;
    }

    @PostConstruct
    public void init() {
        server = new MySokect(Integer.parseInt(BlockchainApplication.port) + 1);
        server.startServer();
        System.out.println(server.getPort());
    }

    /*客户端注册*/
    /*先把连接的地址保存到集合*/
    HashSet<String> set = new HashSet<>();


    @RequestMapping("register")
    public String register(String url) {
        if (url != null) {
            set.add(url);
            return "添加成功";
        }
        return "添加失败";
    }

    /*保存所有的节点*/
    private ArrayList<MyClient> clients = new ArrayList<>();

    /*遍历集合打开连接*/
    @RequestMapping("/conn")
    public String conn() {
        try {
            for (String s : set) {

                URI url = new URI("ws://localhost:" + s);
                MyClient myClient = new MyClient(url, s);
                myClient.connect();
                clients.add(myClient);
            }
            return "连接成功";
        } catch (Exception e) {
            return "连接失败";
        }

    }

    /*广播*/
    @RequestMapping("/broadcast")
    public String broadcast(String massege) {
        server.broadcast(massege);

        return "广播成功";

    }

    // 请求同步其他节点的区块链数据
    @RequestMapping("/syncData")
    public String syncData() {
        /*循环遍历向所有连接上的节点请求同步*/
        for (MyClient client : clients) {
            client.send("亲,把你的区块链数据给我一份");
        }

        return "广播成功";
    }

}

