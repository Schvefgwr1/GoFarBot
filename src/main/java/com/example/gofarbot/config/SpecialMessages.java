package com.example.gofarbot.config;

import com.example.gofarbot.data.MessageRepository;
import com.example.gofarbot.models.Message;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class SpecialMessages {
    private final MessageRepository messageRepository;

    @Autowired
    public SpecialMessages(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Getter
    private String unsupportedCommandMessage;

    @Getter
    private String errorMessage;

    @PostConstruct
    public void onMontageComponent() {
        Optional<Message> container = messageRepository.findByCode("unsupported");
        this.unsupportedCommandMessage = container
                .map(Message::getText)
                .orElseGet(() -> {
                    log.warn("Don't have 'unsupported' special message in DB, use standard variant");
                    return "Неправильная команда. Введите /start .";
                })
        ;

        container = messageRepository.findByCode("error");
        this.errorMessage = container
                .map(Message::getText)
                .orElseGet(() -> {
                    log.warn("Don't have 'error' special message in DB, use standard variant");
                    return """
                    Дорогой друг! В боте возникли техническое проблемы.
                    Мы уже исправляем ситуацию. Попробуй через
                    некоторое время еще раз ввести команду /start .
                
                    Если это не поможет, обратись к команде GoFar в соц. сетях! @gofar_ru
                    Спасибо, что остаешься с нами!
                    """;
                })
        ;
    }
}
