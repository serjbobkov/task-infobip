package ru.bobkov.infobip.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ru.bobkov.infobip.rest.dto.FileUploadResponse;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Api("File upload rest service api")
@Path("/api")
public interface FileUploadRestService {

    @POST
    @Path("/v1/upload")
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Upload file to system"
    )
//    @ApiResponses({
//            @ApiResponse(code = 404, message = "Application not found for contact with target id or contact with target id not found")
//    })
    Response fileUpload(@Context HttpServletRequest request,
                        @ApiParam(value = "Header 'X-Upload-File' with file name", required = true) @HeaderParam("X-Upload-File") String fileName,
                        @ApiParam(value = "Header 'Content-Length'", required = true) @HeaderParam(HttpHeaders.CONTENT_LENGTH) Long contentLength
    );


    @GET
    @Path("/v1/upload/progress")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Getting current in-progress 'bytes uploaded' information"
    )
//    @ApiResponses({
//            @ApiResponse(code = 404, message = "Application not found for contact with target id or contact with target id not found")
//    })
    FileUploadResponse getCurrentUploadsStatus();


    @GET
    @Path("/v1/upload/duration")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(
            value = "getting transfer duration metrics for all finished uploads"
    )
//    @ApiResponses({
//            @ApiResponse(code = 404, message = "Application not found for contact with target id or contact with target id not found")
//    })
    String getFinishedUploadsDurationMetrics();


}