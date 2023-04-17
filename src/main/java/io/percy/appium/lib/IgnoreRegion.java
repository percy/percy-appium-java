package io.percy.appium.lib;

public class IgnoreRegion {
    private int top;
    private int bottom;
    private int left;
    private int right;

    public IgnoreRegion(int top, int bottom, int left, int right) {
        if (top < 0 || bottom < 0 || left < 0 || right < 0) {
            throw new IllegalArgumentException("Only Positive integer is allowed!");
        }
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        if (top < 0) {
            throw new IllegalArgumentException("Only Positive integer is allowed!");
        }
        this.top = top;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        if (bottom < 0) {
            throw new IllegalArgumentException("Only Positive integer is allowed!");
        }
        this.bottom = bottom;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        if (left < 0) {
            throw new IllegalArgumentException("Only Positive integer is allowed!");
        }
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        if (right < 0) {
            throw new IllegalArgumentException("Only Positive integer is allowed!");
        }
        this.right = right;
    }

    public boolean isValid(int height, int width) {
        if (top >= bottom || left >= right) {
            return false;
        }

        if (top >= height || bottom > height || left >= width || right > width) {
            return false;
        }

        return true;
    }
}
