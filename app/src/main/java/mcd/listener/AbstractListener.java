package mcd.listener;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

import mcd.dto.common.ListenerDTO;

public abstract class AbstractListener extends ListenerAdapter {

    private final String COMMAND;

    private final String DESCRIPTION;

    private List<OptionData> OPTIONS;

    protected AbstractListener(String command, String description, List<OptionData> options)  {
        this.COMMAND = command;
        this.DESCRIPTION = description;
        this.OPTIONS = options;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {}
    
    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {}

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {}

    /**
     * 현재 처리할 event인지 검사
     */
    protected boolean checkCommand(CommandInteraction event) {
        return COMMAND.equals(event.getName());
    }

    /**
     * 현재 명령 정보 조회
     */
    public ListenerDTO getInfo() {
        return new ListenerDTO(COMMAND, DESCRIPTION, OPTIONS);
    }
}