package io.zbx.endpoints;

import io.zbx.dto.FileDTO;
import io.zbx.dto.FolderDTO;
import io.zbx.dto.PageDTO;
import io.zbx.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
public class FileEndpoint implements FileEndpointInterface {

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<PageDTO> findAll(@RequestParam(value = "pageToken", required = false) String pageToken) throws Exception {

        PageDTO page = fileService.findAll(pageToken);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileDTO> findById(@RequestParam(value = "id") String id) throws Exception {

        FileDTO file = fileService.findByID(id);
        return new ResponseEntity<>(file, HttpStatus.OK);
    }

    @RequestMapping(value = "/name", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FileDTO>> findByName(@RequestParam(value = "name") String name) throws Exception {

        List<FileDTO> files = fileService.findByName(name);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@RequestParam(value = "id") String id) throws Exception {

        fileService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> emptyTrash() throws Exception {

        fileService.emptyTrash();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/contains", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FileDTO>> contains(@RequestParam(value = "text") String text) throws Exception {

        List<FileDTO> files = fileService.contains(text);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @RequestMapping(value = "/in", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FileDTO>> in(@RequestParam(value = "id") String id) throws Exception {

        List<FileDTO> files = fileService.in(id);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @RequestMapping(value = "/folder", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileDTO> createFolder(@Valid @RequestBody FolderDTO folderDTO) throws Exception {

        FileDTO result = fileService.createFolder(folderDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/folder/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileDTO> updateFolder(@Valid @RequestBody FolderDTO folderDTO,
                                                @RequestParam(value = "id") String id) throws Exception {

        FileDTO result = fileService.updateFolder(folderDTO, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/upload/{id}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileDTO> upload(@RequestPart(value = "file") MultipartFile files,
                                          @RequestParam(value = "id", required = false) String id) throws Exception {

        FileDTO fileDTO = fileService.upload(files, id);
        return new ResponseEntity<>(fileDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public ResponseEntity<byte[]> download(@RequestParam(value = "id") String id) throws Exception {

        FileDTO file = fileService.download(id);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", file.getMimeType());
        headers.set("Content-Disposition", "attachment; filename=\"" + file.getName());
        return new ResponseEntity<>(file.getBinary(), headers, HttpStatus.OK);
    }
}
