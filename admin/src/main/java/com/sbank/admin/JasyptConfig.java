package com.sbank.admin;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**--------------------------------------------------------------------
 * ■Property 암복호화 설정 ■payletter ■2019-05-15
 --------------------------------------------------------------------**/
@Configuration
public class JasyptConfig {
    private static final String ENCRYPT_KEY = "356a385376d011e98ba800155d485e3f";

    @Bean("JasyptStringEncryptor")
    public StandardPBEStringEncryptor stringEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();

        config.setPassword(ENCRYPT_KEY);
        config.setAlgorithm("PBEWithMD5AndDes");
        encryptor.setConfig(config);

        return encryptor;
    }
}