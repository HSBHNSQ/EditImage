package com.liubowang.photoretouch.FileBrowse;

import java.io.File;

/**
 * Created by heshaobo on 2017/11/3.
 */

public class FileInfo {

    public String fileUrl;
    public String thumUrl;
    public String fileSize;
    public String fileName;
    public File file;
    public boolean isSelected;

    private FileInfo(){
        super();
    }

    public FileInfo(File file){
        super();
        this.fileName = file.getName();
        this.file = file;
        this.fileUrl = file.getAbsolutePath();
    }

}
