package mcd.listener;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class ServerInfoListener extends AbstractListener {

    private static final String COMMAND = "정보";
    private static final String DESCRIPTION = "봇 상태 정보를 조회합니다.";
    private static final List<OptionData> OPTIONS = List.of();

    private static final ServerInfoListener instance = new ServerInfoListener(COMMAND, DESCRIPTION, OPTIONS);

    public static ServerInfoListener getInstance() {
        return instance;
    }
    
    private ServerInfoListener(String command, String description, List<OptionData> options) {
        super(command, description, options);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!super.checkCommand(event)) return;

        JDA jda = event.getJDA();
        Runtime runtime = Runtime.getRuntime();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        
        // 메모리 정보
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        long usedMemory = totalMemory - freeMemory;
        
        // CPU 사용량
        double cpuUsage = osBean.getSystemLoadAverage();
        String cpuDisplay = cpuUsage < 0 
            ? "사용 불가" 
            : String.format("%.2f%%", cpuUsage * 100 / osBean.getAvailableProcessors());
        
        // 시스템 정보
        String javaVersion = System.getProperty("java.version");
        
        // 네트워크 정보
        long gatewayPing = jda.getGatewayPing();
        long restPing = jda.getRestPing().complete();
        
        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("봇 상태 정보")
            .setColor(0x00FF00)
            .addField("💾 메모리 사용량", 
                String.format("%d MB / %d MB (%.1f%%)", 
                    usedMemory, maxMemory, (usedMemory * 100.0 / maxMemory)), true)
            .addField("🔥 CPU 사용량", cpuDisplay, true)
            .addField("🌐 Gateway Ping", gatewayPing + " ms", true)
            .addField("📡 REST API Ping", restPing + " ms", true)
            .addField("☕ Java 버전", javaVersion, true)
            .setFooter("JDA " + JDAInfo.VERSION)
            .setTimestamp(java.time.Instant.now());
        
        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }
}
