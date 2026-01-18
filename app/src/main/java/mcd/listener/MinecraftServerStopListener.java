package mcd.listener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mcd.config.AppConfig;
import mcd.service.MinecraftService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class MinecraftServerStopListener extends AbstractListener {

    private static final Logger logger = LoggerFactory.getLogger(MinecraftServerStopListener.class);

    private static final String COMMAND = "서버종료";
    private static final String DESCRIPTION = "마인크래프트 서버를 종료합니다.";
    private static final List<OptionData> OPTIONS = List.of();

    private static final MinecraftServerStopListener instance = new MinecraftServerStopListener(COMMAND, DESCRIPTION, OPTIONS);

    public static MinecraftServerStopListener getInstance() {
        return instance;
    }

    private MinecraftServerStopListener(String command, String description, List<OptionData> options) {
        super(command, description, options);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!super.checkCommand(event)) return;

        MinecraftService minecraftService = AppConfig.getInstance().getMinecraftService();

        // 실행 중인지 확인
        if (!minecraftService.isRunning()) {
            event.reply("⚠️ 마인크래프트 서버가 실행 중이지 않습니다.").setEphemeral(true).queue();
            return;
        }

        // 서버 종료 시도
        event.deferReply(true).queue();

        try {
            minecraftService.stopServer();
            event.getHook().editOriginal("✅ 마인크래프트 서버가 종료되었습니다.").queue();
            logger.info("Minecraft server stopped by user: {}", event.getUser().getName());
        } catch (Exception e) {
            logger.error("Failed to stop Minecraft server", e);
            event.getHook().editOriginal("❌ 마인크래프트 서버 종료에 실패했습니다: " + e.getMessage()).queue();
        }
    }
}
