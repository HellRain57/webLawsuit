package adapters.poi;

import static adapters.poi.PlaceholderUtils.*;

import domain.ports.LawsuitPort;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.xmlbeans.XmlCursor;
import org.springframework.stereotype.Service;
import team.microchad.api.dto.LawsuitDto;

@Slf4j
@Service
public class LawsuitGenerator implements LawsuitPort {

  private static final String TEMPLATE = "template/";

  private static final String HEADER_LEGAL = "header_legal_template.docx";

  private static final String HEADER_NATURAL = "header_natural_template.docx";

  private static final String MAIN = "main_template.docx";


  @Override
  @SneakyThrows
  public File generateLawsuit(LawsuitDto dto) {
    var mainTemplateDoc = getTemplateDoc(TEMPLATE + MAIN);
    XWPFDocument headerTemplateDoc = getTemplateDoc(TEMPLATE + HEADER_NATURAL);
    var rules = dto.getRules();

    insertHeader(mainTemplateDoc, headerTemplateDoc, dto);

    replaceBodyPlaceholders(dto, mainTemplateDoc);

    replaceParagraphPlaceholders(dto, mainTemplateDoc);

    var outFileNameString = "file.docx";

    var file = File.createTempFile("lawsuit", ".docx");
    var outputStream = new FileOutputStream(file);
    mainTemplateDoc.write(outputStream);
    return file;
  }

  private void replaceParagraphPlaceholders(LawsuitDto dto, XWPFDocument mainTemplateDoc) {
    mainTemplateDoc.getParagraphs().forEach(
        xwpfParagraph -> {
          xwpfParagraph.getRuns().forEach(
              xwpfRun -> {
                var text = xwpfRun.getText(0);
                replace(xwpfRun, text, "PERIOD", getHasPeriod(dto));
                replace(xwpfRun, text, "REGISTRATION",
                    getShouldBeRegistered(dto));
                replace(xwpfRun, text, "PENY", getHasPeny(dto));
                replace(xwpfRun, text, "PRETENSION", getHasReply(dto));
                replace(xwpfRun, text, "BEGGING", getBegginingHasPeny(dto));
              }
          );
        }
    );
  }

  private static void replaceBodyPlaceholders(LawsuitDto dto, XWPFDocument mainTemplateDoc) {
    mainTemplateDoc.getParagraphs().forEach(
        xwpfParagraph -> {
          xwpfParagraph.getRuns().forEach(
              xwpfRun -> {
                var text = xwpfRun.getText(0);
                headerReplacements(dto, xwpfRun, text);
                bodyReplacements(dto, xwpfRun, text);
              }
          );
        }
    );
  }


  private void insertHeader(XWPFDocument mainTemplateDoc, XWPFDocument headerTemplateDoc,
      LawsuitDto dto) throws IOException {
    var rules = dto.getRules();
    if (rules.getIsLegal()) {
      headerTemplateDoc = getTemplateDoc(TEMPLATE + HEADER_LEGAL);
    }

    XWPFTable headerTable = headerTemplateDoc.getTableArray(0);
    headerTable.getRows().forEach(
        xwpfTableRow -> {
          xwpfTableRow.getTableCells().forEach(
              xwpfTableCell -> {
                replaceFieldsInHeader(xwpfTableCell, dto);
              }
          );
        }
    );

    XWPFParagraph headerParagraph = mainTemplateDoc.getParagraphs().get(0);

    XmlCursor cursor = headerParagraph.getCTP().newCursor();
    XWPFTable table = mainTemplateDoc.insertNewTbl(cursor);
    headerTable.getRows().forEach(table::addRow);
    table.removeBorders();

    mainTemplateDoc.removeBodyElement(
        mainTemplateDoc.getPosOfParagraph(
            headerParagraph
        )
    );
  }

  private XWPFDocument getTemplateDoc(String path) throws IOException {
    var mainTemplateURL = getClass().getClassLoader().getResource(path);
    var file = new File(Objects.requireNonNull(mainTemplateURL).getFile());
    var fis = new FileInputStream(file.getAbsolutePath());
    return new XWPFDocument(fis);
  }

