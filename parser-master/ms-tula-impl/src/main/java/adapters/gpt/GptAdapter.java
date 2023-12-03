package adapters.gpt;


import adapters.gpt.dto.Message;
import adapters.gpt.dto.OpenAIRequest;
import adapters.gpt.dto.OpenAIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;
import domain.ports.GptPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import team.microchad.api.dto.LawsuitDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class GptAdapter implements GptPort {

  private final RestTemplate gptRestTemplate;
  private final OkHttpClient okHttpClient;
  private final ObjectMapper objectMapper;

  @Value("${openai.api-key}")
  private String apiKey;

  private static final String gptRequest = "Вот текст договора: %s Вот структура json. %s Тебе необходимо извлечь соответствующую информацию из договора и поместить в эту структуру. Больше ничего писать не нужно - верни только JSON";
  private static final String json = """
      {"plantiff":{"ogrn":"string","inn":"string","kpp":"string","oktmo":"string","bic":"string","INN":"string","OGRN":"string","address":"string","KPP":"string","paymentAccount":"string","correspondentAccount":"string","bank":"string","BIC":"string","OKTMO":"string","representative":"string"},"defendant":{"ogrn":"string","fio":"string","inn":"string","FIO":"string","birthdate":"2023-12-03","passport":"string","INN":"string","OGRN":"string","address":"string","title":"string"},"area":{"number":"string","area":0,"address":"string"},"contract":{"date":"2023-12-03","period":"string","paymentPoint":"string","number":"string","sum":0,"penaltyPoint":"string"},"pretension":{"overduePeriod":"string","debt":"string","peny":0,"penalty":0,"date":"2023-12-03","refuseDate":"2023-12-03","refuseCause":"string","unlawRefuseCase":"string","due":0},"agency":{"registeringGovermentAgency":"string","recordNumber":"string","registryDate":"2023-12-03"},"rules":{"isRentPeriod":true,"isPeny":true,"shouldBeRegistred":true,"isLegal":true,"hasPretentionAnswer":true}}
      """;  public LawsuitDto extractSomeData(String text) {
    LawsuitDto result = requestToGpt(text);
    log.info(result.toString());
    return result;
  }

  LawsuitDto requestToGpt(String text) {
    try {
      LawsuitDto lawsuitDtoDefault = objectMapper.readValue(json, LawsuitDto.class);
      var newJson = objectMapper.writeValueAsString(lawsuitDtoDefault);
      String formatted = gptRequest.formatted(text, newJson);
      var parsed = objectMapper.writeValueAsString(OpenAIRequest.builder()
          .model("gpt-3.5-turbo")
          .messages(List.of(Message.builder().role("user").content(formatted).build()))
          .build());

      RequestBody body = RequestBody.create(
          MediaType.parse("application/json"),
          parsed);
      Request request = new Request.Builder()
          .url("https://api.openai.com/v1/chat/completions")
          .method("POST", body)
          .addHeader("Content-Type", "application/json")
          .addHeader("Authorization", "Bearer " + apiKey)

          .build();
      String string = okHttpClient.newCall(request).execute().body().string();
      var gptResult = objectMapper.readValue(string,
          OpenAIResponse.class).getChoices().get(0).getMessage().getContent();
      return objectMapper.readValue(gptResult, LawsuitDto.class);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return null;
  }
}
