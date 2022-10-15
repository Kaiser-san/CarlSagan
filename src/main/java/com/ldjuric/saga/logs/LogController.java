package com.ldjuric.saga.logs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Profile({"log", "all"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
@CrossOrigin(origins="*", maxAge=3600)
public class LogController {
    private final LogService logService;

    @PutMapping
    public ResponseEntity<?> addLog(@RequestBody String log) {
        logService.addLog(log);
        return ResponseEntity.status(HttpStatus.OK).body("Added log");
    }

    @GetMapping
    public ResponseEntity<?> getLogs() {
        return ResponseEntity.status(HttpStatus.OK).body(logService.getLogs());
    }
}
