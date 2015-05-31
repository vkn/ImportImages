package com.vk.importimages;

import com.vk.importimages.ftp.Client;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.net.ftp.FTPFile;
import persistence.PersistenceService;

public class Importer {

    private static final Pattern p = Pattern.compile(".*?(\\d{4})-(\\d{2})-(\\d{2}).*?");
    private final String baseLocalPath;

    private final PersistenceService persistence;
    private final Client client;

    public Importer(PersistenceService persistence, Client client, String baseLocalPath) {
        this.persistence = persistence;
        this.client = client;
        this.baseLocalPath = baseLocalPath;
    }

    public void run() throws SQLException, IOException {
        int imported = 0;
        int skipped = 0;
        List<String> ignored = new ArrayList<>(10);
        FTPFile[] files = client.listFiles("/");
        int all = files.length;
        for (FTPFile file : files) {

            String name = file.getName();
            if (persistence.isImported(name)) {
                skipped++;
                continue;
            }
            if (name.endsWith(".mp4")) {
                ignored.add(name);
                continue;
            }
            importFile(name);
            imported++;
        }
        System.out.println("All " + all);
        System.out.println("Imported " + imported);
        System.out.println("Skipped " + skipped);
        System.out.println("Ignored: ");
        ignored.forEach(i -> System.out.println(i));
    }

    private void importFile(String name) throws IOException, SQLException {
        String targetDir = getTargetDirPath(name);
        File dir = new File(targetDir);
        dir.mkdirs();
        client.downloadFile(name, targetDir);
        persistence.markAsImported(name);
        String ftpSubDirFromFileName = getFtpSubDirFromFileName(name);
        if (ftpSubDirFromFileName != null) {
            client.move(name, ftpSubDirFromFileName);
        }
    }

    public File getTargetDir(String year, String fileDate) {
        File dir = new File(baseLocalPath + "/" + year);
        return Arrays.stream(dir.listFiles(f -> f.isDirectory() && f.getName().startsWith(fileDate)))
                .findAny().orElse(new File(baseLocalPath + "/" + year + "/" + fileDate));
    }

    public String getTargetDirPath(String name) {
        Matcher m = p.matcher(name);
        if (m.matches()) {
            String year = m.group(1);
            String fileDate = m.group(1) + "-" + m.group(2) + "-" + m.group(3);
            return getTargetDir(year, fileDate).getAbsolutePath();
        }
        return null;
    }

    public static String getFtpSubDirFromFileName(String name) {
        Matcher m = p.matcher(name);
        if (m.matches()) {
            return m.group(1) + "-" + m.group(2) + "-" + m.group(3);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Properties prop = new Properties();
        FileInputStream input = new FileInputStream("config.properties");
        prop.load(input);
        Client client = new Client(
                prop.getProperty("ftp.server"),
                Integer.parseInt(prop.getProperty("ftp.port")),
                prop.getProperty("ftp.username"),
                prop.getProperty("ftp.password")
        );

        Renamer renamer = new Renamer();
        renamer.run(client);

        Importer importer = new Importer(
            new PersistenceService(prop.getProperty("sqllite.dbpath")), client, prop.getProperty("basepath"));
        importer.run();
        client.shutDown();
    }

}
