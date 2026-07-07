package com.github.auties00.qr;

import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Utility class to print qr codes to the terminal
 */
public class QrTerminal {
    private static final String WHITE_WHITE = "█";
    private static final String BLACK_BLACK = " ";
    private static final String WHITE_BLACK = "▀";
    private static final String BLACK_WHITE = "▄";
    private static final String NEW_LINE = "\n";
    private static final int QUIET_ZONE = 4;
    private static final int ROWS_PER_LINE = 2;
    public static final String BLACK = "\033[40m  \033[0m";
    public static final String WHITE = "\033[47m  \033[0m";

    // Glyphs pre-encoded once for UTF-8, the fast path used by the byte renderers.
    // Small format, indexed by glyphCode:
    // 0 = both dark,
    // 1 = top dark,
    // 2 = bottom dark,
    // 3 = both light.
    private static final byte[][] SMALL_GLYPHS_UTF_8 = {
            BLACK_BLACK.getBytes(StandardCharsets.UTF_8),
            BLACK_WHITE.getBytes(StandardCharsets.UTF_8),
            WHITE_BLACK.getBytes(StandardCharsets.UTF_8),
            WHITE_WHITE.getBytes(StandardCharsets.UTF_8)
    };
    private static final byte[] BLACK_UTF_8 = BLACK.getBytes(StandardCharsets.UTF_8);
    private static final byte[] WHITE_UTF_8 = WHITE.getBytes(StandardCharsets.UTF_8);
    private static final byte[] NEW_LINE_UTF_8 = NEW_LINE.getBytes(StandardCharsets.UTF_8);

    /**
     * Prints a qr code to the standard output, using its own charset.
     *
     * @param matrix The BitMatrix object that contains the QR code data.
     * @param small boolean - whether to print the QR code in a small format
     */
    public static void print(BitMatrix matrix, boolean small) {
        print(matrix, small, System.out, System.out.charset());
    }

    /**
     * Prints a qr code to the standard output, using the given charset.
     *
     * @param matrix The BitMatrix object that contains the QR code data.
     * @param small boolean - whether to print the QR code in a small format
     * @param charset the charset the QR code is encoded with
     */
    public static void print(BitMatrix matrix, boolean small, Charset charset) {
        print(matrix, small, System.out, charset);
    }

    /**
     * Prints a qr code to the standard error, using its own charset.
     *
     * @param matrix The BitMatrix object that contains the QR code data.
     * @param small boolean - whether to print the QR code in a small format
     */
    public static void printErr(BitMatrix matrix, boolean small) {
        print(matrix, small, System.err, System.err.charset());
    }

    /**
     * Prints a qr code to the standard error, using the given charset.
     *
     * @param matrix The BitMatrix object that contains the QR code data.
     * @param small boolean - whether to print the QR code in a small format
     * @param charset the charset the QR code is encoded with
     */
    public static void printErr(BitMatrix matrix, boolean small, Charset charset) {
        print(matrix, small, System.err, charset);
    }

    /**
     * Prints a qr code directly to the given stream, encoded as UTF-8.
     * The stream is flushed but not closed: its lifecycle stays with the caller.
     *
     * @param matrix The BitMatrix object that contains the QR code data.
     * @param small boolean - whether to print the QR code in a small format
     * @param stream the stream the QR code is written to
     */
    public static void print(BitMatrix matrix, boolean small, OutputStream stream) {
        print(matrix, small, stream, StandardCharsets.UTF_8);
    }

