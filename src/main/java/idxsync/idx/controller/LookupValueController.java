package idxsync.idx.controller;

import idxsync.domain.LookupValue;
import idxsync.idx.service.LookupValueService;
import idxsync.idx.service.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class LookupValueController {

    @Autowired
    private LookupValueService lookupValueService;

    @RequestMapping(value = "/residential/lookupValues", method = GET)
    public Map<String, Object> getLookupValue(@RequestParam(required = false) String field,
                                              @RequestParam(required = false) String value) {

        //assume findAll query
        if (field == null || value == null) {
            List<LookupValue> lookupValues = lookupValueService.getLookupValues();

            Map<String, Object> resp = new HashMap<>();
            resp.put("lookupValues", lookupValues);

            return resp;
        }

        LookupValue lookupValue = lookupValueService.getLookupValue(field, value);

        Map<String, Object> resp = new HashMap<>();

        resp.put("lookupValue", lookupValue);

        return resp;
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
