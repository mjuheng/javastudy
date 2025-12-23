package com.huangch.cloud.utils.office;

import com.aspose.words.Document;
import com.aspose.words.ImportFormatMode;
import com.aspose.words.License;
import com.aspose.words.PdfSaveOptions;
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

}
