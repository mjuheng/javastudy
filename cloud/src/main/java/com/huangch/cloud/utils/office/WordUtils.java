package com.huangch.cloud.utils.office;

import cn.hutool.core.io.IoUtil;
import com.aspose.cells.Workbook;
import com.aspose.cells.WorksheetCollection;
import com.aspose.words.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author huangch
 * @date 2023-09-24
 */
@SuppressWarnings("unused")
@Slf4j
@Service
public class WordUtils {

    private final static String TABLE_PLACEHOLDER_PATTERN = "^\\{\\{.*\\..*}}$";

    public final String XLS = "xls";
    public final String XLSX = "xlsx";
    public final String WORD = "word";
    public final String PDF = "pdf";
    public final String UNKNOWN = "unknown";

    @PostConstruct
    public void postConstruct() throws Exception {
        License license  = new License();
        license.setLicense(new ByteArrayInputStream(new byte[]{0}));
    }

    public void mergeWord(Document source, Document target) {
        try {
            source.appendDocument(target, ImportFormatMode.KEEP_SOURCE_FORMATTING);
            source.updatePageLayout();
        } catch (Exception e) {
            log.error("文档处理失败", e);
            throw new RuntimeException("文档处理失败");
        }
    }

    public void word2pdf(Document document, OutputStream os) {
        try {
            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            document.save(os, pdfSaveOptions);
        } catch (Exception e) {
            log.error("文档转换失败", e);
            throw new RuntimeException("文档转换失败");
        }
    }

    /**
     * 删除word内的批注信息
     *
     * @param bytes word文件流
     * @return 删除批示后的文件流
     * @throws IOException 文件处理异常
     */
    public static byte[] deleteComment(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        XWPFDocument document = new XWPFDocument(bais);
        bais.close();

        // 遍历文档中的段落
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            // 遍历段落中的文本
            for (XWPFRun run : paragraph.getRuns()) {
                // 删除段落中的所有批注
                XmlCursor cursor = run.getCTR().newCursor();
                cursor.selectPath("./*");
                while (cursor.toNextSelection()) {
                    if ("commentReference".equals(cursor.getName().getLocalPart())) {
                        cursor.removeXml();
                    }
                }
                cursor.close();
            }
        }

        // 保存修改后的文档
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.write(baos);
        return baos.toByteArray();
    }

    /**
     * 复制段落样式
     *
     * @param sourceRun 源样式
     * @param targetRun 目标样式
     */
    private static void copyStyle(XWPFRun sourceRun, XWPFRun targetRun) {
        targetRun.setFontSize(sourceRun.getFontSizeAsDouble());
        targetRun.setFontFamily(sourceRun.getFontFamily());
        targetRun.setBold(sourceRun.isBold());
        targetRun.setColor(sourceRun.getColor());
        targetRun.setImprinted(sourceRun.isImprinted());
    }

    public void file2Pdf(byte[] fileContent, String fileExt, OutputStream os) throws Exception {
        if (fileContent == null || fileContent.length == 0) {
            throw new RuntimeException("文件内容不能为空");
        }

        if (!"xls".equalsIgnoreCase(fileExt)
                && !"xlsx".equalsIgnoreCase(fileExt)) {
            throw new RuntimeException("仅支持 Excel 文件转 PDF");
        }

        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(fileContent);

        Workbook workbook = new Workbook(inputStream);

        // 可选：自动适应列宽
        WorksheetCollection sheets = workbook.getWorksheets();
        for (int i = 0; i < sheets.getCount(); i++) {
            sheets.get(i).autoFitColumns();
        }

        com.aspose.cells.PdfSaveOptions options = new com.aspose.cells.PdfSaveOptions();

        // 所有列压缩到一页宽（推荐）
        options.setAllColumnsInOnePagePerSheet(true);

        workbook.save(os, options);
    }

    public Document file2Document(byte[] fileContent, String fileExt) throws Exception {
        ByteArrayInputStream archiveFileStream = IoUtil.toStream(fileContent);
        if (XLS.equals(fileExt) || XLSX.equals(fileExt)) {
            com.aspose.cells.Workbook book = new com.aspose.cells.Workbook(archiveFileStream);
            ByteArrayOutputStream documentStream = new ByteArrayOutputStream();
            book.save(documentStream, SaveFormat.DOCX);
            return new Document(new ByteArrayInputStream(documentStream.toByteArray()));
        }
        return new Document(archiveFileStream);
    }

}
