package io.zbx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PageDTO {

    @JsonProperty(value = "page_token")
    private String pageToken;

    private List<FileDTO> files;

    public String getPageToken() {
        return pageToken;
    }

    public void setPageToken(String pageToken) {
        this.pageToken = pageToken;
    }

    public List<FileDTO> getFiles() {
        return files;
    }

    public void setFiles(List<FileDTO> files) {
        this.files = files;
    }
}
