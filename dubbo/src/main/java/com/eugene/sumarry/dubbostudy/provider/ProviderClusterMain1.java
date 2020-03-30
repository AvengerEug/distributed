package com.eugene.sumarry.dubbostudy.provider;

import com.eugene.sumarry.dubbostudy.provider.config.ProviderClusterConfig1;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class ProviderClusterMain1 {

    public static void main(String[] args) throws IOException {
        // 给jvm设置参数，让dubbo加载指定配置文件
        new AnnotationConfigApplicationContext(ProviderClusterConfig1.class);
        System.in.read(); // 按任意键退出
    }
}
