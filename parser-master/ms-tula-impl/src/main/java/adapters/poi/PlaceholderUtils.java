package adapters.poi;

import static adapters.poi.LawsuitGenerator.formatDate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import team.microchad.api.dto.GovermentAgencyDto;
import team.microchad.api.dto.LawsuitDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaceholderUtils {
  public static final String HAS_PERIOD = "Договор заключен на срок  %s.";

  public static final String HAS_NO_PERIOD = "Договор заключен на неопределенный срок.";

  public static final String SHOULD_NOT_BE_REGISTERED =
      "Таким образом, договор не подлежит государственной регистрации.";

  public static final String SHOULD_BE_REGISTERED =
      "Договор аренды был зарегистрирован %s, номер записи %s от %s.";

  public static final String HAS_PENY = "Пунктом %s о неустойке договора предусмотрена неустойка за просрочку внесения арендной платы в размере %.2f руб. Согласно прилагаемому к настоящему исковому заявлению расчету размер подлежащей уплате неустойки составляет %.2f руб.";

  public static final String HAS_NO_PENY = """
Неустойка за просрочку внесения арендной платы договором не установлена.\s
Согласно ст. 395 ГК РФ за пользование чужими денежными средствами вследствие их неправомерного удержания, уклонения от их возврата, иной просрочки в их уплате подлежат уплате проценты на сумму долга в размере ключевой ставкой Банка России, действовавшей в соответствующие периоды. Согласно прилагаемому к настоящему исковому заявлению расчету размер подлежащих уплате Ответчиком процентов за пользование чужими денежными средствами составляет %s руб.""";

  public static final String HAS_NO_REPLY = "Однако Ответчик оставил претензию без ответа и удовлетворения.";

  public static final String HAS_REPLY = "%s Истцом получен отказ на претензию, мотивированный тем, что %s. Истец считает отказ Ответчика неправомерным и необоснованным ввиду следующего %s.";

  public static final String BEGGING_HAS_NO_PENY = "2. Взыскать с Ответчика в пользу Истца проценты за пользование чужими денежными средствами в размере %.2f руб.";

  public static final String BEGGING_HAS_PENY = "2. Взыскать с Ответчика в пользу Истца предусмотренную договором неустойку в размере %.2f руб.";

  public static String getHasPeriod(LawsuitDto dto) {
    if (dto.getRules().getIsRentPeriod()) {
      return String.format(HAS_PERIOD, dto.getContract().getPeriod());
    }
    return HAS_NO_PERIOD;
  }

  public static String getShouldBeRegistered(LawsuitDto dto) {
    if (dto.getRules().getShouldBeRegistred()) {
      GovermentAgencyDto agency = dto.getAgency();
      return String.format(SHOULD_BE_REGISTERED, agency.getRegisteringGovermentAgency(),
          agency.getRecordNumber(),  formatDate(agency.getRegistryDate()));
    }
    return SHOULD_NOT_BE_REGISTERED;
  }

  public static String getHasPeny(LawsuitDto dto) {
    if (dto.getRules().getIsPeny()) {
      return String.format(HAS_PENY, dto.getContract().getPenaltyPoint(),
          dto.getPretension().getPeny(), dto.getPretension().getPeny()
              +  dto.getPretension().getDue());
    }
    return HAS_NO_PENY.formatted(dto.getPretension().getPenalty().toString());
  }

  public static String getHasReply(LawsuitDto dto) {
    if (dto.getRules().getHasPretentionAnswer()) {
      return String.format(HAS_REPLY, formatDate(dto.getPretension().getRefuseDate()),
          dto.getPretension().getRefuseCause(), dto.getPretension().getUnlawRefuseCase());
    }
    return HAS_NO_REPLY;
  }

  public static String getBegginingHasPeny(LawsuitDto dto) {
    if (dto.getRules().getIsPeny()) {
      return String.format(BEGGING_HAS_PENY, dto.getPretension().getPeny()
          + dto.getPretension().getDue());
    }
    return BEGGING_HAS_NO_PENY.formatted(dto.getPretension().getPenalty());
  }

}
