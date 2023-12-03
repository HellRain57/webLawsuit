package application.controller;

import domain.operations.GenerateLawsuitOperation;
import domain.operations.ParseContractOperation;
import java.net.URL;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team.microchad.api.dto.DefendantDto;
import team.microchad.api.dto.GovermentAgencyDto;
import team.microchad.api.dto.LawsuitDto;

import team.microchad.api.dto.PlaintiffDto;

@RestController
@RequiredArgsConstructor
public class DocumentController {

  final ParseContractOperation parseContract;
  final GenerateLawsuitOperation generateLawsuitOperation;

  @RequestMapping(
      method = RequestMethod.POST,
      value = "/api/v1/lawsuit", produces = "application/octet-stream")
  public ResponseEntity<Resource> generateDocument(@RequestBody LawsuitDto lawsuitDto) {
    return ResponseEntity.ok(
        new FileSystemResource(
            generateLawsuitOperation.execute(lawsuitDto)
        )
    );
  }

  @SneakyThrows
  @RequestMapping(value = "/api/v1/claim",
      method = RequestMethod.POST)
  public ResponseEntity<LawsuitDto> postClaim(MultipartFile file) {
    return ResponseEntity.ok(
        parseContract.execute(file.getInputStream())
    );
  }


  @SneakyThrows
  @RequestMapping(value = "/api/v1/contract",
      method = RequestMethod.POST)
  public ResponseEntity<LawsuitDto> postContract(MultipartFile file) {
    return ResponseEntity.ok().build();
  }
}
