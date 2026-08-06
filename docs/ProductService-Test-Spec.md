# ProductService 単体テスト仕様書

## 目的
- `ProductService` の主要メソッドに対する単体テストの方針と、実装済みテストケースの内容を整理する。

## 対象クラス
- `com.example.fullness.stationary.service.ProductService`

## 対応テストコード
- 実装先: `demo/src/test/java/com/example/fullness/stationary/service/ProductServiceTest.java`

## テスト環境
- JDK 11+
- Gradle
- テストフレームワーク: JUnit 5
- モック: Mockito

## テスト観点
- カテゴリ取得処理
- カテゴリ名取得処理
- 全件商品検索とページング情報設定
- カテゴリ別商品検索とページング情報設定
- 例外発生時のエラー処理

## テストケース一覧

### 1. カテゴリ取得
- TC-01: `getAllCategories_returnsCategoriesWhenPresent`
  - テストデータ: `productCategoryRepository.findAll()` がカテゴリ一覧を返す
  - テスト内容: `productService.getAllCategories()` を実行する
  - 期待結果: 取得結果が `null` ではなく、同じカテゴリ一覧を返す

- TC-02: `getAllCategories_returnsEmptyListWhenRepositoryReturnsNull`
  - テストデータ: `productCategoryRepository.findAll()` が `null` を返す
  - テスト内容: `productService.getAllCategories()` を実行する
  - 期待結果: 空リストを返す

### 2. カテゴリ名取得
- TC-03: `getCategoryName_returnsNameWhenCategoryExists`
  - テストデータ: `productCategoryRepository.findById(1)` がカテゴリを返す
  - テスト内容: `productService.getCategoryName(1)` を実行する
  - 期待結果: カテゴリ名 `文具` を返す

- TC-04: `getCategoryName_returnsNullForNullOrZeroId`
  - テストデータ: 引数が `null` または `0`
  - テスト内容: `productService.getCategoryName(null)` / `productService.getCategoryName(0)` を実行する
  - 期待結果: `null` を返し、リポジトリ呼び出しは行われない

### 3. 全件商品検索
- TC-05: `searchAllProductsAndSetModel_returnsFalseWhenNoProductsFound`
  - テストデータ: `productRepository.findAllWithPaging(0, 10)` が空リストを返す
  - テスト内容: `productService.searchAllProductsAndSetModel(1, model)` を実行する
  - 期待結果: `false` を返し、`model` に `infoMessage` / `searched` / `selectedCategoryId` を設定する

- TC-06: `searchAllProductsAndSetModel_returnsTrueAndSetsPagingForFoundProducts`
  - テストデータ: `productRepository.findAllWithPaging(0, 10)` が商品を返し、`countAll()` が総件数を返す
  - テスト内容: `productService.searchAllProductsAndSetModel(1, model)` を実行する
  - 期待結果: `true` を返し、ページング情報を `model` に設定する

- TC-07: `searchAllProductsAndSetModel_usesPageOneWhenPageIsLessThanOne`
  - テストデータ: ページ番号が `-5`
  - テスト内容: `productService.searchAllProductsAndSetModel(-5, model)` を実行する
  - 期待結果: 1 ページとして扱われ、オフセット `0` で検索される

### 4. カテゴリ別商品検索
- TC-08: `searchProductsByCategoryAndSetModel_returnsFalseWhenCategoryProductsNotFound`
  - テストデータ: `productRepository.findByCategoryWithPaging(2, 0, 10)` が空リストを返す
  - テスト内容: `productService.searchProductsByCategoryAndSetModel(2, 1, model)` を実行する
  - 期待結果: `false` を返し、`model` に `infoMessage` / `searched` / `selectedCategoryId` / `selectedCategoryName` を設定する

- TC-09: `searchProductsByCategoryAndSetModel_returnsTrueAndSetsPagingForCategoryProducts`
  - テストデータ: `productRepository.findByCategoryWithPaging(2, 0, 10)` が商品を返し、`countByCategory(2)` が件数を返す
  - テスト内容: `productService.searchProductsByCategoryAndSetModel(2, 1, model)` を実行する
  - 期待結果: `true` を返し、カテゴリ名付きのページング情報を `model` に設定する

- TC-10: `searchProductsByCategoryAndSetModel_redirectsToAllProductsWhenCategoryIdIsZero`
  - テストデータ: `categoryId` が `0`
  - テスト内容: `productService.searchProductsByCategoryAndSetModel(0, 1, model)` を実行する
  - 期待結果: 全件検索へフォールバックして処理する

### 5. 例外処理
- TC-11: `getAllCategories_handlesRepositoryException_returnsEmptyList`
  - テストデータ: `productCategoryRepository.findAll()` が例外を投げる
  - テスト内容: `productService.getAllCategories()` を実行する
  - 期待結果: 空リストを返す

- TC-12: `getCategoryName_handlesRepositoryException_returnsNull`
  - テストデータ: `productCategoryRepository.findById(99)` が例外を投げる
  - テスト内容: `productService.getCategoryName(99)` を実行する
  - 期待結果: `null` を返す

- TC-13: `searchAllProductsAndSetModel_handlesRepositoryException_setsErrorAndReturnsFalse`
  - テストデータ: `productRepository.findAllWithPaging(0, 10)` が例外を投げる
  - テスト内容: `productService.searchAllProductsAndSetModel(1, model)` を実行する
  - 期待結果: `false` を返し、`model` に `errorMessage` と `searched=true` を設定する

- TC-14: `searchProductsByCategoryAndSetModel_handlesRepositoryException_setsErrorAndReturnsFalse`
  - テストデータ: `productRepository.findByCategoryWithPaging(2, 0, 10)` が例外を投げる
  - テスト内容: `productService.searchProductsByCategoryAndSetModel(2, 1, model)` を実行する
  - 期待結果: `false` を返し、`model` に `errorMessage` と `searched=true` を設定する

## 実行手順
1. プロジェクトルートでテストを実行する。

```bash
./gradlew test
```

2. 個別に確認する場合は、IDE から `ProductServiceTest` を実行する。

