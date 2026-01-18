package mcd.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mcd.exception.ConfigurationException;
import mcd.service.MinecraftService;

/**
 * 애플리케이션 전체 설정 관리 클래스 (Singleton)
 * 모든 설정의 초기화 순서와 라이프사이클을 중앙에서 관리합니다.
 */
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static AppConfig instance;

    private final EnvConfig envConfig;
    private final MinecraftConfig minecraftConfig;
    private final MinecraftService minecraftService;
    private final DiscordConfig discordConfig;

    private AppConfig() {
        try {
            logger.info("Starting application configuration...");

            // 1. 환경 변수 검증 및 로드
            logger.info("Loading environment variables...");
            this.envConfig = new EnvConfig();
            logger.info("Environment variables loaded successfully");

            // 2. Minecraft 서버 설정 및 서비스 초기화
            logger.info("Initializing Minecraft server configuration...");
            this.minecraftConfig = new MinecraftConfig(envConfig);
            this.minecraftService = new MinecraftService(minecraftConfig);
            logger.info("Minecraft server service initialized successfully");

            // 3. Discord 봇 초기화
            logger.info("Initializing Discord bot...");
            this.discordConfig = new DiscordConfig(envConfig);
            logger.info("Discord bot initialized successfully");

            logger.info("Application configuration completed");
        } catch (Exception e) {
            logger.error("Failed to initialize application configuration", e);
            throw new ConfigurationException("Application initialization failed", e);
        }
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    /**
     * 애플리케이션 종료 시 모든 리소스를 정리합니다.
     */
    public void shutdown() {
        logger.info("Shutting down application...");

        // Minecraft 서버 종료
        if (minecraftService != null) {
            logger.info("Shutting down Minecraft server...");
            minecraftService.shutdown();
        }

        // Discord 봇 종료
        if (discordConfig != null) {
            logger.info("Shutting down Discord bot...");
            discordConfig.shutdown();
        }

        logger.info("Application shutdown completed");
    }

    public EnvConfig getEnvConfig() {
        return envConfig;
    }

    public MinecraftConfig getMinecraftConfig() {
        return minecraftConfig;
    }

    public MinecraftService getMinecraftService() {
        return minecraftService;
    }

    public DiscordConfig getDiscordConfig() {
        return discordConfig;
    }
}
