package com.example.pajelingo.models;

import java.util.List;

public class Page<E> {
    private final String previous;
    private String next;
    private final Integer count;
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

    public void setNext(String next) {
        this.next = next;
    }

    public void addResults(List<E> results) {
        this.results.addAll(results);
    }
}
