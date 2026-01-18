package mcd.listener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mcd.config.AppConfig;
import mcd.service.MinecraftService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class MinecraftServerStartListener extends AbstractListener {

    private static final Logger logger = LoggerFactory.getLogger(MinecraftServerStartListener.class);

    private static final String COMMAND = "서버시작";
    private static final String DESCRIPTION = "마인크래프트 서버를 시작합니다.";
    private static final List<OptionData> OPTIONS = List.of();

    private static final MinecraftServerStartListener instance = new MinecraftServerStartListener(COMMAND, DESCRIPTION, OPTIONS);

    public static MinecraftServerStartListener getInstance() {
        return instance;
    }

    private MinecraftServerStartListener(String command, String description, List<OptionData> options) {
        super(command, description, options);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!super.checkCommand(event)) return;

        MinecraftService minecraftService = AppConfig.getInstance().getMinecraftService();

        // 이미 실행 중인지 확인
        if (minecraftService.isRunning()) {
            event.reply("⚠️ 마인크래프트 서버가 이미 실행 중입니다.").setEphemeral(true).queue();
            return;
        }

        // 서버 시작 시도
        event.deferReply().queue();

        try {
            minecraftService.startServer();
            event.getHook().editOriginal("✅ 마인크래프트 서버가 시작되었습니다.").queue();
            logger.info("Minecraft server started by user: {}", event.getUser().getName());
        } catch (Exception e) {
            logger.error("Failed to start Minecraft server", e);
            event.getHook().editOriginal("❌ 마인크래프트 서버 시작에 실패했습니다: " + e.getMessage()).queue();
        }
    }
}
