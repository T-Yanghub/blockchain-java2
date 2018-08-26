package com.yang.blockchain.bean;

import com.yang.blockchain.utils.RSAUtils;

import java.security.PublicKey;

import lombok.Data;

/**
 * ClassName:Transaction
 * Description:
 */
@Data
public class Transaction {
    // 付款方公钥
    public String senderPublicKey;
    // 收款方公钥
    public String receiverPublicKey;
    // 金额
    public String content;
    // 签名
    public String signaturedData;

    /*构造方法*/
    public Transaction() {
    }

    public String getSenderPublicKey() {
        return senderPublicKey;
    }

    public void setSenderPublicKey(String senderPublicKey) {
        this.senderPublicKey = senderPublicKey;
    }

    public String getReceiverPublicKey() {
        return receiverPublicKey;
    }

    public void setReceiverPublicKey(String receiverPublicKey) {
        this.receiverPublicKey = receiverPublicKey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSignaturedData() {
        return signaturedData;
    }

    public void setSignaturedData(String signaturedData) {
        this.signaturedData = signaturedData;
    }

    public Transaction(String senderPublicKey, String receiverPublicKey, String content, String signaturedData) {
        this.senderPublicKey = senderPublicKey;
        this.receiverPublicKey = receiverPublicKey;
        this.content = content;
        this.signaturedData = signaturedData;
    }

    public boolean verify(){

        PublicKey publicKey = RSAUtils.getPublicKeyFromString("RSA",senderPublicKey);
        boolean result = RSAUtils.verifyDataJS("SHA256withRSA", publicKey, content, signaturedData);
           return  result;
    }

}
