package id.perumdamts.kepegawaian.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileUploadUtilTest {

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Test
    void testReportStorageLifecycle() {
        String subFolder = "test-temp";
        String fileName = "test-report.xlsx";
        byte[] content = "dummy excel content".getBytes();

        // Save
        Path savedPath = fileUploadUtil.saveFileLaporan(content, subFolder, fileName);
        assertNotNull(savedPath);
        assertTrue(Files.exists(savedPath));

        // Load as Resource
        Resource resource = fileUploadUtil.loadFileLaporanAsResource(subFolder, fileName);
        assertNotNull(resource);
        assertTrue(resource.exists());

        // Delete
        fileUploadUtil.deleteOldFileLaporan(subFolder, fileName);
        assertFalse(Files.exists(savedPath));
    }
}
