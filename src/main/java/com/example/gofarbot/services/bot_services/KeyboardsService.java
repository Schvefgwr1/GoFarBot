package com.example.gofarbot.services.bot_services;


import com.example.gofarbot.models.DialogState;
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
    public InlineKeyboardMarkup getKeyboard(DialogState.DialogStates dialogStateE, int numberMessage) {
        switch (dialogStateE) {
            case FIRST_MESSAGE:
                switch(numberMessage) {
                    case 1:
                        return getStartMessageKeyboardMarkup();
                    case 2:
                        return getStartMessage2KeyboardMarkup();
                }
            case INFORMATION:
                switch (numberMessage) {
                    case 1:
                        return getInformation1KeyboardMarkup();
                    case 2:
                        return getInformation2KeyboardMarkup();
                    case 3, 4, 5:
                    default: return null;
                }
            default: return null;
//            case REGISTRATION:
//            case GO_LEARN:
//            case CONSULTATION:
//            case CONTACTS:
        }
    }

    private InlineKeyboardMarkup getStartMessage2KeyboardMarkup() {
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text("Больше о поступлении в Европу")
                .callbackData("information")
                .build();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(new ArrayList<>(Collections.singletonList(button)));

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    private InlineKeyboardMarkup getInformation2KeyboardMarkup() {
        InlineKeyboardButton button1 = InlineKeyboardButton.builder()
                .text("Австрия")
                .callbackData("information_austria")
                .build();
        InlineKeyboardButton button2 = InlineKeyboardButton.builder()
                .text("Германия")
                .callbackData("information_germany")
                .build();
        InlineKeyboardButton button3 = InlineKeyboardButton.builder()
                .text("Италия")
                .callbackData("information_italy")
                .build();
        InlineKeyboardButton button4 = InlineKeyboardButton.builder()
                .text("Назад")
                .callbackData("back_button")
                .build();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(new ArrayList<>(Collections.singletonList(button1)));
        keyboard.add(new ArrayList<>(Collections.singletonList(button2)));
        keyboard.add(new ArrayList<>(Collections.singletonList(button3)));
        keyboard.add(new ArrayList<>(Collections.singletonList(button4)));

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    private InlineKeyboardMarkup getInformation1KeyboardMarkup() {
        InlineKeyboardButton button1 = InlineKeyboardButton.builder()
                .text("Гайд по странам")
                .callbackData("information_countries")
                .build();
        InlineKeyboardButton button2 = InlineKeyboardButton.builder()
                .text("Наши соц. сети")
                .callbackData("information_contacts")
                .build();
        InlineKeyboardButton button3 = InlineKeyboardButton.builder()
                .text("Задать вопрос")
                .callbackData("information_question")
                .build();
        InlineKeyboardButton button4 = InlineKeyboardButton.builder()
                .text("Назад")
                .callbackData("back_button")
                .build();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(new ArrayList<>(Collections.singletonList(button1)));
        keyboard.add(new ArrayList<>(Collections.singletonList(button2)));
        keyboard.add(new ArrayList<>(Collections.singletonList(button3)));
        keyboard.add(new ArrayList<>(Collections.singletonList(button4)));

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    private InlineKeyboardMarkup getStartMessageKeyboardMarkup() {
        // Создаем кнопку
        InlineKeyboardButton button1 = InlineKeyboardButton.builder()
                .text("Записи вебинаров")
                .callbackData("registration")
                .build();
//        InlineKeyboardButton button2 = InlineKeyboardButton.builder()
//                .text("Больше о поступлении в Европу")
//                .callbackData("information")
//                .build();
        InlineKeyboardButton button2 = InlineKeyboardButton.builder()
                .text("Хочу записаться на бесплатную консультацию")
                .callbackData("consultation")
                .build();
//        InlineKeyboardButton button4 = InlineKeyboardButton.builder()
//                .text("Подготовка к обучению с GoLearn")
//                .callbackData("go_learn")
//                .build();
        InlineKeyboardButton button5 = InlineKeyboardButton.builder()
                .text("Наши контакты")
                .callbackData("contacts")
                .build();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(new ArrayList<>(Collections.singletonList(button1)));
        keyboard.add(new ArrayList<>(Collections.singletonList(button2)));
//        keyboard.add(new ArrayList<>(Collections.singletonList(button3)));
//        keyboard.add(new ArrayList<>(Collections.singletonList(button4)));
        keyboard.add(new ArrayList<>(Collections.singletonList(button5)));

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    public InlineKeyboardMarkup getLastNotificationKeyboardMarkup() {
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text("Посмотреть повтор")
                .callbackData("show_repeat")
                .build();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(new ArrayList<>(Collections.singletonList(button)));
        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }
}
