package mcd.config;

import java.io.File;

import lombok.Getter;

/**
 * 마인크래프트 서버 설정 클래스
 * Windows/Linux 크로스 플랫폼 지원
 */
@Getter
public class MinecraftConfig {

    private final String minecraftJarPath;
    private final String javaHome;
    private final String minMemory;
    private final String maxMemory;
    private final String jarFileName;
    private final boolean isWindows;

    public MinecraftConfig(EnvConfig envConfig) {
        this.minecraftJarPath = envConfig.getMinecraftHome();
        this.javaHome = envConfig.getJavaHome();
        this.minMemory = envConfig.getMinMemory();
        this.maxMemory = envConfig.getMaxMemory();
        this.jarFileName = envConfig.getJarFileName();
        this.isWindows = System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * 전체 JAR 파일 경로를 반환합니다.
     */
    public String getFullJarPath() {
        return minecraftJarPath + File.separator + jarFileName;
    }

    /**
     * 서버가 위치한 디렉토리 경로를 반환합니다.
     */
    public String getServerDirectory() {
        return minecraftJarPath;
    }

    /**
     * Java 실행 파일 경로를 반환합니다.
     */
    public String getJavaExecutable() {
        String executable = isWindows ? "java.exe" : "java";
        return javaHome + File.separator + "bin" + File.separator + executable;
    }
}
