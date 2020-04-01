package com.eugene.sumarry.resourcecode;


import com.eugene.sumarry.resourcecode.service.PersonService;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;

import java.util.HashMap;
import java.util.Map;

public class Entry {

    public static void main(String[] args) {
        ExtensionLoader<PersonService> extensionLoader = ExtensionLoader.getExtensionLoader(PersonService.class);

        Map<String, String> map = new HashMap<>();
        map.put("aaaaa", "index");

        URL url = new URL("", "", 1, map);
        PersonService personService = extensionLoader.getExtension("man");
        personService.say(url);

        PersonService womanService = extensionLoader.getExtension("woman");
        womanService.say(url);
    }
}
