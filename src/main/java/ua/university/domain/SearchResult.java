package ua.university.domain;

import java.util.List;

public record SearchResult<T>(String query, List<T> results, long count) {
    public boolean isEmpty() { return results.isEmpty(); }
}
