package com.huangch.cloud.utils.file;

import java.io.File;

/**
 * @author huangch
 * @since 2025-04-18
 */
@SuppressWarnings("UnusedReturnValue")
public class FileUtils {

    /**
     * 删除某个路径下，叶子目录不存在.jar结尾文件的目录
     *
     * @param dir 顶级目录
     * @return 删除结果
     */
    public boolean deleteLeafDirsWithoutJar(File dir) {
        if (!dir.isDirectory()) {
            return false;
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return dir.delete();
        }

        boolean isLeaf = true;
        for (File file : files) {
            if (file.isDirectory()) {
                isLeaf = false;
                // 递归子目录
                deleteLeafDirsWithoutJar(file);
            }
        }

        // 只有是叶子目录才检查 jar 文件
        if (isLeaf) {
            boolean hasJar = false;
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".jar")) {
                    hasJar = true;
                    break;
                }
            }

            if (!hasJar) {
                System.out.println("删除目录：" + dir.getAbsolutePath());
                return this.delete(dir);
            }
        }

        return false;
    }

    /**
     * 删除某个路径下的所有文件
     *
     * @param dir 路径
     * @return 删除结果
     */
    public boolean delete(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        delete(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
        return dir.delete();
    }
}
