package com.example.gofarbot.services.bot_services;

import com.example.gofarbot.exceptions.MessageException;
import com.example.gofarbot.services.bot_services.dto.MessageServiceDTO;

@FunctionalInterface
public interface MessageSupplier {
    MessageServiceDTO get() throws MessageException;
}

