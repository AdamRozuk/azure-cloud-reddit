package com.example.Cloud_Lab;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/media")
public class MediaController {

    private static final Logger logger = LoggerFactory.getLogger(MediaController.class);

    @Autowired
    private MediaService fileStorageService;

    @GetMapping
    public HttpEntity download() {
        StringObj result = new StringObj();
        result.setHello("Hello world !!! -- DEPLOY 2");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping
    public String upload( byte[] contents) {
        return "Some code";
    }

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=storage1920;AccountKey=Zgo5bykmGxufmew62KYtYGPqV+deZZVdOowEUaz2p1gNm4DmZwruE0afEy/lHyjiQlIvN1M7s7i3iCMhQxN2LQ==;EndpointSuffix=core.windows.net";
        CloudStorageAccount storageAccount = null;
        try {
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference("images");
            // Get reference to blob
            CloudBlob blob = container.getBlockBlobReference( file.getOriginalFilename());
            blob.getProperties().setContentType(file.getContentType());
            // Upload contents from byte array (check documentation for other alternatives)
            blob.uploadFromByteArray(file.getBytes(), 0, file.getBytes().length);
        } catch (URISyntaxException | StorageException | IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        //String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/media/downloadFile/")
                .path(file.getOriginalFilename())
                .toUriString();

        return new UploadFileResponse(file.getOriginalFilename(), fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        //create client to blob storage
        String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=storage1920;AccountKey=Zgo5bykmGxufmew62KYtYGPqV+deZZVdOowEUaz2p1gNm4DmZwruE0afEy/lHyjiQlIvN1M7s7i3iCMhQxN2LQ==;EndpointSuffix=core.windows.net";
        CloudStorageAccount storageAccount = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        byte[] contents = null;
        try {
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference("images");
            // Get reference to blob
            CloudBlob blob = container.getBlobReferenceFromServer(fileName);
            byteArrayOutputStream = new ByteArrayOutputStream();
            blob.download(byteArrayOutputStream);
            byteArrayOutputStream.close();
            contents = byteArrayOutputStream.toByteArray();
        } catch (URISyntaxException | StorageException | IOException e) {
            e.printStackTrace();
            logger.info("probably storage exception !");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        Resource byteArrayResource = new ByteArrayResource(contents);

        // Try to determine file's content type
        String contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(byteArrayResource);
    }
}
