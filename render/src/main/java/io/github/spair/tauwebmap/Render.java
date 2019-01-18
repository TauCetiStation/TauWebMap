package io.github.spair.tauwebmap;

import io.github.spair.byond.dme.Dme;
import io.github.spair.byond.dme.parser.DmeParser;
import io.github.spair.byond.dmm.Dmm;
import io.github.spair.byond.dmm.drawer.DmmDrawer;
import io.github.spair.byond.dmm.drawer.FilterMode;
import io.github.spair.dmm.io.DmmData;
import io.github.spair.dmm.io.reader.DmmReader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Render {

    private static final String DME_PATH = "tmp/repo/taucetistation.dme";
    private static final String DMM_PATH = "tmp/repo/maps/z1.dmm";

    private static final String[] IGNORE_TYPES = {"/turf/space", "/area", "/obj/effect/landmark"};
    private static final String[] COMPRESSION_ARGS = {"pngquant", "--ext=.png", "--force", "--strip", "--speed=1", "--nofs", "--posterize=2"};

    private final String mapFolderPath;

    // Numbers that are divisible by 8160 without remainder
    private Map<Integer, Integer> zoomFactors = new HashMap<Integer, Integer>() {
        {
            put(3, 8);
            put(4, 16);
            put(5, 32);
        }
    };
    private Map<Integer, Double> scaleFactors = new HashMap<Integer, Double>() {
        {
            put(3, 0.5);
            put(4, 0.8);
            put(5, 1.0);
        }
    };

    private Render(String mapFolderPath) {
        this.mapFolderPath = mapFolderPath;
    }

    private void run() throws Exception {
        BufferedImage generatedImg = generateMapImage();

        for (Map.Entry<Integer, Integer> entry : zoomFactors.entrySet()) {
            Integer zoom = entry.getKey();
            Integer zoomFactor = entry.getValue();

            File zoomFolder = new File(mapFolderPath + "/" + zoom);
            zoomFolder.mkdir();

            createSubImages(generatedImg, zoomFolder.getPath(), zoomFactor, scaleFactors.get(zoom));
        }

        File mapFolder = new File(mapFolderPath);

        for (File zoomFolder : Objects.requireNonNull(mapFolder.listFiles())) {
            new Thread(() -> {
                for (File imgFile : Objects.requireNonNull(zoomFolder.listFiles())) {
                    try {
                        new ProcessBuilder(getCompressionArgs(imgFile.getPath())).start().waitFor();
                    } catch (InterruptedException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }
    }

    private BufferedImage generateMapImage() {
        Dme dme = DmeParser.parse(new File(DME_PATH));
        dme.mergeWithJson(getClass().getClassLoader().getResourceAsStream("render_config.json"));
        DmmData dmmData = DmmReader.readMap(new File(DMM_PATH));
        Dmm dmm = new Dmm(dmmData, dme);
        return DmmDrawer.drawMap(dmm, FilterMode.IGNORE, IGNORE_TYPES);
    }

    private void createSubImages(BufferedImage img, String zoomFolderPath, Integer zoomFactor, Double scaleFactor) throws Exception {
        BufferedImage imageToCrop;

        if (scaleFactor != 1.0) {
            int scaleSize = (int) (img.getWidth() * scaleFactor);
            Image scaledImage = img.getScaledInstance(scaleSize, scaleSize, Image.SCALE_SMOOTH);
            imageToCrop = new BufferedImage(scaleSize, scaleSize, BufferedImage.TYPE_INT_ARGB);

            Graphics g = imageToCrop.createGraphics();
            g.drawImage(scaledImage, 0, 0, null);
            g.dispose();
        } else {
            imageToCrop = img;
        }

        int imageSize = imageToCrop.getWidth() / zoomFactor;
        for (int x = 0; x < zoomFactor; x++) {
            for (int y = 0; y < zoomFactor; y++) {
                BufferedImage subImg = imageToCrop.getSubimage(x * imageSize, y * imageSize, imageSize, imageSize);
                if (!isBlankImage(subImg)) {
                    String imgPath = String.format("%s/%s-%s.png", zoomFolderPath, y, x);
                    ImageIO.write(subImg, "png", new File(imgPath));
                }
            }
        }
    }

    private List<String> getCompressionArgs(String imgFilePath) {
        List<String> args = new ArrayList<>(Arrays.asList(COMPRESSION_ARGS));
        args.add(imgFilePath);
        return args;
    }

    private boolean isBlankImage(BufferedImage img) throws Exception {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] pixels = new int[w * h];

        PixelGrabber pg = new PixelGrabber(img, 0, 0, w, h, pixels, 0, w);
        pg.grabPixels();

        boolean isBlank = true;
        for (int pixel : pixels) {
            if (pixel != 0) {
                isBlank = false;
                break;
            }
        }

        return isBlank;
    }

    public static void main(String[] args) throws Exception {
        new Render(args[0]).run();
    }
}
