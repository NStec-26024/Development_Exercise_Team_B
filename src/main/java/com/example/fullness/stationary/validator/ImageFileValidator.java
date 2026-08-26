package com.example.fullness.stationary.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.Locale;

public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        // 1. ファイル未選択時は必須チェック（@NotNullなど）に委ねる
        if (file == null || file.isEmpty()) {
            return true;
        }

        // 2. 拡張子のチェック (jpg, jpeg, png のみ)
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !hasValidExtension(originalFilename)) {
            return false;
        }

        // 3. ファイル中身（マジックナンバー）のチェック
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[4];
            int bytesRead = is.read(header);

            if (bytesRead < 4) {
                return false; // ファイルが壊れている、または小さすぎる
            }

            // バイト配列を16進数文字列に変換
            String hexHeader = bytesToHex(header);

            // PDFのマジックナンバー「%PDF」（25504446）を検知して確実に弾く
            if (hexHeader.startsWith("25504446")) {
                return false;
            }

            // 許可する画像のヘッダーパターン
            boolean isJpeg = hexHeader.startsWith("FFD8"); // JPEG (.jpg, .jpeg)
            boolean isPng = hexHeader.startsWith("89504E47"); // PNG (.png)

            return isJpeg || isPng;

        } catch (Exception e) {
            return false;
        }
    }

    // 拡張子が jpg, jpeg, png かを判定するヘルパーメソッド
    private boolean hasValidExtension(String filename) {
        String lowerFilename = filename.toLowerCase(Locale.ROOT);
        return lowerFilename.endsWith(".jpg") ||
                lowerFilename.endsWith(".jpeg") ||
                lowerFilename.endsWith(".png");
    }

    // バイト配列を16進数文字列（大文字）に変換するヘルパーメソッド
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
