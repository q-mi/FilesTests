package github.qmi.tests;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static github.qmi.helpers.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Owner("Tsareva")
public class FileTests extends TestBase {

    @Test
    @DisplayName("Test for upload file")
    @Story("Upload file and check it on site")
    void uploadFileResources() {
        open(UPLOAD_URL);
        $(UPLOAD_SELECTOR).uploadFromClasspath(UPLOAD_FILE_NAME);
        $(UPLOADED_SELECTOR).shouldHave(text(UPLOAD_NAME));
    }

    @Test
    @DisplayName("Test for CSV file")
    @Story("Parse and check CSV file")
    void downloadFileCSV() throws IOException, CsvException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(CSV_FILE_NAME)) {
            assert is != null;
            try (Reader reader = new InputStreamReader(is)) {
                CSVReader csvReader = new CSVReader(reader);
                List<String[]> strings = csvReader.readAll();
                assertEquals(7, strings.size());
            }
        }
    }

    @Test
    @DisplayName("Test for ZIP file")
    @Story("Parse and check ZIP file")
    void parseZipFileTest() throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(ZIP_FILE_NAME);
             ZipInputStream zip = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                assertEquals(ZIP_FILE_TEXT, entry.getName());
            }
        }
    }

    @Test
    @DisplayName("Test for txt file")
    @Story("Download, parse and check txt file")
    void downloadFile() throws IOException {
        open(TXT_URL);
        File download = $(TXT_SELECTOR).download();
        String fileContent = IOUtils.toString(new FileReader(download));
        assertTrue(fileContent.contains(TXT_TEXT));
    }

    @Test
    @DisplayName("Test for PDF file")
    @Story("Download, parse and check PDF file")
    void downloadPdfTest() throws IOException {
        open(PDF_URL);
        File file = $(PDF_SELECTOR).download();
        PDF parsedPdf = new PDF(file);
        assertTrue(parsedPdf.text.contains(PDF_TEXT));
        assertEquals(6, parsedPdf.numberOfPages);
    }

    @Test
    @DisplayName("Test for Excel file")
    @Story("Download, parse and check xls file")
    public void downloadXlsTest() throws FileNotFoundException {
        open(XLS_URL);
        File file = $(XLS_SELECTOR).download();
        XLS parsedXls = new XLS(file);

        boolean checkPassed = parsedXls.excel
                .getSheetAt(0)
                .getRow(128)
                .getCell(0)
                .getStringCellValue()
                .contains(XLS_TEXT);
        assertTrue(checkPassed);
    }

    @Test
    @DisplayName("Test for ZIP file")
    @Story("Download, parse and check ZIP file")
    void downloadZipFileTest() throws IOException {
        open(ZIP_URL);
        File file = $(ZIP_SELECTOR).download();
        try (InputStream is = new FileInputStream(file.getPath());
             ZipInputStream zip = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                assertEquals(ZIP_TEXT, entry.getName());
            }
        }
    }
}
