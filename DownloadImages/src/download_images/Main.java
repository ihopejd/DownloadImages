package download_images;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    private static final String IN_FILE_TXT = "src\\download_images\\inFile.txt";
    private static final String OUT_FILE_TXT = "src\\download_images\\outFile.txt";
    private static final String PATH_TO_IMAGES = "C:\\Users\\BIG GUY\\IdeaProjects\\DownloadImages\\src\\images\\";
    private static String Url;

    public static void main(String[] args) {

        downloadImages();
        picturesSave();

    }

    private static void downloadUsingNIO(String strUrl, String file) throws IOException {
        URL url = new URL(strUrl);
        ReadableByteChannel byteChannel = Channels.newChannel(url.openStream());
        FileOutputStream stream = new FileOutputStream(file);
        stream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
        stream.close();
        byteChannel.close();
    }

    private static void downloadImages() {
        try (BufferedReader inFile = new BufferedReader(new FileReader(IN_FILE_TXT));
             BufferedWriter outFile = new BufferedWriter(new FileWriter(OUT_FILE_TXT))) {
            while ((Url = inFile.readLine()) != null) {
                URL url = new URL(Url);

                String result;

                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    result = bufferedReader.lines().collect(Collectors.joining("\n"));
                }
                Pattern email_pattern = Pattern.compile("(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg)");
                Matcher matcher = email_pattern.matcher(result);
                int i = 0;
                while (matcher.find() && i < 2) {
                    outFile.write(matcher.group() + "\r\n");
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void picturesSave() {
        try (BufferedReader imageFile = new BufferedReader(new FileReader(OUT_FILE_TXT))) {
            String image;
            int count = 0;
            try {
                while ((image = imageFile.readLine()) != null) {
                    downloadUsingNIO(image, PATH_TO_IMAGES + String.valueOf(count) + ".jpg");
                    count++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}