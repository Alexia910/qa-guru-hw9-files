package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import guru.qa.domain.Student;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.codeborne.pdftest.assertj.Assertions.assertThat;

public class SelenideFilesTest {

    @Test
    void pdfTest() throws Exception {
        ZipFile zipFile = new ZipFile("src/test/resources/dystopian_books.zip");
        ZipEntry zipEntry = zipFile.getEntry("1984.pdf");
        InputStream inputStream = zipFile.getInputStream(zipEntry);
        PDF pdf = new PDF(inputStream);
        assertThat(pdf.text).contains("Свобода – это возможность сказать, что дважды два – четыре.");
    }

    @Test
    void xlsTest() throws Exception {
        ZipFile zipFile = new ZipFile("src/test/resources/dystopian_books.zip");
        ZipEntry zipEntry = zipFile.getEntry("Books.xlsx");
        InputStream inputStream = zipFile.getInputStream(zipEntry);
        XLS xls = new XLS(inputStream);
        assertThat(xls.excel
                .getSheetAt(0)
                .getRow(3)
                .getCell(0)
                .getStringCellValue()).contains("Замятин");
    }

    @Test
    void csvTest() throws Exception {
        ZipFile zipFile = new ZipFile("src/test/resources/dystopian_books.zip");
        ZipEntry zipEntry = zipFile.getEntry("Books.csv");
        try (InputStream inputStream = zipFile.getInputStream(zipEntry);
             CSVReader csv = new CSVReader(new InputStreamReader(inputStream))) {
            List<String[]> content = csv.readAll();
            assertThat(content.get(1)).contains("Оруэлл", "1984");
        }
    }

    @Test
    void jsonTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Student student = mapper.readValue(Paths.get("src/test/resources/simple.json").toFile(), Student.class);
        assertThat(student.books).contains("1984", "We");
    }
}