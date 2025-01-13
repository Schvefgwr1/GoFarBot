package com.example.gofarbot.config;


import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MainBotControllerConfig {
    private final HashMap<String, Short> SwitchTable = new HashMap<>() {{
       put("back_button", (short) 0);

       put("information", (short) 1);
       put("information_countries", (short) 2);
       put("information_austria", (short) 3);
       put("information_germany", (short) 4);
       put("information_italy", (short) 5);
       put("information_contacts", (short) 6);
       put("information_question", (short) 7);

       put("registration", (short) 8);
       put("show_repeat", (short) 9);

        put("consultation", (short) 10);

        put("contacts", (short) 11);

        put("guide", (short) 12);
        put("go_learn", (short) 13);
    }};

    public short getValue(String key) {
        return SwitchTable.get(key);
    }
}
