package com.example.fullness.stationary.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.form.AdminProductRegistrationForm;

/**
 * FormからEntityに変換するHelperクラス
 */
@Component
public class ProductHelper {

    /**
     * 入力データが入ったFormを基に社員アカウントEntityを生成
     * 
     * @param 入力データが入ったForm
     * @return 社員アカウントEntity
     */
    public Product formToEntity(AdminProductRegistrationForm adminProductRegistrationForm) {
        Product product = new Product();
        product.setName(adminProductRegistrationForm.getProductName());
        product.setPrice(adminProductRegistrationForm.getPrice());
        product.setStock(adminProductRegistrationForm.getQuantity());
        product.setCategoryId(adminProductRegistrationForm.getProductCategoryId());

        // 修正箇所：MultipartFileから「ファイル名（String）」を取り出してセットする
        MultipartFile imageFile = adminProductRegistrationForm.getImageUrl(); // Formの型がMultipartFileの場合
        if (imageFile != null && !imageFile.isEmpty()) {
            product.setImageUrl(imageFile.getOriginalFilename());
        } else {
            product.setImageUrl(null); // 画像がない場合はnull、またはデフォルト画像名など
        }
        return product;
    }

}
