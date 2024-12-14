package com.example.gofarbot.services.bot_services;


import com.example.gofarbot.models.Button;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class KeyboardsService {
    public InlineKeyboardMarkup getKeyboard(List<Button> buttons) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for(Button button: buttons) {
            InlineKeyboardButton buttonForKeyboard = InlineKeyboardButton.builder()
                    .text(button.getText())
                    .callbackData(button.getCode())
                    .build();
            keyboard.add(new ArrayList<>(Collections.singletonList(buttonForKeyboard)));
        }
        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }
}
