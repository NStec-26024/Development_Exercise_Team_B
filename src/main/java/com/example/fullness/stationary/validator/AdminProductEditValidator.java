package com.example.fullness.stationary.validator;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import com.example.fullness.stationary.form.AdminProductForm;

@Component
public class AdminProductEditValidator implements Validator {
    @Value("${product.max-price}")
    private int maxPrice;

    @Value("${product.max-stock}")
    private int maxStock;

    @Value("${product.allowed-image-extensions}")
    private List<String> allowedExtensions;

    @Override
    public boolean supports(Class<?> clazz) {
        return AdminProductForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AdminProductForm form = (AdminProductForm) target;

        validateRange(form.getPrice(), maxPrice, "price", "価格は100万円以下で入力してください", errors);
        validateRange(form.getStock(), maxStock, "stock", "在庫数は1000個以下で入力してください", errors);
        validateImage(form.getImage(), errors);
    }

    private void validateRange(String value, int max, String field, String message, Errors errors) {
        if (value == null || errors.hasFieldErrors(field)) {
            return;
        }
        long parsed;
        try {
            parsed = Long.parseLong(value);
        } catch (NumberFormatException e) {
            errors.rejectValue(field, "range.overflow", message);
            return;
        }
        if (parsed > max) {
            errors.rejectValue(field, "range.max", message);
        }
    }

    private void validateImage(MultipartFile file, Errors errors) {
        if (file == null || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            return;
        }

        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.lastIndexOf('.') >= 0) {
            extension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1)
                    .toLowerCase(Locale.ROOT);
        }

        if (!allowedExtensions.contains(extension)) {
            errors.rejectValue("image", "image.format", "正しい画像形式でアップロードしてください");
            return;
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            errors.rejectValue("image", "image.format", "正しい画像形式でアップロードしてください");
            return;
        }

        BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            image = null;
        }
        if (image == null) {
            errors.rejectValue("image", "image.format", "正しい画像形式でアップロードしてください");
        }
    }
}
