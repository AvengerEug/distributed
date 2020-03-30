package com.eugene.sumarry.dubbostudy.provider;

import com.eugene.sumarry.dubbostudy.provider.config.ProviderClusterConfig3;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class ProviderClusterMain3 {

    public static void main(String[] args) throws IOException {
        System.setProperty("dubbo.properties.file", "dubbo-cluster-3/dubbo.properties");
        new AnnotationConfigApplicationContext(ProviderClusterConfig3.class);
        System.in.read(); // 按任意键退出
    }
}
