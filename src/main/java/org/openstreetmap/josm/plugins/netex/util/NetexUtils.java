package org.openstreetmap.josm.plugins.netex.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class NetexUtils {
    private static final String TEMP_DIR_PREFIX = "josm_netex_temp_";
    
    public static File createTempDir() throws IOException {
        return Files.createTempDirectory(TEMP_DIR_PREFIX).toFile();
    }

    public static void deletePreviousTempDirs() {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        if (tmpDir.exists() && tmpDir.isDirectory()) {
            for (File dir : tmpDir.listFiles((dir, name) -> name.startsWith(TEMP_DIR_PREFIX))) {
                deleteDir(dir);
            }
        }
    }

    private static void deleteDir(File dir) {
        for (File file : dir.listFiles()) {
            if (!file.delete()) {
                file.deleteOnExit();
            }
        }
        if (!dir.delete()) {
            dir.deleteOnExit();
        }
    }
}

