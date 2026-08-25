package com.example.fullness.stationary.entity;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 注文を表すエンティティクラス（対応するテーブル名は orders）。
 */
// order はSQLの予約語のため、テーブル名は複数形の orders を使用している。
@Data
public class Order {
    private Integer id;
    private Integer customerId;
    private Integer orderStatusId;
    private Integer paymentMethodId;
    private LocalDateTime orderDate;
    private Integer amountTotal;

}
