package com.example.csvparser.model;

import lombok.Getter;

@Getter
public class PageVisitModel {
    private String email;
    private String phone;
    private String source;

    public PageVisitModel(String email, String phone, String source) {
        this.email = email;
        this.phone = phone;
        this.source = source;
    }

    public boolean isPoison() {
        return this.source == null &&
                this.phone == null &&
                this.email == null;
    }
    
    public boolean isValid() {
        return this.source != null &&
                !this.source.isEmpty() &&
                this.phone != null &&
                !this.phone.isEmpty() &&
                this.email != null &&
                !this.email.isEmpty();
    }

    public static PageVisitModel getPoison() {
        return new PageVisitModel(null, null, null);
    }
}
