package com.vk.importimages;

import com.vk.importimages.ftp.Client;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPFile;

public class Renamer {

    private static Pattern p = Pattern.compile(".*?(\\d{8})(.+)(\\d{6}).*?");

    public void run(Client client) throws IOException {
        for (FTPFile file : client.listFiles("/")) {
            String name = file.getName();
            System.out.println(name);
            String newName = toStandard(name);
            if (name.equals(newName)) {
                System.out.println(String.format("%s already renamed", name));
                continue;
            }
            boolean renamed = client.rename(name, newName);
            System.out.println(String.format("%s %srenamed to %s", name, (renamed ? "" : "not "), toStandard(name)));
        }
    }

    public static void main(String[] args) throws IOException {
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

    }

    @SuppressWarnings("unchecked")
    public static List<File> getFiles(String dir) {
        String[] extensions = {"jpg", "jpeg"};
        return (List<File>) FileUtils.listFiles(new File(dir), extensions, true);
    }

    public static String toStandard(String input) {
        Matcher m = p.matcher(input);
        if (m.matches()) {
            String date = m.group(1);
            String sep = m.group(2);
            String time = m.group(3);
            String newDate = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
            String newTime = time.substring(0, 2) + "-" + time.substring(2, 4) + "-" + time.substring(4, 6);
            input = input.replace(date + sep, newDate + " ").replace(time, newTime);
        }
        return addMobiMarker(input);
    }
    
    private static String addMobiMarker(String input) {
        if (input.contains("_mobi")) {
            return input;
        }
        int pointIndex = input.lastIndexOf('.');
        return input.substring(0, pointIndex) + "_mobi" + input.substring(pointIndex);                    
    }

}
