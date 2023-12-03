package adapters.gpt.dto;

import java.util.List;
import lombok.Data;

@Data
public class OpenAIResponse {

  private String id;
  private String object;
  private long created;

  private List<Choice> choices;

}
