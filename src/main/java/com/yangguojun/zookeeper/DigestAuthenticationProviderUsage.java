package com.yangguojun.zookeeper;

import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;

/**
 * 生成digest账户字符串
 */
public class DigestAuthenticationProviderUsage {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        // ZooKeeper会对明文Id先后进行两次编码，分别是SHA-1算法加密和BASE64编码
        System.out.println(DigestAuthenticationProvider.generateDigest("super:admin"));
    }
}
