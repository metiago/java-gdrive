package io.zbx.endpoints;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.zbx.dto.FileDTO;
import io.zbx.dto.FolderDTO;
import io.zbx.dto.PageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


interface FileEndpointInterface {

    @ApiOperation(value = "Find all files with a predefined pagination limit of 10 records")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Returns a list of files"),
                           @ApiResponse(code = 401, message = "Unauthorized")})
    ResponseEntity<PageDTO> findAll(@ApiParam(value = "Token to the next page") String pageToken) throws Exception;


    @ApiOperation(value = "Returns a file by id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Returns the file information"),
                           @ApiResponse(code = 401, message = "Unauthorized"),
                           @ApiResponse(code = 404, message = "Not found")})
    ResponseEntity<FileDTO> findById(@ApiParam(value = "id of a existing file", required = true) String id) throws Exception;


    @ApiOperation(value = "Search for files that match a given name.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Returns a list of files."),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")})
    ResponseEntity<List<FileDTO>> findByName(@ApiParam(value = "File name", required = true) String name) throws Exception;


    @ApiOperation(value = "Search for files which contains any character and ints name")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Returns a list of files"),
                           @ApiResponse(code = 401, message = "Unauthorized"),
                           @ApiResponse(code = 404, message = "Not found")})
    ResponseEntity<List<FileDTO>> contains(@ApiParam(value = "Any character", required = true) String text) throws Exception;


    @ApiOperation(value = "List all files using an IN clause matching its id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "For folders it'll return a list of files which is " +
                                                              "inside of it otherwise it should be a file and return an empty array"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")})
    ResponseEntity<List<FileDTO>> in(@ApiParam(value = "id of a existing file", required = true) String id) throws Exception;


    @ApiOperation(value = "Create a new folder in the drive")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Folder has been created successfully"),
                           @ApiResponse(code = 400, message = "Name must be not empty"),
                           @ApiResponse(code = 401, message = "Unauthorized")})
    ResponseEntity<FileDTO> createFolder(FolderDTO folderDTO) throws Exception;


    @ApiOperation(value = "Create a new folder inside an existent one based on its id")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Folder has been created successfully"),
                           @ApiResponse(code = 404, message = "Not found If the id doesn't exists"),
                           @ApiResponse(code = 400, message = "Name must be not empty"),
                           @ApiResponse(code = 401, message = "Unauthorized")})
    ResponseEntity<FileDTO> updateFolder(FolderDTO folderDTO,  @ApiParam(value = "id of a existing folder", required = true)  String id) throws Exception;


    @ApiOperation(value = "Upload a new file to drive or into a folder if folder's id sent")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Returns the file id"),
                           @ApiResponse(code = 401, message = "Unauthorized")})
    ResponseEntity<FileDTO> upload(MultipartFile files, @ApiParam(value = "id of a existing folder") String id) throws Exception;


    @ApiOperation(value = "Download a file from drive")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Returns the binary file"),
                           @ApiResponse(code = 401, message = "Unauthorized")})
    ResponseEntity<byte[]> download(@ApiParam(value = "id of a existing file", required = true) String id) throws Exception;

}
