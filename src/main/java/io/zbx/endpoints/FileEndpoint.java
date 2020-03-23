package io.zbx.endpoints;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.zbx.dto.FileDTO;
import io.zbx.dto.PageDTO;
import io.zbx.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/files")
public class FileEndpoint {

    @Autowired
    private FileService fileService;

    @ApiOperation(value = "Find all files using pagination limit of 10 records.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "It returns an array of files."),
                           @ApiResponse(code = 401, message = "Unauthorized.")})
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<PageDTO> findAll(@ApiParam(value = "Token to the next page")
                                           @RequestParam(value = "pageToken", required = false) String pageToken) throws Exception {
        PageDTO page = fileService.findAll(pageToken);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @ApiOperation(value = "Find files that match a given name.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "If it is a folder it'll return a list of files otherwise an empty array."),
                           @ApiResponse(code = 401, message = "Unauthorized."),
                           @ApiResponse(code = 404, message = "File not found")})
    @RequestMapping(value = "/name", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FileDTO>> findByName(@ApiParam(value = "File name", required = true)
                                                    @RequestParam(value = "name") String name) throws Exception {

        List<FileDTO> files = fileService.findByName(name);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @ApiOperation(value = "Find files which contains a given text.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "If it is a folder it'll return a list of files otherwise an empty array."),
                           @ApiResponse(code = 401, message = "Unauthorized."),
                           @ApiResponse(code = 404, message = "File not found.")})
    @RequestMapping(value = "/contains", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FileDTO>> contains(@ApiParam(value = "Any character", required = true)
                                                  @RequestParam(value = "text") String text) throws Exception {
        List<FileDTO> files = fileService.contains(text);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @ApiOperation(value = "List all files inside a folder based on its ID.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "If it is a folder it'll return a list of files which is " +
            "                                                  inside it otherwise it's a file and return an empty array."),
                           @ApiResponse(code = 401, message = "Unauthorized."),
                           @ApiResponse(code = 404, message = "File not found.")})
    @RequestMapping(value = "/in", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FileDTO>> in(@ApiParam(value = "ID of a existing file", required = true)
                                            @RequestParam(value = "id") String id) throws Exception {
        List<FileDTO> files = fileService.in(id);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @ApiOperation(value = "Create a new folder in the drive.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Folder has been created successfully."),
                           @ApiResponse(code = 401, message = "Unauthorized.")})
    @RequestMapping(value = "/folder", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createFolder(@Valid @RequestBody FileDTO fileDTO) throws Exception {
        fileService.createFolder(fileDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Upload a new file to drive.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Returns the file ID."),
                           @ApiResponse(code = 401, message = "Unauthorized.")})
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileDTO> add(@RequestPart(value = "file") MultipartFile files) throws Exception {
        FileDTO fileDTO = fileService.upload(files);
        return new ResponseEntity<>(fileDTO, HttpStatus.OK);
    }
}