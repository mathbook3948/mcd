package mcd.config;

import lombok.Getter;
import mcd.exception.ConfigurationException;

/**
 * 환경 변수 관리 및 검증 클래스
 */
public class EnvConfig {

    @Getter
    private final String discordToken;

    @Getter
    private final String minMemory;
    
    @Getter
    private final String maxMemory;
    
    @Getter
    private final String minecraftHome;

    @Getter
    private final String jarFileName;

    @Getter
    private final String javaHome;

    public EnvConfig() {
        this.discordToken = getRequiredEnv("DISCORD_TOKEN");
        this.minMemory = getRequiredEnv("MIN_MEMORY");
        this.maxMemory = getRequiredEnv("MAX_MEMORY");
        this.minecraftHome = getRequiredEnv("MINECRAFT_HOME");
        this.jarFileName = getOptionalEnv("JAR_FILE_NAME", "server.jar");
        this.javaHome = getRequiredEnv("JAVA_HOME");
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
}
