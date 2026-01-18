package mcd.config;

import java.util.ArrayList;
import java.util.List;

import mcd.dto.common.ListenerDTO;
import mcd.listener.AbstractListener;
import mcd.listener.ServerInfoListener;
import mcd.util.Env;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class DiscordConfig {

    private static final DiscordConfig INSTANCE;

    private final JDA JDA;

    static {
       try {
            INSTANCE = new DiscordConfig();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DiscordConfig() throws Exception {
        //===================================================listener 초기화
        List<AbstractListener> listeners = new ArrayList<>();
        listeners.add(ServerInfoListener.getInstance());
        //===================================================listener 초기화

        JDA jda = JDABuilder.createDefault(Env.DISCORD_TOKEN) // 토큰 교체
                // .setActivity(Activity.playing("/알림목록"))
                .addEventListeners(listeners.toArray())
                .build()
                .awaitReady();

        List<CommandData> commands = listeners.stream()
            .<CommandData>map(m -> {
                ListenerDTO info = m.getInfo();
                return Commands.slash(info.getCommand(), info.getDescription()).addOptions(info.getOptions());
            })
            .toList();

        jda.updateCommands().addCommands(commands).queue();

        this.JDA = jda;
    }

    public static DiscordConfig getInstance() {
        return INSTANCE;
    }

    public void shutdown() {
        if (JDA != null) {
            JDA.shutdown();
        }
    }
}
