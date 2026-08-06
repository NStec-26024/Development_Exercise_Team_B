package com.example.fullness.stationary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot アプリケーションの起動クラス。
 * アプリケーション全体を起動し、Web サーバーを開始する役割を持つ。
 */
@SpringBootApplication
public class FullnessStationaryApplication {

	/**
	 * アプリケーションを起動する。
	 *
	 * @param args 起動時引数
	 */
	public static void main(String[] args) {
		SpringApplication.run(FullnessStationaryApplication.class, args);
	}

}
