package com.example.gofarbot.data;

import com.example.gofarbot.models.MessageView;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface MessageViewRepository extends CrudRepository<MessageView, Long> {
    
    /**
     * Находит просмотр сообщения конкретным пользователем
     */
    Optional<MessageView> findByMessageIdAndUserChatId(Long messageId, Long userChatId);
    
    /**
     * Проверяет, существует ли просмотр сообщения пользователем
     */
    boolean existsByMessageIdAndUserChatId(Long messageId, Long userChatId);
    
    /**
     * Получает количество просмотров сообщения конкретным пользователем
     */
    @Query("SELECT mv.viewCount FROM MessageView mv WHERE mv.message.id = :messageId AND mv.userChatId = :userChatId")
    Optional<Integer> getViewCountByMessageIdAndUserChatId(@Param("messageId") Long messageId, @Param("userChatId") Long userChatId);
    
    /**
     * Увеличивает счетчик просмотров для существующего просмотра
     */
    @Modifying
    @Transactional
    @Query("UPDATE MessageView mv SET mv.viewCount = mv.viewCount + 1, mv.lastViewedAt = CURRENT_TIMESTAMP WHERE mv.message.id = :messageId AND mv.userChatId = :userChatId")
    int incrementViewCount(@Param("messageId") Long messageId, @Param("userChatId") Long userChatId);
    
    /**
     * Получает общее количество просмотров сообщения
     */
    @Query("SELECT SUM(mv.viewCount) FROM MessageView mv WHERE mv.message.id = :messageId")
    Optional<Long> getTotalViewsByMessageId(@Param("messageId") Long messageId);
    
    /**
     * Получает количество уникальных пользователей, просмотревших сообщение
     */
    @Query("SELECT COUNT(DISTINCT mv.userChatId) FROM MessageView mv WHERE mv.message.id = :messageId")
    Long getUniqueViewersByMessageId(@Param("messageId") Long messageId);
} 