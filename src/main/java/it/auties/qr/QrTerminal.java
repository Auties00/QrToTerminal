package it.auties.qr;

import com.google.zxing.common.BitMatrix;

import java.util.Objects;

/**
 * Utility class to print qr codes to the terminal
 */
public class QrTerminal {
    private static final String WW = "█";
    private static final String BB = " ";
    private static final String WB = "▀";
    private static final String BW = "▄";
    private static final int QZ = 2;
    public static final String BLACK = "\\e[40m";
    public static final String WHITE = "\\e[47m";

    /**
     * Prints a qr code to the terminal
     *
     * @param matrix The BitMatrix object that contains the QR code data.
     * @param small boolean - whether to print the QR code in a small format
     */
    public static void print(BitMatrix matrix, boolean small){
        System.out.println(toString(matrix, small));
    }

    /**
     * Transforms a qr code to a string
     *
     * @param matrix The BitMatrix object that contains the QR code data.
     * @param small boolean - whether to print the QR code in a small format
     * @return string
     */
    private static String toString(BitMatrix matrix, boolean small) {
        Objects.requireNonNull(matrix, "Missing argument: matrix");
        return small ? toSmallString(matrix) : toBigString(matrix);
    }

    private static String toSmallString(BitMatrix matrix) {
        var writer = new StringBuilder();
        var header = WW.repeat(matrix.getWidth() + QZ * QZ);
        writer.append((header + "\n").repeat(1));
        for (var i = 0; i <= matrix.getWidth(); i += QZ) {
            writer.append(WW.repeat(QZ));
            for (var j = 0; j <= matrix.getWidth(); j++) {
                var nextBlack = i + 1 < matrix.getWidth() && matrix.get(j, i + 1);
                var currentBlack = matrix.get(j, i);
                if (currentBlack && nextBlack) {
                    writer.append(BB);
                } else if (currentBlack) {
                    writer.append(BW);
                } else if (!nextBlack) {
                    writer.append(WW);
                } else {
                    writer.append(WB);
                }
            }

            writer.append(WW.repeat(QZ - 1));
            writer.append("\n");
        }

        writer.append(WB.repeat(matrix.getWidth() + QZ * QZ));
        writer.append("\n");
        return writer.toString();
    }

    private static String toBigString(BitMatrix matrix) {
        return matrix.toString(BLACK, WHITE);
    }
}
