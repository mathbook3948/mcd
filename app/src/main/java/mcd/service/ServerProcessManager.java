package mcd.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mcd.config.MinecraftConfig;

/**
 * 마인크래프트 서버 프로세스 관리 클래스
 * 실제 프로세스 실행, 모니터링, 종료를 담당합니다.
 */
public class ServerProcessManager {

    private static final Logger logger = LoggerFactory.getLogger(ServerProcessManager.class);

    private final MinecraftConfig config;
    private Process serverProcess;
    private PrintWriter processInput;
    private final List<Consumer<String>> outputListeners = new ArrayList<>();

    public ServerProcessManager(MinecraftConfig config) {
        this.config = config;
    }

    /**
     * 마인크래프트 서버를 시작합니다.
     */
    public void startServer() throws IOException {
        if (isRunning()) {
            logger.warn("Server is already running");
            return;
        }

        logger.info("Starting Minecraft server...");

        // 명령어 구성
        List<String> command = buildCommand();
        logger.info("Command: {}", String.join(" ", command));

        // 프로세스 빌더 설정
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(config.getServerDirectory()));
        processBuilder.redirectErrorStream(true);

        // 프로세스 시작
        serverProcess = processBuilder.start();

        // 입력 스트림 설정
        processInput = new PrintWriter(
            new OutputStreamWriter(serverProcess.getOutputStream(), StandardCharsets.UTF_8),
            true
        );

        // 출력 스트림 모니터링 (비동기)
        CompletableFuture.runAsync(this::monitorOutput);

        logger.info("Minecraft server process started");
    }

    /**
     * 서버 명령어를 빌드합니다.
     * 크로스 플랫폼 지원
     */
    private List<String> buildCommand() {
        List<String> command = new ArrayList<>();
        command.add(config.getJavaExecutable());
        command.add("-Xms" + config.getMinMemory());
        command.add("-Xmx" + config.getMaxMemory());
        command.add("-jar");
        command.add(config.getFullJarPath());
        command.add("nogui");
        return command;
    }

    /**
     * 서버 콘솔 출력을 모니터링합니다.
     */
    private void monitorOutput() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(serverProcess.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String finalLine = line;
                logger.info("[MC] {}", finalLine);
                // 리스너에게 알림
                outputListeners.forEach(listener -> listener.accept(finalLine));
            }
        } catch (IOException e) {
            logger.error("Error reading server output", e);
        }
    }

    /**
     * 서버에 명령어를 전송합니다.
     */
    public void sendCommand(String command) {
        if (!isRunning()) {
            logger.warn("Cannot send command: server is not running");
            return;
        }

        logger.info("Sending command to server: {}", command);
        processInput.println(command);
    }

    /**
     * 서버를 정상 종료합니다.
     */
    public void stopServer() {
        if (!isRunning()) {
            logger.warn("Server is not running");
            return;
        }

        logger.info("Stopping Minecraft server gracefully...");
        sendCommand("stop");

        try {
            // 최대 30초 대기
            boolean exited = serverProcess.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
            if (!exited) {
                logger.warn("Server did not stop gracefully, forcing shutdown...");
                killProcess();
            } else {
                logger.info("Minecraft server stopped successfully");
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted while waiting for server to stop", e);
            Thread.currentThread().interrupt();
            killProcess();
        } finally {
            cleanup();
        }
    }

    /**
     * 프로세스를 강제 종료합니다.
     */
    public void killProcess() {
        if (serverProcess != null && serverProcess.isAlive()) {
            logger.warn("Force killing server process");
            serverProcess.destroyForcibly();
        }
        cleanup();
    }

    /**
     * 리소스를 정리합니다.
     */
    private void cleanup() {
        if (processInput != null) {
            processInput.close();
            processInput = null;
        }
        serverProcess = null;
    }

    /**
     * 서버가 실행 중인지 확인합니다.
     */
    public boolean isRunning() {
        return serverProcess != null && serverProcess.isAlive();
    }

    /**
     * 콘솔 출력 리스너를 추가합니다.
     */
    public void addOutputListener(Consumer<String> listener) {
        outputListeners.add(listener);
    }

    /**
     * 콘솔 출력 리스너를 제거합니다.
     */
    public void removeOutputListener(Consumer<String> listener) {
        outputListeners.remove(listener);
    }
}
