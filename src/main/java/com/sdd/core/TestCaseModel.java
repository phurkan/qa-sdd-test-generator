package com.sdd.core;

/**
 * TestCaseModel - Data model for a single generated test case.
 * Supports UI, API, and Accessibility types.
 */
public class TestCaseModel {
    private String tcId, ticketId, requirementId, title, type;
    private String priority, module, preconditions, steps;
    private String testData, expectedResult, actualResult, status, originalAC;

    private TestCaseModel(Builder b) {
        this.tcId = b.tcId; this.ticketId = b.ticketId; this.requirementId = b.requirementId;
        this.title = b.title; this.type = b.type; this.priority = b.priority;
        this.module = b.module; this.preconditions = b.preconditions; this.steps = b.steps;
        this.testData = b.testData; this.expectedResult = b.expectedResult;
        this.actualResult = b.actualResult; this.status = b.status; this.originalAC = b.originalAC;
    }

    public static class Builder {
        private String tcId, ticketId, requirementId, title, type;
        private String priority, module, preconditions, steps;
        private String testData, expectedResult, actualResult, status, originalAC;
        public Builder tcId(String v)           { tcId = v; return this; }
        public Builder ticketId(String v)       { ticketId = v; return this; }
        public Builder requirementId(String v)  { requirementId = v; return this; }
        public Builder title(String v)          { title = v; return this; }
        public Builder type(String v)           { type = v; return this; }
        public Builder priority(String v)       { priority = v; return this; }
        public Builder module(String v)         { module = v; return this; }
        public Builder preconditions(String v)  { preconditions = v; return this; }
        public Builder steps(String v)          { steps = v; return this; }
        public Builder testData(String v)       { testData = v; return this; }
        public Builder expectedResult(String v) { expectedResult = v; return this; }
        public Builder actualResult(String v)   { actualResult = v; return this; }
        public Builder status(String v)         { status = v; return this; }
        public Builder originalAC(String v)     { originalAC = v; return this; }
        public TestCaseModel build()            { return new TestCaseModel(this); }
    }

    public String getTcId()           { return tcId; }
    public String getTicketId()       { return ticketId; }
    public String getRequirementId()  { return requirementId; }
    public String getTitle()          { return title; }
    public String getType()           { return type; }
    public String getPriority()       { return priority; }
    public String getModule()         { return module != null ? module : "General"; }
    public String getPreconditions()  { return preconditions; }
    public String getSteps()          { return steps; }
    public String getTestData()       { return testData; }
    public String getExpectedResult() { return expectedResult; }
    public String getActualResult()   { return actualResult != null ? actualResult : ""; }
    public String getStatus()         { return status; }
    public String getOriginalAC()     { return originalAC; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | Priority: %s", type, tcId, title, priority);
    }
}
