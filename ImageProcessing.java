package com.example.android.camera2basic;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ImageProcessing {
    public static byte[] getBytes(Image image) {
        return NV21toJPEG(YUV420toNV21(image), image.getWidth(), image.getHeight(), 100);
    }

    private static byte[] NV21toJPEG(byte[] nv21, int width, int height, int quality) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), quality, out);
        return out.toByteArray();
    }

    private static byte[] YUV420toNV21(Image image) {
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];

        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    channelOffset = width * height + 1;
                    outputStride = 2;
                    break;
                case 2:
                    channelOffset = width * height;
                    outputStride = 2;
                    break;
            }

            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();

            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
        }
        return data;
    }

//    public static int decodeYUV420SP(byte[] yuv420sp, int width, int height) {
//        final int frameSize = width * height;
//        int sum = 0;
//
//        // define variables before loops (+ 20-30% faster algorithm o0`)
//        int r, g, b, y1192, y, i, uvp, u, v;
//        for (int j = 0, yp = 0; j < height; j++) {
//            uvp = frameSize + (j >> 1) * width;
//            u = 0;
//            v = 0;
//            for (i = 0; i < width; i++, yp++) {
//                y = (0xff & ((int) yuv420sp[yp])) - 16;
//                if (y < 0)
//                    y = 0;
//                if ((i & 1) == 0) {
//                    v = (0xff & yuv420sp[uvp++]) - 128;
//                    u = (0xff & yuv420sp[uvp++]) - 128;
//                }
//
//                y1192 = 1192 * y;
//                r = (y1192 + 1634 * v);
//                g = (y1192 - 833 * v - 400 * u);
//                b = (y1192 + 2066 * u);
//
//                // Java's functions are faster then 'IFs'
//                r = Math.max(0, Math.min(r, 262143));
//                g = Math.max(0, Math.min(g, 262143));
//                b = Math.max(0, Math.min(b, 262143));
//
//                sum += r;
//
////                rgba[yp] = ((r << 14) & 0xff000000) | ((g << 6) & 0xff0000) | ((b >> 2) | 0xff00);
//            }
//        }
//        return sum;
//    }
//
//
//    public static byte[] imageToByteArray(Image image) {
//        byte[] data = null;
//        if (image.getFormat() == ImageFormat.JPEG) {
//            Image.Plane[] planes = image.getPlanes();
//            ByteBuffer buffer = planes[0].getBuffer();
//            data = new byte[buffer.capacity()];
//            buffer.get(data);
//            return data;
//        } else if (image.getFormat() == ImageFormat.YUV_420_888) {
//            data = NV21toJPEG(YUV_420_888toNV21(image), image.getWidth(), image.getHeight());
//        }
//        return data;
//    }
//
//    private static byte[] YUV_420_888toNV21(Image image) {
//        byte[] nv21;
//        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
//        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
//        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();
//
//        int ySize = yBuffer.remaining();
//        int uSize = uBuffer.remaining();
//        int vSize = vBuffer.remaining();
//
//        nv21 = new byte[ySize + uSize + vSize];
//
//        //U and V are swapped
//        yBuffer.get(nv21, 0, ySize);
//        vBuffer.get(nv21, ySize, vSize);
//        uBuffer.get(nv21, ySize + vSize, uSize);
//
//        return nv21;
//    }
//
//    private static byte[] NV21toJPEG(byte[] nv21, int width, int height) {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
//        yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
//        return out.toByteArray();
//    }
//
//    private static int decodeYUV420SPtoRedSum(byte[] yuv420sp, int width, int height) {
//        if (yuv420sp == null) return 0;
//
//        final int frameSize = width * height;
//
//        int sum = 0;
//        for (int j = 0, yp = 0; j < height; j++) {
//            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
//            for (int i = 0; i < width; i++, yp++) {
//                int y = (0xff & yuv420sp[yp]) - 16;
//                if (y < 0) y = 0;
//                if ((i & 1) == 0) {
//                    v = (0xff & yuv420sp[uvp++]) - 128;
//                    u = (0xff & yuv420sp[uvp++]) - 128;
//                }
//                int y1192 = 1192 * y;
//                int r = (y1192 + 1634 * v);
//                int g = (y1192 - 833 * v - 400 * u);
//                int b = (y1192 + 2066 * u);
//
//                if (r < 0) r = 0;
//                else if (r > 262143) r = 262143;
//                if (g < 0) g = 0;
//                else if (g > 262143) g = 262143;
//                if (b < 0) b = 0;
//                else if (b > 262143) b = 262143;
//
//                int pixel = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
//                int red = (pixel >> 16) & 0xff;
//                sum += red;
//            }
//        }
//        return sum;
//    }
//
//    public static int decodeYUV420SPtoRedAvg(byte[] yuv420sp, int width, int height) {
//        if (yuv420sp == null) return 0;
//
//        final int frameSize = width * height;
//
//        int sum = decodeYUV420SPtoRedSum(yuv420sp, width, height);
//
////        int sum = decodeYUV420SP(yuv420sp, width, height);
//        return (sum / frameSize);
//    }
}
