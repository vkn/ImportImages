package com.vk.importimages.ftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class Client {

    private final FTPClient ftpClient;
    private final String server;
    private final int port;
    private final String user;
    private final String pass;

    public Client(String server, int port, String user, String pass) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.pass = pass;
        ftpClient = new FTPClient();
    }

    public Client(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
        this.server = null;
        this.port = 0;
        this.user = null;
        this.pass = null;
    }

    public FTPFile[] listFiles(String dir) throws IOException {
        return getFtpClient().listFiles(dir, f -> f.isFile());
    }

    public boolean rename(String from, String to) throws IOException {
        return getFtpClient().rename(from, to);
    }
    
    public boolean move(String filename, String targetDir) throws IOException {
        getFtpClient().makeDirectory(targetDir);        
        return getFtpClient().rename(filename, targetDir + "/" + filename);
    }
    
    public String[] getReplyString() throws IOException {
        return getFtpClient().getReplyStrings();
    }

    public boolean downloadFile(String remoteFilePath, String localDir) throws FileNotFoundException, IOException {
        FileOutputStream outputStream = new FileOutputStream(new File(localDir + "/" + remoteFilePath));
        getFtpClient().setDataTimeout(10000);
        boolean retrieveFile = false;
        try {
            retrieveFile = getFtpClient().retrieveFile(remoteFilePath, outputStream);
        } catch (IOException iOException) {
            if (!(iOException.getCause() instanceof SocketTimeoutException)) {
                throw iOException;
            }   
        } finally {
            outputStream.close();
            shutDown();
        }
        return retrieveFile;
    }

    private FTPClient getFtpClient() throws IOException {

        if (ftpClient.isConnected()) {
            return ftpClient;
        }
        ftpClient.connect(server, port);
        ftpClient.login(user, pass);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setBufferSize(1024 * 1024);
        return ftpClient;
    }

    public void shutDown() throws IOException {
        if (ftpClient != null) {
            ftpClient.disconnect();
        }
    }
}
