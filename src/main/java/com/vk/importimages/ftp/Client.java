package com.vk.importimages.ftp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class Client {

    private FTPClient ftpClient;
    private final String server;
    private final int port;
    private final String user;
    private final String pass;
    
    public Client(String server, int port, String user, String pass) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public Client(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
        this.server = null;
        this.port = 0;
        this.user = null;        
        this.pass = null;        
    }

    public FTPFile[] listFiles(String dir) throws IOException {
        return getFtpClient().listFiles(dir);
    }

    public boolean rename(String from, String to) throws IOException {
        boolean renamed = getFtpClient().rename(from, to);
        String[] replyStrings = ftpClient.getReplyStrings();        
        for (String replyString : replyStrings) {
            System.out.println(replyString);
        }
        return renamed;
    }

    public boolean downloadFile(String remoteFile, String localFile) throws FileNotFoundException, IOException {
//        String remoteFile1 = "/test/video.mp4";
        File downloadFile1 = new File(localFile);//abs path
        try (OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1))) {
            return getFtpClient().retrieveFile(remoteFile, outputStream1);
        }
    }

    private FTPClient getFtpClient() throws IOException {
        if (ftpClient != null) {
            return ftpClient;
        }

        ftpClient = new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.login(user, pass);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        
        return ftpClient;

    }
}
