package idxsync.idx.controller;


import idxsync.domain.ListingResidential;
import idxsync.idx.service.ListingResidentialService;
import idxsync.idx.service.ListingsRequest;
import idxsync.idx.service.ResourceNotFoundException;
import idxsync.idx.service.SortConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static idxsync.idx.service.PageConfig.PageConfigBuilder;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ListingResidentialController {

    @Autowired
    private ListingResidentialService listingResidentialService;

    @RequestMapping(value = "/residential/listings/{mlsNumber}", method = GET)
    public ListingResidentialResponse getListing(@PathVariable String mlsNumber,
                                                 @RequestParam(required = false, defaultValue = "true") Boolean xformLookupValues) {
        ListingResidential listing = listingResidentialService.getListing(mlsNumber);

        ListingResidentialResponse resp = new ListingResidentialResponse();

        if (xformLookupValues) {
            listingResidentialService.transformLookupValues(listing);
        }

        resp.setListing(listing);

        return resp;
    }

    @RequestMapping(value = "/residential/listings", method = GET)
    public ListingResidentialPageResponse getListings(
            @RequestParam(required = false) String locationQuery,
            IncludedFields includedFields,
            ExcludedFields excludedFields,
            RangeRequest rangeRequest,
            @RequestParam(required = false, defaultValue = "matrixModifiedDate") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer per_page,
            @RequestParam(required = false, defaultValue = "true") Boolean xformLookupValues) {

        ListingsRequest listingsRequest = new ListingsRequest();


        //location search
        if (locationQuery != null) {
            listingsRequest.setLocationQuery(locationQuery);
        }

        //range filter
        if (rangeRequest != null && rangeRequest.getRanges() != null) {
            listingsRequest.getRanges().addAll(rangeRequest.getRanges());
        }

        //included fields
        if (includedFields != null && includedFields.getIncludedFields() != null) {
            listingsRequest.getIncludedFields().addAll(includedFields.getIncludedFields());
        }

        //excluded fields
        if (excludedFields != null && excludedFields.getExcludedFields() != null) {
            listingsRequest.getExcludedFields().addAll(excludedFields.getExcludedFields());
        }

        listingsRequest.setPageConfig(
                new PageConfigBuilder()
                    .setPage(page)
                    .setSize(per_page)
                    .build());

        listingsRequest.setSortConfig(
                new SortConfig.SortConfigBuilder()
                        .setField(sortBy)
                        .setOrder(sortOrder)
                        .build());

        return createPageResponse(listingResidentialService.getListings(listingsRequest), xformLookupValues);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    private void handleBadRequests(HttpServletResponse response, Exception exception) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    private void handleBadRequests(HttpServletResponse response, ResourceNotFoundException exception) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    private ListingResidentialPageResponse createPageResponse(Page<ListingResidential> pageResp, Boolean xformLookupValues) {
        ListingResidentialPageResponse resp = new ListingResidentialPageResponse();

        resp.setListings(pageResp.getContent());
        resp.getMeta().put("total_pages", pageResp.getTotalPages());

        if (xformLookupValues) {
            listingResidentialService.transformLookupValues(pageResp.getContent());
        }

        return resp;
    }
}
