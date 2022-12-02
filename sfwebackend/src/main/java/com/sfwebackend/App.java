package com.sfwebackend;
//App java file 

//Jsoup 
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

//IO
import java.io.IOException;
import java.net.URL;
import java.io.*;

//Image
import javax.imageio.*;

//Awt
import java.awt.image.*;
import java.awt.Color;
import java.awt.Graphics2D;

//NIO
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//Util
import java.util.Hashtable;

//Zxing
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class App {
    public static void main(String[] args) throws Exception {

        System.out.println("Main Started");

        String[][] webscrapeSites = { { "https://engineering.arizona.edu/majors/software", "col-sm-7", "sfwemajor" }
        };

        for (int i = 0; i < webscrapeSites.length; i++) {

            String url = webscrapeSites[i][0];
            String element = webscrapeSites[i][1];
            String name = webscrapeSites[i][2];

            String storage = webscrape(url, "div." + element);

            htmlcreator(name, storage, element);

        }

        String[][] qrSites = {
                { "https://ua-trellis.force.com/uastudent/s/catcloud/services/calendar/?NetId=julianalincoln",
                        "jlAppointment" }
        };

        for (int i = 0; i < qrSites.length; i++) {

            String url = qrSites[i][0];
            String name = qrSites[i][1];

            QRgenerator(url, name);

        }

        pdfGrab("https://engineering.arizona.edu/pdfs/4-year-degree-plans/2022/SoftwareEngineering22-23.pdf",
                "testpdf");

        // Run webscrape & QRgenerator above for all required qr-codes and website info
        // Run the frontend executable beneath once all the datafiles have been made

        System.out.println("Main Ended");

    }
    // End of main

    public static String webscrape(String url, String element) {

        try {

            Document webdoc = Jsoup.connect(url).get();

            Elements content = webdoc.select(element);

            return content.toString();

        } catch (IOException IOexception) {

            IOexception.printStackTrace();

        }

        return "";
    }
    // End of webscrape

    public static void htmlcreator(String objname, String content, String element) {

        String filename = "html/" + objname + ".html";

        try {

            File htmlfile = new File(filename);

            boolean filemade = htmlfile.createNewFile();

            boolean fileexists = htmlfile.exists();

            if (filemade || fileexists) {

                System.out.println("File " + filename + " made");

                BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));

                writer.append(content);

                writer.close();

                Path filepath = Paths.get(filename);

                Charset charset = StandardCharsets.UTF_8;

                String htmlstuff = new String(Files.readAllBytes(filepath), charset);

                htmlstuff = htmlstuff.replaceAll(element, objname);

                Files.write(filepath, htmlstuff.getBytes(charset));

            } else {

                System.out.println("File " + filename + " does not exist");

            }

        } catch (IOException IOexception) {

            IOexception.printStackTrace();

        }
    }
    // End of htmlcreator

    public static void QRgenerator(String url, String name) throws WriterException, IOException {

        File QRFile = new File("qrs/" + name + ".png");
        System.out.println("File qrs/" + name + ".png made");

        int imgSize = 800;

        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintsHash = new Hashtable<>();
        hintsHash.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        QRCodeWriter qrWriter = new QRCodeWriter();

        BitMatrix bmatrix = qrWriter.encode(url, BarcodeFormat.QR_CODE, imgSize, imgSize, hintsHash);

        int matWidth = bmatrix.getWidth();

        BufferedImage image = new BufferedImage(matWidth, matWidth, BufferedImage.TYPE_INT_RGB);

        image.createGraphics();
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matWidth, matWidth);
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matWidth; i++) {

            for (int j = 0; j < matWidth; j++) {

                if (bmatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }

        ImageIO.write(image, "png", QRFile);

    }
    // End of QRgenerator

    public static void pdfGrab(String url, String name) throws IOException {

        URL fileurl = new URL(url);

        String pathname = "pdfimgs/" + name + ".pdf";

        InputStream is = fileurl.openStream();

        FileOutputStream fs = new FileOutputStream(pathname);

        System.out.println("File pdfimgs/" + name + ".pdf made");

        byte[] buffer = new byte[1024];

        int l = -1;
        while ((l = is.read(buffer)) > -1) {
            fs.write(buffer, 0, l);
        }

        fs.close();
        is.close();

    }

}