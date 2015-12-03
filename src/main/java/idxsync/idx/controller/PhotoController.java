package idxsync.idx.controller;

import idxsync.idx.service.PhotoService;
import idxsync.idx.service.ResourceNotFoundException;
import idxsync.idx.strategy.PhotoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static idxsync.idx.service.PhotoSize.getPhotoSize;
import static idxsync.idx.strategy.Utils.getMediaType;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class PhotoController {

    @Autowired
    private PhotoService photoService;


    @RequestMapping(value="/residential/listing/photo/{mlsNumber}/{photoIdx}", method=GET)
    public ResponseEntity<InputStreamResource> getListingResidentialPhoto(
            @PathVariable String mlsNumber,
            @PathVariable Integer photoIdx,
            @RequestParam(required = false, defaultValue = "large") String size) {

        return createResponse(photoService.getListingResidentialPhoto(mlsNumber, photoIdx, getPhotoSize(size)));
    }

    @RequestMapping(value="/commercial/listing/photo/{mlsNumber}/{photoIdx}", method=GET)
    public ResponseEntity<InputStreamResource> getListingCommercialPhoto(
            @PathVariable String mlsNumber,
            @PathVariable Integer photoIdx,
            @RequestParam(required = false, defaultValue = "large") String size) {

        return createResponse(photoService.getListingCommercialPhoto(mlsNumber, photoIdx, getPhotoSize(size)));
    }

    @RequestMapping(value="/land/listing/photo/{mlsNumber}/{photoIdx}", method=GET)
    public ResponseEntity<InputStreamResource> getListingLandPhoto(
            @PathVariable String mlsNumber,
            @PathVariable Integer photoIdx,
            @RequestParam(required = false, defaultValue = "large") String size) {

        return createResponse(photoService.getListingLandPhoto(mlsNumber, photoIdx, getPhotoSize(size)));
    }

    @RequestMapping(value="/mult/listing/photo/{mlsNumber}/{photoIdx}", method=GET)
    public ResponseEntity<InputStreamResource> getListingMultPhoto(
            @PathVariable String mlsNumber,
            @PathVariable Integer photoIdx,
            @RequestParam(required = false, defaultValue = "large") String size) {

        return createResponse(photoService.getListingMultPhoto(mlsNumber, photoIdx, getPhotoSize(size)));
    }

    private ResponseEntity<InputStreamResource> createResponse(PhotoData photoData) {
        return ResponseEntity
                .ok()
                .contentType(getMediaType(photoData.getMimeType()))
                .contentLength(photoData.getSize())
                .body(new InputStreamResource(photoData.getPhotoInputStream()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    private void handleBadRequests(HttpServletResponse response, Exception exception) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    private void handleBadRequests(HttpServletResponse response, ResourceNotFoundException exception) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }
}
