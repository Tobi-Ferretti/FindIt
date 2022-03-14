package com.FindIt.finditpflanzenfinden;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {

    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;
    private final String TAG = "DRIVE_TAG";
    private String fileId;


    public DriveServiceHelper(Drive driveService) {

        mDriveService = driveService;
    }

    /**
     * Creates a text file in the user's My Drive folder and returns its file ID.
     */
    public Task<GoogleDriveFileHolder> createFile(String folderId, String filename) {
        return Tasks.call(mExecutor, () -> {
            GoogleDriveFileHolder googleDriveFileHolder = new GoogleDriveFileHolder();

            List<String> root;
            if (folderId == null) {

                root = Collections.singletonList("root");

            } else {

                root = Collections.singletonList(folderId);
            }
            File metadata = new File()
                    .setParents(root)
                    .setMimeType("text/plain")
                    .setName(filename);

            File googleFile = mDriveService.files().create(metadata).execute();
            if (googleFile == null) {

                throw new IOException("Null result when requesting file creation.");
            }
            googleDriveFileHolder.setId(googleFile.getId());
            return googleDriveFileHolder;
        });
    }


// TO CREATE A FOLDER

    public Task<GoogleDriveFileHolder> createFolder(String folderName, @Nullable String folderId) {
        return Tasks.call(mExecutor, () -> {

            GoogleDriveFileHolder googleDriveFileHolder = new GoogleDriveFileHolder();

            List<String> root;
            if (folderId == null) {

                root = Collections.singletonList("root");

            } else {

                root = Collections.singletonList(folderId);
            }
            File metadata = new File()
                    .setParents(root)
                    .setMimeType("application/vnd.google-apps.folder")
                    .setName(folderName);

            File googleFile = mDriveService.files().create(metadata).execute();
            if (googleFile == null) {
                throw new IOException("Null result when requesting file creation.");
            }
            googleDriveFileHolder.setId(googleFile.getId());
            return googleDriveFileHolder;
        });
    }


    public Task<Void> downloadFile(java.io.File targetFile, String fileId) {
        return Tasks.call(mExecutor, () -> {

            // Retrieve the metadata as a File object.
            OutputStream outputStream = new FileOutputStream(targetFile.getPath());
            mDriveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
            outputStream.flush();
            outputStream.close();
            return null;
        });
    }

    public Task<Void> deleteFolderFile(String fileId) {
    return Tasks.call(mExecutor, () -> {

        // Retrieve the metadata as a File object.
        if (fileId != null) {
            mDriveService.files().delete(fileId).execute();

        }

        return null;

    });
    }

// TO LIST FILES


    public Task<List<File>> listDriveImageFiles() throws IOException{
        return Tasks.call(mExecutor, new Callable<List<File>>() {
                    @Override
                    public List<File> call() throws Exception {
                        FileList result;
                        String pageToken = null;
                        do {
                            result = mDriveService.files().list()
/*.setQ("mimeType='image/png' or mimeType='text/plain'")This si to list both image and text files. Mind the type of image(png or jpeg).setQ("mimeType='image/png' or mimeType='text/plain'") */
                                    .setSpaces("drive")
                                    .setFields("nextPageToken, files(id, name, webViewLink, size, owners, permissions)").setQ("mimeType='text/html'")
                                    .setPageToken(pageToken)
                                    .execute();

                            pageToken = result.getNextPageToken();
                        } while (pageToken != null);

                        return result.getFiles();
                    }
                });
    }


// TO UPLOAD A FILE ONTO DRIVE

    public Task<GoogleDriveFileHolder> uploadFile(final java.io.File localFile,
                                                  final String mimeType, @Nullable final String folderId) {
        return Tasks.call(mExecutor, new Callable<GoogleDriveFileHolder>() {
            @Override
            public GoogleDriveFileHolder call() throws Exception {
                // Retrieve the metadata as a File object.

                List<String> root;
                if (folderId == null) {
                    root = Collections.singletonList("root");
                } else {

                    root = Collections.singletonList(folderId);
                }

                File metadata = new File()
                        .setParents(root)
                        .setMimeType(mimeType)
                        .setName(localFile.getName());

                FileContent fileContent = new FileContent(mimeType, localFile);

                File fileMeta = mDriveService.files().create(metadata,
                        fileContent).execute();
                GoogleDriveFileHolder googleDriveFileHolder = new
                        GoogleDriveFileHolder();
                fileContent.setType("UFT-8");
                googleDriveFileHolder.setId(fileMeta.getId());
                fileId=googleDriveFileHolder.getId();
                googleDriveFileHolder.setWebLink(fileMeta.getWebViewLink());
                if (fileMeta.getSize()!=null) {
                    googleDriveFileHolder.setSize(fileMeta.getSize());
                }else {
                    googleDriveFileHolder.setSize(0);
                }
                googleDriveFileHolder.setName(fileMeta.getName());

                return googleDriveFileHolder;
            }
        });
    }

    public Task<Void> updateFile(java.io.File file) {
        return Tasks.call(mExecutor, () -> {
            try {

                FileContent mediaContent = new FileContent("text/html", file);


                mDriveService.files().update(fileId, null, mediaContent).execute();
            } catch (Exception e) {
                System.out.println("An error occurred: " + e);
                return null;
            }
            return null;
        });
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
