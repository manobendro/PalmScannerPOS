package com.palmscanner.pos.viewmodel.datatype;

public class PalmMaskedImage {


    private byte[] maskedImage;
    private int width;
    private int height;


    public PalmMaskedImage(byte[] maskedImage, int width, int height) {
        this.maskedImage = maskedImage;
        this.width = width;
        this.height = height;
    }

    public byte[] getMaskedImage() {
        return maskedImage;
    }

    public void setMaskedImage(byte[] maskedImage) {
        this.maskedImage = maskedImage;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
