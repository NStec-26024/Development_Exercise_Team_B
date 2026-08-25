package com.example.fullness.stationary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Fullness Stationary アプリケーションの起動クラス。
 * Spring Boot の自動構成とコンポーネントスキャンを有効化し、
 * アプリケーションを起動する。
 */
@SpringBootApplication
public class FullnessStationaryApplication {

	/**
	 * アプリケーションの起動メソッド。
	 * 
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		SpringApplication.run(FullnessStationaryApplication.class, args);
	}
}
