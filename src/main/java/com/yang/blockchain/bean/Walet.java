package com.yang.blockchain.bean;

import com.yang.blockchain.utils.RSAUtils;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * ClassName:Walet
 * Description:
 */
public class Walet {
    PublicKey publicKey;

    PrivateKey privateKey;

    String name;

    public Walet(String name) {
        this.name = name;

        File publicKeyFile = new File(name + ".public");
        File privateKeyFile = new File(name + ".private");
        if (!privateKeyFile.exists() || privateKeyFile.length() == 0 || !publicKeyFile.exists() || publicKeyFile.length() == 0) {
            RSAUtils.generateKeysJS("RSA", name + ".private", name + ".public");
        }
    }


    public static void main(String[] args) {
        Walet a = new Walet("a");
        Walet b = new Walet("b");
    }
}
