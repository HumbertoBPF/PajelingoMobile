package com.example.pajelingo.models;

import java.util.List;

public class Page<E> {
    private String previous;
    private String next;
    private Integer count;
    private List<E> results;

    public Page(String previous, String next, Integer count, List<E> results) {
        this.previous = previous;
        this.next = next;
        this.count = count;
        this.results = results;
    }

    public String getPrevious() {
        return previous;
    }

    public String getNext() {
        return next;
    }

    public Integer getCount() {
        return count;
    }

    public List<E> getResults() {
        return results;
    }
}
