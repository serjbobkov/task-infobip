package ru.bobkov.infobip.rest.dto;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class FileUploadResponse {

    private List<FileUpload> uploads;

    public FileUploadResponse(final List<FileUpload> uploads) {
        this.uploads = uploads;
    }

    @XmlElement(name = "uploads")
    public List<FileUpload> getUploads() {
        return uploads;
    }


}
