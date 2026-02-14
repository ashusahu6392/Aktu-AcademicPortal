package com.app.dto;

public class SubjectSummary {
    private String subjectCode;
    private String subjectName;
    private long materialsCount;
    private long unitsCount;

    public SubjectSummary(String subjectCode, String subjectName, long materialsCount, long unitsCount) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.materialsCount = materialsCount;
        this.unitsCount = unitsCount;
    }

    public String getSubjectCode() { return subjectCode; }
    public String getSubjectName() { return subjectName; }
    public long getMaterialsCount() { return materialsCount; }
    public long getUnitsCount() { return unitsCount; }
}
