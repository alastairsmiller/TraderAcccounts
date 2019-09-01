package com.ib.traderaccounts.webcontroller;

import com.ib.traderaccounts.businessobjects.ReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Interface for the WebService. This class uses a separate thread to create the report so that control can
 * return to the front end immediately otherwise it could be a long wait on the front end
 *
 * The thread pool is limited to one thread so if you call it twice you will have to wait for the previous invocation
 * to complete. Do not want a queue is useless ReportGenertors, one at a time is enough.
 */
@RestController
public class ReportController {

    @Autowired
    ReportGenerator reportGenerator;

    private ExecutorService executor = Executors.newFixedThreadPool(1);

    @RequestMapping(value = "/generateReport", method = RequestMethod.POST)
    public ResponseEntity<Object>  generateReport() {
        // Do this in a separate thread so you can return control to the UI asap
        // limit this to one at a time incase UI is hitting the button a million times a minute
        try {
            Future<Boolean> future = executor.submit(reportGenerator);
            return (new ResponseEntity<>("Report Generation started", HttpStatus.OK));
        } catch ( Exception ex ) {
            return (new ResponseEntity<>("Report Generation Exception :" + ex.getMessage(), HttpStatus.I_AM_A_TEAPOT));
        }
   }

}