package com.fcmbp.geigerobservatory.repository;

import com.fcmbp.geigerobservatory.model.Reading;
import org.springframework.stereotype.Repository;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

@Repository
public class ReadingRepository {

    private final Deque<Reading> readings = new ArrayDeque<>();

    public synchronized Reading save(Reading reading, int historyLimit) {
        readings.addLast(reading);
        while (readings.size() > historyLimit) {
            readings.removeFirst();
        }
        return reading;
    }

    public synchronized List<Reading> findRecent(int limit) {
        List<Reading> copy = new ArrayList<>(readings);
        int fromIndex = Math.max(0, copy.size() - limit);
        return copy.subList(fromIndex, copy.size());
    }

    public synchronized Optional<Reading> findLatest() {
        return Optional.ofNullable(readings.peekLast());
    }

    public synchronized long count() {
        return readings.size();
    }

    public synchronized long countAnomalies() {
        return readings.stream().filter(Reading::anomalous).count();
    }
}
