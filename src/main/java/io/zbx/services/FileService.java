package io.zbx.services;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import io.zbx.dto.FileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private TokenService tokenService;

    public List<FileDTO> findAll() throws Exception {

        List<FileDTO> files = new ArrayList<>();

        String pageToken = null;

        do {

            FileList result = tokenService.getDrive().files().list()
//                    .setQ("mimeType = 'application/vnd.google-apps.folder'")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();

            result.getFiles().forEach(f -> files.add(new FileDTO(f.getId(), f.getName(), f.getMimeType())));

            pageToken = result.getNextPageToken();

        } while (pageToken != null);

        return files;
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

    public void createFolder(FileDTO fileDTO) throws Exception {
        File fileMetadata = new File();
        fileMetadata.setParents(Collections.singletonList(fileDTO.getId()));
        fileMetadata.setName(fileDTO.getName());
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        this.tokenService.getDrive().files().create(fileMetadata).setFields("id").execute();
    }

//    public ByteArrayOutputStream listAllFiles() throws Exception {
//
//        FileList result = tokenService.getDrive().files().list().setPageSize(10).setFields("nextPageToken, files(id, name)").execute();
//
//        List<FileDTO> files = result.getFiles();
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        if (files != null) {
//
//            for (File file : files) {
//
//                System.out.println(file.getName());
//
//                if (file.getName().equals("file_example_MP3_2MG.mp3")) {
//
//                    tokenService.getDrive().files().get(file.getId()).executeMediaAndDownloadTo(outputStream);
//                }
//            }
//
//            return outputStream;
//
//        } else {
//            throw new Exception("There is no files to show.");
//        }
//    }

}
