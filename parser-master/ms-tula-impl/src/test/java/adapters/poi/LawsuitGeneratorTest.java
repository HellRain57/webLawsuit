package adapters.poi;


import java.time.LocalDate;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import team.microchad.api.dto.AreaDto;
import team.microchad.api.dto.ContractDto;
import team.microchad.api.dto.DefendantDto;
import team.microchad.api.dto.GovermentAgencyDto;
import team.microchad.api.dto.LawsuitDto;
import team.microchad.api.dto.PlaintiffDto;
import team.microchad.api.dto.PretensionDto;
import team.microchad.api.dto.RulesDto;

class LawsuitGeneratorTest {

  static Faker faker = new Faker();
  @Test
  void shouldLoadFile() {
    LawsuitGenerator generator = new LawsuitGenerator();
    LawsuitDto lawsuitDto = getLawsuit();
    generator.generateLawsuit(lawsuitDto);
  }

  private static LawsuitDto getLawsuit() {
    return new LawsuitDto()
        .rules(new RulesDto()
            .isLegal(true)
            .isPeny(true)
            .hasPretentionAnswer(true)
            .isRentPeriod(false)
            .shouldBeRegistred(true)
        )
        .contract(new ContractDto()
            .date(LocalDate.now())
            .number(String.valueOf(faker.number().numberBetween(1000, 2000)))
            .sum((float) faker.number().randomDouble(2, 1000, 2000))
            .penaltyPoint("2.1")
            .paymentPoint("2.2")
            .period("3 месяца")
        )
        .defendant(new DefendantDto()
            .INN(faker.idNumber().ssnValid())
            .birthdate(LocalDate.now())
            .title(faker.name().title())
            .FIO(faker.name().fullName())
            .OGRN(faker.idNumber().ssnValid())
            .address(faker.address().fullAddress())
            .passport(faker.idNumber().ssnValid())
        )
        .agency(new GovermentAgencyDto()
            .registryDate(LocalDate.now())
            .recordNumber(faker.idNumber().ssnValid())
            .registeringGovermentAgency("Тульский МФЦ"))
        .pretension(new PretensionDto()
            .date(LocalDate.now())
            .due((float) faker.number().numberBetween(1000, 2000))
            .debt("1200 рублей")
            .peny((float) faker.number().numberBetween(1000, 2000))
            .overduePeriod("3 месяца")
            .refuseCause("истец проиграл все деньги в покер")
            .refuseDate(LocalDate.now())
            .unlawRefuseCase("проигрыш в покер не является основанием для неуплаты")
            .penalty((float) faker.number().numberBetween(1000, 2000)))
        .plantiff(new PlaintiffDto()
            .INN(faker.idNumber().ssnValid())
            .OGRN(faker.idNumber().ssnValid())
            .address(faker.address().fullAddress())
            .BIC(faker.idNumber().ssnValid())
            .bank(faker.company().name())
            .correspondentAccount(faker.idNumber().ssnValid())
            .KPP(faker.idNumber().ssnValid())
            .OKTMO(faker.idNumber().ssnValid())
            .paymentAccount(faker.idNumber().ssnValid())
            .representative(faker.name().fullName()))
        .area(new AreaDto()
            .area((float) faker.number().numberBetween(1000, 2000))
            .address(faker.address().fullAddress())
            .number(faker.idNumber().peselNumber()));
  }
}