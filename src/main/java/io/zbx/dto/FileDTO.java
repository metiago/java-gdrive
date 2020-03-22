package io.zbx.dto;


import javax.validation.constraints.NotBlank;

public class FileDTO {

    private String id;

    @NotBlank
    private String name;

    private String mimeType;

    public FileDTO() {

    }

    public FileDTO(String id, String name, String mimeType) {
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
    }

    public FileDTO(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}

