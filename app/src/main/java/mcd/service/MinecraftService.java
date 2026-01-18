package mcd.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import mcd.config.MinecraftConfig;

/**
 * 마인크래프트 서버 관리 서비스
 * 서버의 시작, 정지, 재시작 등 비즈니스 로직을 담당합니다.
 */
public class MinecraftService {

    private static final Logger logger = LoggerFactory.getLogger(MinecraftService.class);
    
    private final MinecraftConfig config;
    private final ServerProcessManager processManager;

    public MinecraftService(MinecraftConfig config) {
        this.config = config;
        this.processManager = new ServerProcessManager(config);
        logger.info("MinecraftService initialized");
    }

    /**
     * 서버를 시작합니다.
     */
    public void startServer() {
        try {
            logger.info("Attempting to start Minecraft server...");
            processManager.startServer();
            logger.info("Minecraft server started successfully");
        } catch (IOException e) {
            logger.error("Failed to start Minecraft server", e);
            throw new RuntimeException("Failed to start Minecraft server", e);
        }
    }

    /**
     * 서버를 정지합니다.
     */
    public void stopServer() {
        logger.info("Attempting to stop Minecraft server...");
        processManager.stopServer();
    }

    /**
     * 서버를 재시작합니다.
     */
    public void restartServer() {
        logger.info("Restarting Minecraft server...");
        stopServer();

        // 서버가 완전히 종료될 때까지 대기
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error("Interrupted while waiting for server to stop", e);
            Thread.currentThread().interrupt();
        }

        startServer();
    }

    /**
     * 서버 상태를 확인합니다.
     */
    public boolean isRunning() {
        return processManager.isRunning();
    }

    /**
     * 서버에 명령어를 전송합니다.
     */
    public void sendCommand(String command) {
        processManager.sendCommand(command);
    }

    /**
     * 서버 상태 정보를 반환합니다.
     */
    public ServerStatus getStatus() {
        return new ServerStatus(
            processManager.isRunning(),
            config.getServerDirectory(),
            config.getFullJarPath()
        );
    }

    /**
     * 서비스 종료 시 정리 작업을 수행합니다.
     */
    public void shutdown() {
        logger.info("Shutting down MinecraftService...");
        if (processManager.isRunning()) {
            stopServer();
        }
        logger.info("MinecraftService shutdown completed");
    }

    /**
     * 서버 상태 정보 클래스
     */
    @Getter
    public static class ServerStatus {
        private final boolean running;
        private final String serverPath;
        private final String jarPath;

        public ServerStatus(boolean running, String serverPath, String jarPath) {
            this.running = running;
            this.serverPath = serverPath;
            this.jarPath = jarPath;
        }
    }
}