  private void replaceFieldsInHeader(XWPFTableCell cell, LawsuitDto dto) {
    cell.getParagraphs().forEach(
        xwpfParagraph -> {
          xwpfParagraph.getRuns().forEach(
              xwpfRun -> {
                var text = xwpfRun.getText(0);
                headerReplacements(dto, xwpfRun, text);
              }
          );
        }
    );

  }

  private static void bodyReplacements(LawsuitDto dto, XWPFRun xwpfRun, String text) {
    replace(xwpfRun, text, "areaNumber", dto.getArea().getNumber());
    replace(xwpfRun, text, "areaArea", dto.getArea().getArea().toString());
    replace(xwpfRun, text, "areaAddress", dto.getArea().getAddress());

    replace(xwpfRun, text, "contractPaymentPoint", dto.getContract().getPaymentPoint());
    replace(xwpfRun, text, "contractNumber", dto.getContract().getNumber());
    replace(xwpfRun, text, "contractSum", dto.getContract().getSum().toString());
    replace(xwpfRun, text, "contractDate", formatDate(dto.getContract().getDate()));
    replace(xwpfRun, text, "contractPeriod", dto.getContract().getPeriod());

    replace(xwpfRun, text, "pretensionOverduePeriod", dto.getPretension().getOverduePeriod());
    replace(xwpfRun, text, "pretensionDebt", dto.getPretension().getDebt());
    replace(xwpfRun, text, "pretensionDue", dto.getPretension().getDue().toString());
    replace(xwpfRun, text, "pretensionDate", formatDate(dto.getPretension().getDate()));


    replace(xwpfRun, text, "plaintiffPaymentAccount", dto.getPlantiff().getPaymentAccount());
    replace(xwpfRun, text, "plaintiffCorrespondentAccount", dto.getPlantiff().getINN());
    replace(xwpfRun, text, "plaintiffBank", dto.getPlantiff().getBank());
    replace(xwpfRun, text, "plaintiffBIC", dto.getPlantiff().getBIC());
    replace(xwpfRun, text, "plaintiffKPP", dto.getPlantiff().getKPP());
    replace(xwpfRun, text, "plaintiffOKTMO", dto.getPlantiff().getOKTMO());
    replace(xwpfRun, text, "plaintiffRepresentative", dto.getPlantiff().getRepresentative());

  }

  private static void headerReplacements(LawsuitDto dto, XWPFRun xwpfRun, String text) {
    replace(xwpfRun, text, "defendantINN", dto.getDefendant().getINN());
    replace(xwpfRun, text, "defendantPassport", dto.getDefendant().getPassport());
    replace(xwpfRun, text, "defendantOGRN", dto.getDefendant().getOGRN());
    replace(xwpfRun, text, "defendantAddress", dto.getDefendant().getAddress());
    replace(xwpfRun, text, "defendantName", dto.getDefendant().getTitle());
    replace(xwpfRun, text, "defendantINN", dto.getDefendant().getINN());
    replace(xwpfRun, text, "defendantFIO", dto.getDefendant().getFIO());
    replace(xwpfRun, text, "defendantBirthdate",
        formatDate(dto.getDefendant().getBirthdate()));

    replace(xwpfRun, text, "plaintiffPassport", dto.getDefendant().getPassport());
    replace(xwpfRun, text, "plaintiffOGRN", dto.getPlantiff().getOGRN());
    replace(xwpfRun, text, "plaintiffINN", dto.getPlantiff().getINN());
    replace(xwpfRun, text, "plaintiffAddress", dto.getPlantiff().getAddress());
  }

  public static String formatDate(LocalDate date) {
    if (Objects.isNull(date)) {
      return "";
    }
    return "%s г.".formatted(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy",
        Locale.getDefault())));
  }

  private static void replace(XWPFRun xwpfRun, String text, String param, String replacement) {
    if (Objects.nonNull(text) &&  text.contains(param)) {
      text = text.replace(param, replacement);
      xwpfRun.setText(text, 0);
    }
  }
}
