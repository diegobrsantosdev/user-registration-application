package com.diegobrsantosdev.user_registration_application.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import dev.samstevens.totp.code.*;

import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class TopService {

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1);
    private final CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, new SystemTimeProvider());

    public TopService() {
        // No additional configuration required, the default TOTP tolerance window will be used
    }

    // generate secret
    public String generateSecret() {
        return secretGenerator.generate();
    }

    // validate TOTP code
    public boolean validateCode(String secret, String code) {
        return verifier.isValidCode(secret, code);
    }

    // generate otpauth URI
    public String generateQrCodeUri(String account, String secret) {
        String issuer = "MyApp";
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
                issuer, account, secret, issuer
        );
    }

    // generate QR Code image as Base64 string
    public String generateQrCodeImage(String account, String secret) throws Exception {
        String uri = generateQrCodeUri(account, secret);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(uri, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }

}