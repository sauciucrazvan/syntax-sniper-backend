package dev.razvansauciuc.plg.responses;

public class SnippetResponse {
    private String code;
    private String language;
    private String[] options;

    public SnippetResponse(String code, String language, String[] options) {
        this.code = code;
        this.language = language;
        this.options = options;
    }

    public String getCode() { return code; }
    public String getLanguage() { return language; }
    public String[] getOptions() { return options; }
}