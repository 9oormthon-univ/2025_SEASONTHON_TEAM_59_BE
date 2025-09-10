package com.leafup.leafupbackend.global.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leafup.leafupbackend.member.api.dto.response.DailyChallengesResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyChallengeCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public Optional<DailyChallengesResDto> getCachedChallenges(String email, LocalDate date, int stage) {
        String cacheKey = buildCacheKey(email, date, stage);
        String cachedData = redisTemplate.opsForValue().get(cacheKey);

        if (cachedData != null) {
            try {
                return Optional.of(objectMapper.readValue(cachedData, DailyChallengesResDto.class));
            } catch (JsonProcessingException e) {
                log.error("Failed to parse cached data for key: {}", cacheKey, e);
            }
        }

        return Optional.empty();
    }

    public void cacheChallenges(String email, LocalDate date, int stage, DailyChallengesResDto data) {
        String cacheKey = buildCacheKey(email, date, stage);

        try {
            String jsonData = objectMapper.writeValueAsString(data);
            long ttlSeconds = Duration.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay()).getSeconds();
            redisTemplate.opsForValue().set(cacheKey, jsonData, Duration.ofSeconds(ttlSeconds));
        } catch (JsonProcessingException e) {
            log.error("Failed to cache data for key: {}", cacheKey, e);
        }
    }

    public void deleteDailyChallengeCache(String email, LocalDate date, int stage) {
        String cacheKey = buildCacheKey(email, date, stage);
        redisTemplate.delete(cacheKey);
    }

    private String buildCacheKey(String email, LocalDate date, int stage) {
        return "daily-challenges:" + email + ":" + date + ":" + stage;
    }
    
}
