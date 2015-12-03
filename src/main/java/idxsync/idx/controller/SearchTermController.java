package idxsync.idx.controller;

import idxsync.domain.SearchTermResidential;
import idxsync.domain.SearchTermSchool;
import idxsync.idx.service.PageConfig;
import idxsync.idx.service.ResourceNotFoundException;
import idxsync.idx.service.SearchTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class SearchTermController {

    @Autowired
    private SearchTermService searchTermService;

    @RequestMapping(value = "/listing/residential/search/term/location", method = GET)
    public Page<SearchTermResidential> locationTerms(@RequestParam String q,
                                           @RequestParam(required = false, defaultValue = "0") Integer page,
                                           @RequestParam(required = false, defaultValue = "10") Integer size) {
        return searchTermService.findResidentialSearchTerms(q,
                new PageConfig.PageConfigBuilder()
                        .setPage(page)
                        .setSize(size).build());
    }

    @RequestMapping(value = "/listing/residential/search/term/school", method = GET)
    public Page<SearchTermSchool> schoolTerms(@RequestParam String q,
                                   @RequestParam(required = false, defaultValue = "0") Integer page,
                                   @RequestParam(required = false, defaultValue = "10") Integer size) {
        return searchTermService.findSchoolSearchTerms(q,
                new PageConfig.PageConfigBuilder()
                        .setPage(page)
                        .setSize(size).build());
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
