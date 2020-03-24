package io.zbx.services;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import io.zbx.dto.FileDTO;
import io.zbx.dto.PageDTO;
import io.zbx.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FileService {

    private TokenService tokenService;

    private HttpSession httpSession;

    @Autowired
    public FileService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public PageDTO findAll(String pageToken) throws Exception {

        List<FileDTO> files = new ArrayList<>();

        FileList result = tokenService.getDrive().files().list()
                .setPageSize(10)
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name)")
                .setPageToken(pageToken)
                .execute();

        pageToken = result.getNextPageToken();

        for (File f : result.getFiles()) {
            files.add(new FileDTO(f.getId(), f.getName(), f.getMimeType()));
        }

        PageDTO pageDTO = new PageDTO();
        pageDTO.setPageToken(pageToken);
        pageDTO.setFiles(files);

        return pageDTO;
    }

    public List<FileDTO> contains(String text) throws Exception {

        List<FileDTO> files = new ArrayList<>();

        String pageToken = null;

        do {

            FileList result = tokenService.getDrive().files().list()
//                    .setQ("mimeType = 'application/vnd.google-apps.folder'")
                    .setQ(String.format("name contains '%s'", text))
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name, mimeType)")
                    .setPageToken(pageToken)
                    .execute();

            result.getFiles().forEach(f -> files.add(new FileDTO(f.getId(), f.getName(), f.getMimeType())));

            pageToken = result.getNextPageToken();

        } while (pageToken != null);

        return files;
    }

    public List<FileDTO> in(String id) throws Exception {

        List<FileDTO> files = new ArrayList<>();

        String pageToken = null;

        do {

            FileList result = tokenService.getDrive().files().list()
                    .setQ(String.format("'%s' in parents", id))
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();

            result.getFiles().forEach(f -> files.add(new FileDTO(f.getId(), f.getName(), f.getMimeType())));

            pageToken = result.getNextPageToken();

        } while (pageToken != null);

        return files;
    }

    public List<FileDTO> findByName(String name) throws Exception {

        List<FileDTO> files = new ArrayList<>();

        String pageToken = null;

        do {

            FileList result = tokenService.getDrive().files().list()
                    .setQ(String.format("name = '%s'", name))
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();

            result.getFiles().forEach(f -> files.add(new FileDTO(f.getId(), f.getName(), f.getMimeType())));

            pageToken = result.getNextPageToken();

        } while (pageToken != null);

        return files;
    }

    public FileDTO findByID(String id) throws Exception {

//        List<FileDTO> files = new ArrayList<>();

//        String pageToken = null;

//        do {

        File result = tokenService.getDrive().files().get(id)
//                    .setQ(String.format("id = '%s'", id))
//                    .setSpaces("drive")
//                    .setFields("nextPageToken, files(id, name)")
//                    .setPageToken(pageToken)
                .execute();

//            result.getFiles().forEach(f -> files.add(new FileDTO(f.getId(), f.getName(), f.getMimeType())));

//            pageToken = result.getNextPageToken();

//        } while (pageToken != null);

        return new FileDTO(result.getId(), result.getName(), result.getMimeType());
    }

    public void createFolder(FileDTO fileDTO) throws Exception {
        File fileMetadata = new File();
        fileMetadata.setParents(Collections.singletonList(fileDTO.getId()));
        fileMetadata.setName(fileDTO.getName());
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        this.tokenService.getDrive().files().create(fileMetadata).setFields("id").execute();
    }

    public FileDTO upload(MultipartFile files) throws Exception {

        String dest = System.getProperty("user.home") + "/" + files.getOriginalFilename();
        FileUtils.copyFileTo(dest, files.getBytes());

        File fileMetadata = new File();
        fileMetadata.setName(files.getOriginalFilename());
        fileMetadata.setMimeType(files.getContentType());
        java.io.File uploadedFile = new java.io.File(dest);

        FileContent mediaContent = new FileContent(files.getContentType(), uploadedFile);
        File file = this.tokenService.getDrive().files().create(fileMetadata, mediaContent).setFields("id").execute();

        uploadedFile.delete();

        return new FileDTO(file.getId());
    }

    public FileDTO download(String id) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        File file = tokenService.getDrive().files().get(id).execute();

        tokenService.getDrive().files().get(id).executeMediaAndDownloadTo(outputStream);

        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(file.getId());
        fileDTO.setName(file.getName());
        fileDTO.setMimeType(file.getMimeType());
        fileDTO.setBinary(outputStream.toByteArray());

        return fileDTO;
    }

}
