package com.app.dto;

import java.util.HashSet;
import java.util.Set;

public class TeacherRegisterDto {

    private String name;
    private String email;
    private String password;
    private Set<String> subjectCodes = new HashSet<>();

    public TeacherRegisterDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getSubjectCodes() {
        return subjectCodes;
    }

    public void setSubjectCodes(Set<String> subjectCodes) {
        this.subjectCodes = subjectCodes != null ? subjectCodes : new HashSet<>();
    }
}