    /**
     * Prints a qr code directly to the given stream, using the given charset.
     * The stream is flushed but not closed: its lifecycle stays with the caller.
     *
     * @param matrix The BitMatrix object that contains the QR code data.
     * @param small boolean - whether to print the QR code in a small format
     * @param stream the stream the QR code is written to
     * @param charset the charset the QR code is encoded with
     */
    public static void print(BitMatrix matrix, boolean small, OutputStream stream, Charset charset) {
        Objects.requireNonNull(matrix, "Missing argument: matrix");
        Objects.requireNonNull(stream, "Missing argument: stream");
        Objects.requireNonNull(charset, "Missing argument: charset");
        try {
            stream.write(small ? toSmallBytes(matrix, charset) : toBigBytes(matrix, charset));
            stream.flush();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    /**
     * Transforms a qr code to a string
     *
     * @param matrix The BitMatrix object that contains the QR code data.
     * @param small boolean - whether to print the QR code in a small format
     * @return string
     */
    public static String toString(BitMatrix matrix, boolean small) {
        Objects.requireNonNull(matrix, "Missing argument: matrix");
        return small ? toSmallString(matrix) : toBigString(matrix);
    }

    private static String toSmallString(BitMatrix matrix) {
        var columns = matrix.getWidth() + 2 * QUIET_ZONE;
        var lines = lineCount(matrix);
        var builder = new StringBuilder(lines * (columns + 1));

        var minX = -QUIET_ZONE;
        var maxX = matrix.getWidth() + QUIET_ZONE;
        var minY = -QUIET_ZONE;
        for (var line = 0; line < lines; line++) {
            var y = minY + line * ROWS_PER_LINE;
            for (var x = minX; x < maxX; x++) {
                var topBlack = isBlack(matrix, x, y);
                var bottomBlack = isBlack(matrix, x, y + 1);
                if (topBlack && bottomBlack) {
                    builder.append(BLACK_BLACK);
                } else if (topBlack) {
                    builder.append(BLACK_WHITE);
                } else if (bottomBlack) {
                    builder.append(WHITE_BLACK);
                } else {
                    builder.append(WHITE_WHITE);
                }
            }
            builder.append(NEW_LINE);
        }
        return builder.toString();
    }

    private static String toBigString(BitMatrix matrix) {
        var minX = -QUIET_ZONE;
        var maxX = matrix.getWidth() + QUIET_ZONE;
        var minY = -QUIET_ZONE;
        var maxY = matrix.getHeight() + QUIET_ZONE;
        var builder = new StringBuilder((maxY - minY) * ((maxX - minX) * BLACK.length() + 1));
        for (var y = minY; y < maxY; y++) {
            for (var x = minX; x < maxX; x++) {
                builder.append(isBlack(matrix, x, y) ? BLACK : WHITE);
            }
            builder.append(NEW_LINE);
        }
        return builder.toString();
    }

    static byte[] toSmallBytes(BitMatrix matrix, Charset charset) {
        if (!charset.equals(StandardCharsets.UTF_8)) {
            return toSmallString(matrix).getBytes(charset);
        }

        var minX = -QUIET_ZONE;
        var maxX = matrix.getWidth() + QUIET_ZONE;
        var minY = -QUIET_ZONE;
        var lines = lineCount(matrix);

        // First pass: exact size.
        var size = lines * NEW_LINE_UTF_8.length;
        for (var line = 0; line < lines; line++) {
            var y = minY + line * ROWS_PER_LINE;
            for (var x = minX; x < maxX; x++) {
                size += SMALL_GLYPHS_UTF_8[glyphCode(matrix, x, y)].length;
            }
        }

        // Second pass: fill.
        var buffer = new byte[size];
        var position = 0;
        for (var line = 0; line < lines; line++) {
            var y = minY + line * ROWS_PER_LINE;
            for (var x = minX; x < maxX; x++) {
                var glyph = SMALL_GLYPHS_UTF_8[glyphCode(matrix, x, y)];
                System.arraycopy(glyph, 0, buffer, position, glyph.length);
                position += glyph.length;
            }
            System.arraycopy(NEW_LINE_UTF_8, 0, buffer, position, NEW_LINE_UTF_8.length);
            position += NEW_LINE_UTF_8.length;
        }
        return buffer;
    }

    static byte[] toBigBytes(BitMatrix matrix, Charset charset) {
        if (!charset.equals(StandardCharsets.UTF_8)) {
            return toBigString(matrix).getBytes(charset);
        }

        var minX = -QUIET_ZONE;
        var maxX = matrix.getWidth() + QUIET_ZONE;
        var minY = -QUIET_ZONE;
        var maxY = matrix.getHeight() + QUIET_ZONE;
        var columns = maxX - minX;
        var rows = maxY - minY;
        var buffer = new byte[rows * (columns * BLACK_UTF_8.length + NEW_LINE_UTF_8.length)];
        var position = 0;
        for (var y = minY; y < maxY; y++) {
            for (var x = minX; x < maxX; x++) {
                var cell = isBlack(matrix, x, y) ? BLACK_UTF_8 : WHITE_UTF_8;
                System.arraycopy(cell, 0, buffer, position, cell.length);
                position += cell.length;
            }
            System.arraycopy(NEW_LINE_UTF_8, 0, buffer, position, NEW_LINE_UTF_8.length);
            position += NEW_LINE_UTF_8.length;
        }
        return buffer;
    }

    private static int lineCount(BitMatrix matrix) {
        var rows = matrix.getHeight() + 2 * QUIET_ZONE;
        return (rows + ROWS_PER_LINE - 1) / ROWS_PER_LINE;
    }

    private static int glyphCode(BitMatrix matrix, int x, int y) {
        var topBlack = isBlack(matrix, x, y);
        var bottomBlack = isBlack(matrix, x, y + 1);
        if (topBlack && bottomBlack) {
            return 0;
        } else if (topBlack) {
            return 1;
        } else if (bottomBlack) {
            return 2;
        } else {
            return 3;
        }
    }

    private static boolean isBlack(BitMatrix matrix, int x, int y) {
        return x >= 0 && x < matrix.getWidth()
                && y >= 0 && y < matrix.getHeight()
                && matrix.get(x, y);
    }
}
