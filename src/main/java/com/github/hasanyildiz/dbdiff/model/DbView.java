package com.github.hasanyildiz.dbdiff.model;

/**
 * ALL_VIEWS görünümündeki bir view.
 *
 * @author EROLULKER
 */
public final class DbView {

    private final String viewName;
    private final String text;

    public DbView(String viewName, String text) {
        this.viewName = viewName;
        this.text = text;
    }

    public String getViewName() {
        return viewName;
    }

    public String getText() {
        return text;
    }
}
