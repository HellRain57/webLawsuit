package domain.ports;

import java.io.File;
import team.microchad.api.dto.LawsuitDto;

public interface LawsuitPort {
  File generateLawsuit(LawsuitDto dto);
}
