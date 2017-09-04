package ru.bobkov.infobip.rest.dto;

public class FileUpload {

    private String id;
    private long size;
    private long uploaded = 0;


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
        return "FileUpload{"
                + "id='" + id + '\''
                + ", size=" + size
                + ", uploaded=" + uploaded
                + '}';
    }


    public FileUpload copy() {
        FileUpload fl = new FileUpload(id, size);
        fl.setUploaded(uploaded);
        return fl;
    }
}
