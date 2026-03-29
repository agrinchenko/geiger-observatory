package com.fcmbp.geigerobservatory.api;

import com.fcmbp.geigerobservatory.ingest.LiveReadingPublisher;
import com.fcmbp.geigerobservatory.ingest.ReadingIngestionService;
import com.fcmbp.geigerobservatory.model.AnalysisSnapshot;
import com.fcmbp.geigerobservatory.model.DeviceStatusResponse;
import com.fcmbp.geigerobservatory.model.Reading;
import com.fcmbp.geigerobservatory.model.ReadingSummary;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Validated
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/readings")
public class ReadingController {

    private final ReadingIngestionService readingIngestionService;
    private final LiveReadingPublisher liveReadingPublisher;

    public ReadingController(
            ReadingIngestionService readingIngestionService,
            LiveReadingPublisher liveReadingPublisher
    ) {
        this.readingIngestionService = readingIngestionService;
        this.liveReadingPublisher = liveReadingPublisher;
    }

    @GetMapping
    public List<Reading> recentReadings(
            @RequestParam(defaultValue = "50") @Min(1) @Max(500) int limit
    ) {
        return readingIngestionService.recentReadings(limit);
    }

    @GetMapping("/summary")
    public ReadingSummary summary() {
        return readingIngestionService.summary();
    }

    @GetMapping("/analysis")
    public AnalysisSnapshot analysis() {
        return readingIngestionService.analysis();
    }

    @GetMapping("/device")
    public DeviceStatusResponse device() {
        return readingIngestionService.deviceStatus();
    }

    @GetMapping("/stream")
    public SseEmitter stream() {
        return liveReadingPublisher.subscribe();
    }
}
