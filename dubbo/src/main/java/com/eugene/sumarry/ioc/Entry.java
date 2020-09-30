package com.eugene.sumarry.ioc;


import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;

public class Entry {

    public static void main(String[] args) {
        ExtensionLoader<PersonService> extensionLoader = ExtensionLoader.getExtensionLoader(PersonService.class);

        URL url = new URL("", "", 1);

        // 这个叫woman的key, 是在com.eugene.sumarry.ioc.PersonService文件中配置的，这个文件配置了key和key对应的对象
        PersonService womanService = extensionLoader.getExtension("woman");
        womanService.say(url);

        /*PersonService personService = extensionLoader.getExtension("man");
        url = url.addParameter("indexServiceImpl1", "indexServiceImpl1");
        personService.say(url);*/

        ExtensionLoader<IndexService> indexServiceExtensionLoader = ExtensionLoader.getExtensionLoader(IndexService.class
        );

        IndexService indexService = indexServiceExtensionLoader.getExtension("indexServiceImpl");

        // 因为在index方法中，添加了@Adaptive注解，其中指定了value为indexServiceImpl，
        // 这将决定url中param参数中的key为indexServiceImpl，value为对应实现类的名称(在spi文件中配置的key)
        // 针对indexService.index(url);方法而言，它会为它内部的indexService属性注入名称为indexServiceImpl1的对象
        url = url.addParameter("indexServiceImpl", "indexServiceImpl1");
        indexService.index(url);
        url = url.addParameter("indexServiceImpl", "indexServiceImpl2");
        indexService.index2(url);


    }
}
