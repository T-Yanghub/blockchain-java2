package com.yang.blockchain.bean;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yang.blockchain.utils.HashUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * ClassName:NoteBook
 * Description:
 */
public class NoteBook {
    /*这个区块链必须是单例 只有一条*/
    private static NoteBook noteBook;

    /*私有构造方法*/
    private NoteBook() {
        init();
    }

    public static NoteBook getNoteBook() {
        if (noteBook == null) {
            synchronized (NoteBook.class) {
                if (noteBook == null) {
                    noteBook = new NoteBook();


                }
            }
        }
        return noteBook;
    }

    /*保存数据的集合*/
    private ArrayList<Block> blocks = new ArrayList<>();

    /*区块链存在时创世区块就生成*/
    public void init() {
        File file = new File("blocks.json");
        if (file.exists() && file.length() > 0) {
            ObjectMapper objectMapper = new ObjectMapper();

            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, Block.class);
            try {
                blocks = objectMapper.readValue(file, javaType);
            } catch (IOException e) {

            }

        } else {
            addFirst();
        }

        save2Disk();


    }

    /*挖矿*/
    public static int mine(String content) {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String hash = HashUtils.sha256(content + i);
            if (hash.startsWith("0000")) {
                return i;
            }
        }
        throw new RuntimeException("挖矿失败");
    }

    public ArrayList<Block> showBlocks() {
        return blocks;
    }

    /*添加创世区块*/
    public void addFirst() {
        File file = new File("blocks.json");
        Block block = new Block();
        block.id = 1;
        block.content = "创世区块";
        block.preHash = "0000000000000000000000000000000000000000000000000000000000000000";
        block.nonce = mine(block.content + block.preHash);
        block.hash = HashUtils.sha256(block.content + block.preHash + block.nonce);

        blocks.add(block);

        save2Disk();


    }

    /*添加区块*/
    public void add(String note) {
        Block block = new Block();
        block.id = blocks.size() + 1;
        block.preHash = blocks.get(blocks.size() - 1).hash;
        block.content = note;
        block.nonce = mine(block.content + block.preHash);
        block.hash = HashUtils.sha256(block.content + block.preHash + block.nonce);
        blocks.add(block);
        save2Disk();
    }


    // 保存数据
    // xml ,json
    // fastjson jackson gson
    public void save2Disk() {
        try {
            // jackson序列化对象的方法
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File("blocks.json"), blocks);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /*数据校验*/
    public String check() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            String hash = block.hash;
            String content = block.content;
            int id = block.id;
            int nonce = block.nonce;
            String preHash = block.preHash;
            if (!hash.equals(HashUtils.sha256(content + preHash + nonce))) {
                sb.append("编号为" + block.id + "的hash有问题,请注意检查<br>");
            }
            Block preBlock = blocks.get(i - 1);
            String preBlockHash = preBlock.hash;
            if (!preBlockHash.equals(preHash)) {
                sb.append("编号为" + block.id + "的hash有问题,请注意检查<br>");
            }
        }


        return sb.toString();

    }

    // 和本地的区块链进行比较,如果对方的数据比较新,就用对方的数据替换本地的数据
    public void compareData(ArrayList<Block> newList) {
        // 比较长度, 校验
        if (newList.size() > blocks.size()) {
            blocks = newList;
            save2Disk();
        }
    }

}
