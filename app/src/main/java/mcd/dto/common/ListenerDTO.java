package mcd.dto.common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * listener 정보 DTO
 */
@Data
@AllArgsConstructor
public class ListenerDTO {
    private String command;
    private String description;
    private List<OptionData> options;
}
