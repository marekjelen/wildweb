package cz.wildweb.server.netty;

import cz.wildweb.api.HttpFile;
import io.netty.handler.codec.http.multipart.FileUpload;

import java.io.File;
import java.io.IOException;

public class NettyFile implements HttpFile {

    private final FileUpload upload;

    public NettyFile(FileUpload upload) {
        this.upload = upload;
    }

    @Override
    public String name() {
        return upload.getFilename();
    }

    @Override
    public String type() {
        return upload.getContentType();
    }

    @Override
    public byte[] content() {
        try {
            return upload.get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public File file() {
        try {
            return upload.getFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
