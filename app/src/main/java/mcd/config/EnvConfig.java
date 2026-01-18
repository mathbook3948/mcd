package mcd.config;

import mcd.exception.ConfigurationException;

/**
 * 환경 변수 관리 및 검증 클래스
 */
public class EnvConfig {

    private final String discordToken;

    public EnvConfig() {
        this.discordToken = getRequiredEnv("DISCORD_TOKEN");
    }

    /**
     * 필수 환경 변수를 가져옵니다. 없으면 예외를 던집니다.
     */
    private String getRequiredEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new ConfigurationException(
                String.format("Required environment variable '%s' is not set", key)
            );
        }
        return value;
    }

    /**
     * 선택적 환경 변수를 가져옵니다.
     */
    private String getOptionalEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    public String getDiscordToken() {
        return discordToken;
    }
}
