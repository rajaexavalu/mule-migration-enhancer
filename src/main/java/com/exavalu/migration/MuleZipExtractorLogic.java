package com.exavalu.migration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.web.multipart.MultipartFile;

public class MuleZipExtractorLogic {

    // Method to extract files from a ZIP file
    public static boolean extractZip(MultipartFile file, String destDirectory) {
        byte[] buffer = new byte[1024];

        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            // Create the destination directory if it doesn't exist
            File destDir = new File(destDirectory);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                // Path for the current ZIP entry
                String filePath = destDirectory + File.separator + zipEntry.getName();
                File newFile = new File(filePath);

                // Create necessary directory structure if the entry is a directory
                new File(newFile.getParent()).mkdirs();

                // Output the file path for each entry
                System.out.println(filePath);

                if (zipEntry.isDirectory()) {
                    // Create directory if the entry is a directory
                    newFile.mkdirs();
                } else {
                    // Extract the file content if the entry is a file
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }

            // Indicate completion of extraction
            System.out.println("Extraction completed.");
            return true;

        } catch (IOException e) {
            // Print the stack trace in case of an exception
            e.printStackTrace();
            return false;
        }
    }
}