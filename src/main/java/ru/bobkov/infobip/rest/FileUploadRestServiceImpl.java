package ru.bobkov.infobip.rest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.DeferredFileOutputStream;
import ru.bobkov.infobip.rest.dto.FileUpload;
import ru.bobkov.infobip.rest.dto.FileUploadResponse;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;


public class FileUploadRestServiceImpl implements FileUploadRestService {

    private final ConcurrentHashMap<FileUpload, CountingInputStream> uploadsInProgress = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<FileUpload, Long> uploaded = new ConcurrentHashMap<>();

    private final String path;

    private boolean isPathExists = false;

    public FileUploadRestServiceImpl(final String path) {
        this.path = path;
    }


    @Override
    public Response fileUpload(final HttpServletRequest request, final String fileName, final Long contentLength) {
        System.out.println("[FileUpload!!!!]");

        long startTime = System.currentTimeMillis();

        DeferredFileOutputStream output = null;

        FileUpload upload = null;
        try {

            InputStream is = request.getInputStream();

            if (is != null) {

                String fileId = fileName + "-" + System.currentTimeMillis();
                File file = new File(path + fileId);

                FileUtils.forceMkdirParent(file);

                System.out.println("file.getAbsolutePath() = " + file.getAbsolutePath());
                System.out.println("fileName = " + fileName);
                System.out.println("contentLength = " + contentLength);

                upload = new FileUpload(fileId, contentLength);
                CountingInputStream countingInputStream = new CountingInputStream(is);

                uploadsInProgress.put(upload, countingInputStream);

                output = new DeferredFileOutputStream(1024 * 1024, file);
                long count = IOUtils.copyLarge(countingInputStream, output);
                output.flush();

                long uploadTime = System.currentTimeMillis() - startTime;
                upload.setUploaded(count);

                if (count != contentLength) {
                    return Response.serverError().build();
                }

                uploaded.put(upload, uploadTime);

            }
        } catch (IOException e) {
            return Response.serverError().build();
        } finally {

            if (upload != null) {
                uploadsInProgress.remove(upload);
            }

            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    //skip it and log it
                }
            }
        }


        return Response.ok().build();
    }

    @Override
    public FileUploadResponse getCurrentUploadsStatus() {

        LinkedList<FileUpload> uploadList = new LinkedList<>();

        for (FileUpload fileUpload : uploadsInProgress.keySet()) {
            CountingInputStream is = uploadsInProgress.get(fileUpload);

            FileUpload copy = fileUpload.copy();
            copy.setUploaded(is.getByteCount());

            uploadList.add(copy);
        }

        return new FileUploadResponse(uploadList);
    }

    @Override
    public String getFinishedUploadsDurationMetrics() {

        StringBuilder b = new StringBuilder();

        for (FileUpload fl : uploaded.keySet()) {
            Long time = uploaded.get(fl);
            b.append("upload_duration{id=\"" + fl.getId() + "\"} " + 1567.0 + "\n");
        }

        return b.toString();

    }


}
