package es.codeurjc.web.nitflex.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ImageUtils {

    private static final String ERROR_AT_PROCESSING_THE_IMAGE = "Error at processing the image";

	public Blob remoteImageToBlob(String imageUrl){
        try {
            Resource image = new UrlResource(imageUrl);
		    return BlobProxy.generateProxy(image.getInputStream(), image.contentLength());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_AT_PROCESSING_THE_IMAGE);
        }
	}

	public Blob localImageToBlob(String localFilePath){
		File imageFile = new File(localFilePath);
		if (imageFile.exists()) {
			try {
				return BlobProxy.generateProxy(imageFile.toURI().toURL().openStream(), imageFile.length());
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_AT_PROCESSING_THE_IMAGE);
			}
		}
		return null;
	}

    public Blob multiPartFileImageToBlob(MultipartFile image){
		if (image!=null && !image.isEmpty()) {
			try {
				return BlobProxy.generateProxy(image.getInputStream(), image.getSize());
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_AT_PROCESSING_THE_IMAGE);
			}
		}
        return null;
	}

}
