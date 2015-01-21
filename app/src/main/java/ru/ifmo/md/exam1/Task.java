package ru.ifmo.md.exam1;

import java.util.ArrayList;

/**
 * Created by daria on 21.01.15.
 */
public class Task {
    private String title;
    private String description;
    private String date;
    private boolean label;
    private ArrayList<String> tags;

    public Task(ArrayList<String> tags, String date, String description, String title) {
        this.tags = tags;
        this.date = date;
        this.description = description;
        this.title = title;
        label = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isLabel() {
        return label;
    }

    public void setLabel(boolean label) {
        this.label = label;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
