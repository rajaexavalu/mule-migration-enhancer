package com.exavalu.migration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MuleZipExtractorController {

    // Endpoint for uploading and extracting a Mule3 ZIP file
    @PostMapping("/upload-mule3-zip")
    public static ResponseEntity<String> cloneGitProject(@RequestBody MultipartFile file) {
        if (file.isEmpty()) {
            // Return a BAD_REQUEST response if no file is provided
            return new ResponseEntity<>("Please select a file!", HttpStatus.BAD_REQUEST);
        }
 
        String uploadDir = "D:\\App\\mule"; // Directory to store extracted files
        
        // Extract the uploaded ZIP file using MuleZipExtractorLogic
        if(MuleZipExtractorLogic.extractZip(file,uploadDir)) {
            // Return OK response if extraction is successful
            return new ResponseEntity<>("File uploaded and extracted successfully!", HttpStatus.OK);
        }
        else {
            // Return INTERNAL_SERVER_ERROR if extraction fails
            return new ResponseEntity<>("Failed to upload and extract file!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}