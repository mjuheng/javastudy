package com.huangch.cloud.utils.file;

import com.huangch.cloud.utils.http.HttpUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author huangch
 * @since 2023-12-06
 */
@SuppressWarnings({"ConstantValue", "unused"})
@Slf4j
public class ZipUtils {

    private static final int BUFFER_SIZE = 4 * 1024;

    /**
     * 压缩成ZIP
     *
     * @param srcDir           压缩文件夹路径
     * @param out              压缩文件输出流
     * @param keepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String srcDir, OutputStream out, boolean keepDirStructure) throws Exception {
        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), keepDirStructure);
        }
    }


    /**
     * 递归压缩方法
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param keepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean keepDirStructure) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 若需要保留原来的文件结构时,需要对空文件夹进行处理
                if (keepDirStructure) {
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (keepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(), keepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), keepDirStructure);
                    }
                }
            }
        }
    }

    /**
     * 从url下载文件并压缩
     *
     * @param fileUrlMap 文件信息
     * @param os         输出流
     * @throws Exception exception
     */
    public static void toZipByUrl(Map<String, List<FileUrl>> fileUrlMap, OutputStream os) throws Exception {
        try (ZipOutputStream zos = new ZipOutputStream(os)) {
            for (Map.Entry<String, List<FileUrl>> fileUrlEntry : fileUrlMap.entrySet()) {

                String folder = fileUrlEntry.getKey();
                List<FileUrl> fileUrlNameList = fileUrlEntry.getValue();

                for (FileUrl fileUrl : fileUrlNameList) {
                    zos.putNextEntry(new ZipEntry(folder + File.separator + fileUrl.getName()));
                    InputStream in = HttpUtils.sendGet(fileUrl.getUrl(), null, null);

                    int len;
                    byte[] buf = new byte[BUFFER_SIZE];
                    while ((len = in.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                    }
                    zos.flush();
                    zos.closeEntry();
                    in.close();
                }
            }
        }
    }

    /**
     * 解压文件
     *
     * @param in       压缩包输入流
     * @param consumer 每个文件处理逻辑
     * @throws Exception exception
     */
    public static void unZip(InputStream in, BiConsumer<ZipEntry, ZipInputStream> consumer) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(in)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                consumer.accept(entry, zis);
                zis.closeEntry();
            }
        }
    }

    @Data
    @NoArgsConstructor
    public static class FileUrl {

        private String url;

        private String name;

        public FileUrl(String url, String name) {
            this.url = url;
            this.name = name;
        }
    }


}
