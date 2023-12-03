package adapters.gpt.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Choice {
  private Message message;
  private Long index;
  private String finishReason;
}
