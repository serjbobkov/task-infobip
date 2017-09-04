package ru.bobkov.infobip.rest.dto;


import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;


public class FileUpload {

    @JsonProperty
    private String id;

    @JsonProperty
    private long size;

    @JsonProperty
    private long uploaded = 0;


    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private long time = 0;


    public FileUpload(final String id, final long size) {
        this.id = id;
        this.size = size;
    }

    public long getUploaded() {
        return uploaded;
    }

    public void setUploaded(final long uploaded) {
        this.uploaded = uploaded;
    }

    public String getId() {
        return id;
    }

    public long getSize() {
        return size;
    }

    @JsonIgnore
    public long getTime() {
        return time;
    }

    @JsonIgnore
    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FileUpload that = (FileUpload) o;

        if (size != that.size) {
            return false;
        }
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "FileUpload{" +
                "id='" + id + '\'' +
                ", size=" + size +
                ", uploaded=" + uploaded +
                ", time=" + time +
                '}';
    }

    public FileUpload copy() {
        FileUpload fl = new FileUpload(id, size);
        fl.setUploaded(uploaded);
        fl.setTime(time);
        return fl;
    }


}
