package com.example.fullness.stationary.entity;

import lombok.Data;

/**
 * 注文明細を表すエンティティクラス。
 */
@Data
public class OrderDetail {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private Integer customerId;
    private Integer count;

}
