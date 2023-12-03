package domain.ports;

import team.microchad.api.dto.LawsuitDto;

public interface GptPort {
  LawsuitDto extractSomeData(String text);
}
