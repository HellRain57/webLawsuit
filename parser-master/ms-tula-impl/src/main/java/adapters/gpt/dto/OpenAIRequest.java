package adapters.gpt.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenAIRequest {

  private String model;
  private List<Message> messages;

}
