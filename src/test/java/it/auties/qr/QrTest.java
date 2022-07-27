package it.auties.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class QrTest {
    public static void main(String[] args) throws WriterException {
        var writer = new MultiFormatWriter();
        var data = new byte[128];
        ThreadLocalRandom.current().nextBytes(data);
        var qrCode = writer.encode(Base64.getEncoder().encodeToString(data), BarcodeFormat.QR_CODE, 33, 33);
        QrTerminal.print(qrCode, false);
        QrTerminal.print(qrCode, true);
    }
}
