package org.itzstonlex.recon.http.app.util;

import org.itzstonlex.recon.http.app.HttpContextAttachment;
import org.itzstonlex.recon.http.app.HttpContextContent;
import org.itzstonlex.recon.http.app.HttpContextHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public final class ContextInitUtils {

    public static InputStream getInputStream(Class<?> classLoader, PathLevel pathLevel, String filePath) {
        switch (pathLevel) {
            case FILESYSTEM: {
                try {
                    File file = Paths.get(filePath).toFile();
                    return new FileInputStream(file);
                }
                catch (IOException exception) {
                    exception.printStackTrace();
                }
                break;
            }

            case CLASSPATH: {
                return classLoader.getResourceAsStream(filePath);
            }
        }

        return null;
    }

    public static void initContextContent(HttpContextHandler httpContextHandler) {
        HttpContextContent contextContent = httpContextHandler.getClass().getAnnotation(HttpContextContent.class);

        if (contextContent != null) {
            InputStream contentStream = ContextInitUtils.getInputStream(httpContextHandler.getClass(),
                    contextContent.level(), contextContent.filePath());

            if (contentStream != null) {
                httpContextHandler.setContentStream(contentStream);
            }
        }
    }

    public static void initContextAttachments(HttpContextHandler httpContextHandler) {
        HttpContextAttachment attachment = httpContextHandler.getClass().getAnnotation(HttpContextAttachment.class);

        if (attachment == null) {
            return;
        }

        for (String filePath : attachment.filePath()) {
            InputStream attachmentStream = ContextInitUtils.getInputStream(httpContextHandler.getClass(),
                    attachment.level(), filePath);

            if (attachmentStream != null) {
                httpContextHandler.addAttachmentStream(filePath, attachmentStream);
            }
        }
    }

    public static void initContextInstance(HttpContextHandler httpContextHandler) {
        initContextContent(httpContextHandler);
        initContextAttachments(httpContextHandler);
    }

}
