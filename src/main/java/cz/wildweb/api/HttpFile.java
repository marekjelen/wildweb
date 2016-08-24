package cz.wildweb.api;

import java.io.File;

public interface HttpFile {

    String name();

    String type();

    byte[] content();

    File file();

}
