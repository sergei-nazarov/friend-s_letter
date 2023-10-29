package com.example.friendsletter.messages;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.GeneratedIds;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GoogleDriveMessageStorage implements MessageStorage {
    private static final String APPLICATION_NAME = "Friend's letter";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private Drive service;
    private String folderName = "friends_letter";
    private String folderId = "";


    public GoogleDriveMessageStorage() {
        initDrive();
    }

    private void initDrive() {
        InputStream credentialStream = GoogleDriveMessageStorage.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (credentialStream == null) {
            throw new RuntimeException("Google Drive credential file hasn't been found in resources" + CREDENTIALS_FILE_PATH);
        }
        try {
            final GoogleCredentials googleCredentials = ServiceAccountCredentials
                    .fromStream(credentialStream)
                    .createScoped(SCOPES);
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(googleCredentials);
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            service = new Drive.Builder(httpTransport, JSON_FACTORY, requestInitializer)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        findFolderId();
    }

    @Override
    public String save(InputStream message) {
        try {
            // todo replace with queue in order to request a lot of ids for single request
            GeneratedIds execute = service.files().generateIds().setCount(1).execute();
            String fileId = execute.getIds().get(0);

            File fileMetadata = new File()
                    .setId(fileId)
                    .setParents(Collections.singletonList(folderId))
                    .setName(fileId);
            InputStreamContent streamContent = new InputStreamContent("text/plain", message);
            File file = service.files().create(fileMetadata, streamContent).setFields("id").execute();
            return file.getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(String fileId, InputStream message) {
        InputStreamContent streamContent = new InputStreamContent("text/plain", message);
        try {
            service.files().update(fileId, null, streamContent).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public InputStream readAsStream(String fileId) {
        try {
            return service.files().get(fileId).executeMediaAsInputStream(); //todo check whether file exists
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(String fileId) {
        try {
            service.files().delete(fileId).execute();//todo add trash instead of delete
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public List<String> getAllFiles() {
        List<String> filesId = new ArrayList<>(50);
        String nextPageToken = "";
        do {
            try {
                FileList result = getFileList(nextPageToken, "'" + folderId + "' in parents");
                filesId.addAll(result.getFiles().stream().map(File::getId).toList());
                nextPageToken = result.getNextPageToken();
            } catch (IOException e) {
                throw new RuntimeException(e);
                //todo handle exception with retry
            }
        } while (nextPageToken != null);

        return filesId;
    }

    private void findFolderId() {
        String nextPageToken = "";
        do {
            try {
                FileList result = getFileList(nextPageToken, "name = '" + folderName + "'");
                Optional<File> optionalFileId = result.getFiles().stream().filter(x -> x.getName().equals(folderName)).findFirst();
                if (optionalFileId.isPresent()) {
                    folderId = optionalFileId.get().getId();
                    return;
                }
                nextPageToken = result.getNextPageToken();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (nextPageToken != null);
        throw new RuntimeException("Main folder id not found");
    }

    private FileList getFileList(String nextPageToken, String query) throws IOException {
        return service.files().list()
                .setPageToken(nextPageToken)
                .setQ(query)
                .setPageSize(1000)
                .setFields("nextPageToken, files(id, name, parents, mimeType)")
                .execute();
    }
}

