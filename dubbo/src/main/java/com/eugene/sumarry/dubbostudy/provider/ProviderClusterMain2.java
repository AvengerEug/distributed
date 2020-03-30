package com.eugene.sumarry.dubbostudy.provider;

import com.eugene.sumarry.dubbostudy.provider.config.ProviderClusterConfig2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class ProviderClusterMain2 {

    public static void main(String[] args) throws IOException {
        // 给jvm设置参数，让dubbo加载指定配置文件
        System.setProperty("dubbo.properties.file", "dubbo-cluster-2/dubbo.properties");
        new AnnotationConfigApplicationContext(ProviderClusterConfig2.class);
        System.in.read(); // 按任意键退出
    }
}
