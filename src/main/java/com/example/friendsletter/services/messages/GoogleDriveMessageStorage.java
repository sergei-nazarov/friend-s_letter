package com.example.friendsletter.services.messages;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Profile("prod")
@Slf4j
public class GoogleDriveMessageStorage implements MessageStorage {
    private static final int ID_CAPACITY = 3;//I don't want to request a lot of ids from Google
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    @Value("${messages.store.folder}")
    private String folderName;

    private Drive service;
    private String folderId;
    @Value("${spring.application.name}")
    private String APPLICATION_NAME;
    @Value("${messages.store.credential-path:/credentials.json}")
    private String CREDENTIALS_FILE_PATH;
    private BlockingQueue<String> ids;

    @PostConstruct
    private void initDrive() {
        InputStream credentialStream = GoogleDriveMessageStorage.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (credentialStream == null) {
            throw new RuntimeException("Google Drive credential file hasn't been found in resources" + CREDENTIALS_FILE_PATH);
        }
        try {
            final GoogleCredentials googleCredentials = ServiceAccountCredentials
                    .fromStream(credentialStream)
                    .createScoped(Collections.singletonList(DriveScopes.DRIVE));
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(googleCredentials);
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            service = new Drive.Builder(httpTransport, GsonFactory.getDefaultInstance(), requestInitializer)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        ids = new ArrayBlockingQueue<>(ID_CAPACITY);
        runGoogleDriveIdRequester();
        findFolderId();
    }

    private void runGoogleDriveIdRequester() {
        new Thread(() -> {
            while (true) {
                try {
                    int requestCount = ID_CAPACITY + 1;
                    log.info("Request " + requestCount + " file ids from Google Drive for storing messages");
                    List<String> newMessageIds = service.files().generateIds().setCount(requestCount).execute().getIds();
                    for (String newMessageId : newMessageIds) {
                        ids.put(newMessageId); //heart
                    }
                } catch (IOException e) {
                    log.error("Error during getting ids from Google Drive", e);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "Drive updater").start();
    }

    @Override
    public String save(InputStream message) {
        try {
            String fileId = ids.take();
            saveLetterInStorage(message, fileId);
            return fileId;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveLetterInStorage(InputStream message, String fileId) throws IOException {
        executorService.execute(() -> {
            File fileMetadata = new File()
                    .setId(fileId)
                    .setParents(Collections.singletonList(folderId))
                    .setName(fileId);
            InputStreamContent streamContent = new InputStreamContent("text/plain", message);
            try {
                service.files().create(fileMetadata, streamContent).setFields("id").execute();
            } catch (IOException e) {
                throw new RuntimeException(e);//todo retry
            }
        });
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

    @Override
    public InputStream readAsStream(String fileId) throws FileNotFoundException {
        try {
            return service.files().get(fileId).executeMediaAsInputStream();
        } catch (IOException e) {
            if (e instanceof GoogleJsonResponseException googleException) {
                if (googleException.getStatusCode() == 404) {
                    throw new FileNotFoundException(fileId);
                }
            }
            throw new RuntimeException(e);
        }
    }
}

