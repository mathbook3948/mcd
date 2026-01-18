package mcd.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mcd.dto.common.ListenerDTO;
import mcd.exception.ConfigurationException;
import mcd.listener.AbstractListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * Discord 봇 설정 및 초기화 관리 클래스
 * JDA 인스턴스 생성과 라이프사이클을 관리합니다.
 */
public class DiscordConfig {

    private static final Logger logger = LoggerFactory.getLogger(DiscordConfig.class);

    private final JDA jda;

    public DiscordConfig(EnvConfig envConfig) {
        try {
            // 등록된 리스너 가져오기
            List<AbstractListener> listeners = ListenerRegistry.getListeners();
            logger.info("Loaded {} listeners", listeners.size());

            // JDA 초기화
            JDA jda = JDABuilder.createDefault(envConfig.getDiscordToken())
                    // .setActivity(Activity.playing("/알림목록"))
                    .addEventListeners(listeners.toArray())
                    .build()
                    .awaitReady();

            logger.info("Discord bot connected successfully");

            // 슬래시 커맨드 등록
            List<CommandData> commands = listeners.stream()
            .<CommandData>map(listener -> {
                ListenerDTO info = listener.getInfo();
                return Commands.slash(info.getCommand(), info.getDescription())
                    .addOptions(info.getOptions());
            })
            .toList();

            jda.updateCommands().addCommands(commands).queue();

            this.jda = jda;
        } catch(Exception e) {
            throw new ConfigurationException("Error in DiscordConfig initialization", e);
        }
    }

    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
    }
}
