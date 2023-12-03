package domain.operations;


import domain.ports.LawsuitPort;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team.microchad.api.dto.LawsuitDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateLawsuitOperation {
  private final LawsuitPort lawsuitPort;

  public File execute(LawsuitDto dto) {
    log.info("Generate Lawsuit: {}", dto);
    return lawsuitPort.generateLawsuit(dto);
  }

}
