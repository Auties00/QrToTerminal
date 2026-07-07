package com.github.auties00.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class QrTest {
    public static void main(String[] args) throws WriterException {
        var writer = new MultiFormatWriter();
        var data = new byte[128];
        ThreadLocalRandom.current().nextBytes(data);
        var hints = Map.of(EncodeHintType.MARGIN, 0);
        var qrCode = writer.encode(Base64.getEncoder().encodeToString(data), BarcodeFormat.QR_CODE, 33, 33, hints);
        QrTerminal.print(qrCode, false);
        QrTerminal.print(qrCode, true);
    }
}
