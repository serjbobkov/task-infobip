package ru.bobkov.infobip.rest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bobkov.infobip.rest.dto.FileUpload;
import ru.bobkov.infobip.rest.dto.FileUploadResponse;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


public class FileUploadRestServiceImpl implements FileUploadRestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadRestService.class);

    private final ConcurrentHashMap<FileUpload, CountingInputStream> uploadsInProgress = new ConcurrentHashMap<>();
    private final List<FileUpload> uploaded = Collections.synchronizedList(new LinkedList<FileUpload>());

    private final String path;

    public FileUploadRestServiceImpl(final String path) {
        this.path = path;
    }


    @Override
    public Response fileUpload(final HttpServletRequest request, @Nonnull final String fileName, @Nonnull final Long contentLength) {


        Objects.requireNonNull(fileName, "FileName should be not null");
        Objects.requireNonNull(contentLength, "ContentLength should be not null");


        long startTime = System.currentTimeMillis();

        DeferredFileOutputStream output = null;

        FileUpload upload = null;
        try {

            InputStream is = request.getInputStream();

            if (is != null) {

                String fileId = fileName + "-" + System.currentTimeMillis();
                File file = new File(path + fileId);

                FileUtils.forceMkdirParent(file);

                upload = new FileUpload(fileId, contentLength);
                CountingInputStream countingInputStream = new CountingInputStream(is);

                uploadsInProgress.put(upload, countingInputStream);

                output = new DeferredFileOutputStream(1024 * 1024, file);
                long count = IOUtils.copyLarge(countingInputStream, output);
                output.flush();


                upload.setUploaded(count);

                long uploadTime = System.currentTimeMillis() - startTime;
                upload.setTime(uploadTime);

                uploaded.add(upload);

            }


            LOGGER.info("File uploaded {}", fileName);

        } catch (IOException e) {
            LOGGER.error("Exception occured", e);
            return Response.serverError().build();
        } finally {

            if (upload != null) {
                uploadsInProgress.remove(upload);
            }

            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    LOGGER.error("Exception occured", e);
                }
            }
        }


        return Response.ok().build();
    }

    @Override
    public FileUploadResponse getCurrentUploadsStatus() {

        LinkedList<FileUpload> uploadList = new LinkedList<>();

        uploadsInProgress.forEach((key, is) -> {

            FileUpload copy = key.copy();
            copy.setUploaded(is.getByteCount());

            uploadList.add(copy);
        });

        return new FileUploadResponse(uploadList);
    }

    @Override
    public String getFinishedUploadsDurationMetrics() {

        StringBuilder b = new StringBuilder();

        uploaded.forEach(fl ->
                b.append("upload_duration{id=\"")
                        .append(fl.getId())
                        .append("\"} ")
                        .append(fl.getTime())
                        .append(".0")
                        .append("\n"));

        return b.toString();

    }


}
