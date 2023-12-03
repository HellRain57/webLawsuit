package domain.operations;

import domain.ports.GptPort;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import team.microchad.api.dto.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParseContractOperation {

    private final GptPort gptPort;

    /**
     * Plaintiff patterns
     */
    private static final Pattern INN_PATTERN = Pattern.compile("ИНН [0-9]{10}");
    private static final Pattern OGRN_PATTERN = Pattern.compile("");
    private static final Pattern KPP_PATTERN = Pattern.compile("КПП [0-9]{9}]");
    private static final Pattern BIC_PATTERN = Pattern.compile("БИК [0-9]{9}");

    public LawsuitDto execute(InputStream pdfContract) throws IOException {
        String pdfText = parsePdfToString(pdfContract);
        log.info(pdfText);
        return gptPort.extractSomeData(pdfText);
    }

    private LawsuitDto parseStringToLawsuit(String pdfText) {
        LawsuitDto result = new LawsuitDto();
        result.setPlantiff(extractPlaintiff(pdfText));
        result.setDefendant(extractDefendant(pdfText));
        result.setArea(extractArea(pdfText));
        result.setContract(extractContract(pdfText));
        result.setPretension(extractPretension(pdfText));
        result.setAgency(extractAgency(pdfText));
        result.setRules(extractRules(pdfText));
        return result;
    }

    private RulesDto extractRules(String text) {
        RulesDto result = new RulesDto();
        return result;
    }

    private GovermentAgencyDto extractAgency(String text) {
        GovermentAgencyDto result = new GovermentAgencyDto();
        return result;
    }

    private PretensionDto extractPretension(String text) {
        PretensionDto result = new PretensionDto();
        return result;
    }

    private AreaDto extractArea(String text) {
        AreaDto result = new AreaDto();
        return result;
    }

    private DefendantDto extractDefendant(String text) {
        DefendantDto result = new DefendantDto();
        return result;
    }

    private ContractDto extractContract(String text) {
        ContractDto result = new ContractDto();
        return result;
    }

    private PlaintiffDto extractPlaintiff(String text) {
        PlaintiffDto result = new PlaintiffDto();
        return result;
    }

    private String parsePdfToString(InputStream pdfContract) throws IOException {
        PDDocument document = PDDocument.load(pdfContract);
        StringBuilder result = new StringBuilder();
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        ExecutorService service = Executors.newCachedThreadPool();
        List<Future<String>> futureList = new ArrayList<>();
        for (int page = 0; page < document.getNumberOfPages(); page++) {
            Callable<String> parser = new PdfToStringParser(pdfRenderer
                    .renderImageWithDPI(page, 150, ImageType.GRAY), page);
            futureList.add(service.submit(parser));
        }
        for (Future<String> future : futureList) {
            while (!future.isDone()) ;
            try {
                result.append(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return result.toString();
    }

    static class PdfToStringParser implements Callable<String> {

        final BufferedImage bufferedImage;
        final int page;

        PdfToStringParser(BufferedImage bufferedImage, int page) {
            this.bufferedImage = bufferedImage;
            this.page = page;
        }

        @Override
        public String call() {
            File tempFile = null;
            try {
                ITesseract tesseract = new Tesseract();
                tesseract.setLanguage("rus");
                tempFile = File.createTempFile("tempfile_" + page, ".png");
                ImageIO.write(bufferedImage, "png", tempFile);
                return tesseract.doOCR(tempFile);
            } catch (Exception e) {
                return "ОШИБКА: Не удалось найти текст";
            } finally {
                tempFile.delete();
            }
        }
    }
}
