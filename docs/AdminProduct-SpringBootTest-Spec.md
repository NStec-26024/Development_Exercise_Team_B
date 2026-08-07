# AdminProduct コントローラ / サービス単体テスト仕様書（@SpringBootTest）

## 概要
- 対象: `AdminProductController`, `AdminProductServiceImpl`
- 実行形式: `@SpringBootTest`（実 DB は使用しない。依存はモック化）
- 目的: コントローラのルーティング・リダイレクト、Model への設定、サービスのビジネスロジックと例外処理を網羅的に確認する

## 実行方法
ルートで実行:

```bash
./gradlew test --tests "com.example.fullness.stationary..*"
```

## テストケース（各ケースに「テストデータ（入力値）」「テスト内容」「期待結果」を記載）

### Controller: `AdminProductController`

- TC-C1: `productSearchPage_searchesByCategoryParam`
  - テストデータ: `GET /admin/product?categoryId=2`、`productService.getAllCategories()` はカテゴリ一覧を返す
  - テスト内容: エンドポイントを呼ぶ
  - 期待結果: HTTP 200、ビュー `admin/product/search`、`productService.searchProductsByCategoryAndSetModel(2,1,model)` が呼ばれる

- TC-C2: `productSearchPage_usesCategoryParamWhenProvided`
  - テストデータ: `GET /admin/product?category=3&page=2`、`productService.getAllCategories()` はカテゴリ一覧を返す
  - テスト内容: `category` パラメータ優先で呼び出す
  - 期待結果: HTTP 200、ビュー `admin/product/search`、`productService.searchProductsByCategoryAndSetModel(3,2,model)` が呼ばれる

- TC-C3: `productSearchPage_withInvalidCategoryString_callsAllProducts`
  - テストデータ: `GET /admin/product?category=abc`、`productService.getAllCategories()` は空リスト
  - テスト内容: 文字列カテゴリを渡す
  - 期待結果: HTTP 200、ビュー `admin/product/search`、`productService.searchAllProductsAndSetModel(1,model)` が呼ばれる

- TC-C4: `editProduct_redirectsToUpdateUrl`
  - テストデータ: `GET /admin/product/edit/5?category=2&page=3`
  - テスト内容: 編集エンドポイント呼び出し
  - 期待結果: 3xx リダイレクト、リダイレクト先に `/admin/product/update/5?category=2&page=3` を含む

- TC-C5: `deleteProduct_redirectsToDeleteConfirmUrl`
  - テストデータ: `GET /admin/product/delete/7?category=1&page=1`
  - テスト内容: 削除確認エンドポイント呼び出し
  - 期待結果: 3xx リダイレクト、リダイレクト先に `/admin/product/delete/7?category=1&page=1` を含む

- TC-C6: `addProduct_redirectsToRegisterWithCategory`
  - テストデータ: `GET /admin/product/add?category=4`
  - テスト内容: 追加遷移エンドポイント呼び出し
  - 期待結果: 3xx リダイレクト、リダイレクト先に `/admin/product/register?category=4` を含む

### Service: `AdminProductServiceImpl`

- TC-S1: `getAllCategories_returnsEmptyListWhenRepositoryReturnsNull`
  - テストデータ: `productCategoryRepository.findAll()` が `null`
  - テスト内容: `getAllCategories()` を実行
  - 期待結果: 空リストを返す（例外は発生しない）

- TC-S2: `getAllCategories_returnsCategoriesWhenPresent`
  - テストデータ: `productCategoryRepository.findAll()` が `[ {id:1,name:\"文具\"} ]` を返す
  - テスト内容: `getAllCategories()` を実行
  - 期待結果: サイズ 1 のリスト、最初の要素の `name` が `文具`

- TC-S3: `getAllCategories_handlesException_returnsEmptyList`
  - テストデータ: `productCategoryRepository.findAll()` が例外をスロー
  - テスト内容: `getAllCategories()` を実行
  - 期待結果: 空リストを返す（例外は内部で捕捉される）

- TC-S4: `getCategoryName_returnsNameWhenExists`
  - テストデータ: `productCategoryRepository.findById(2)` が `{id:2,name:\"文房具\"}` を返す
  - テスト内容: `getCategoryName(2)` を実行
  - 期待結果: 文字列 `文房具` を返す

- TC-S5: `getCategoryName_returnsNullForNullOrZeroId`
  - テストデータ: 引数が `null` および `0`
  - テスト内容: `getCategoryName(null)`, `getCategoryName(0)` を実行
  - 期待結果: どちらも `null` を返し、リポジトリ呼び出しは行われない

- TC-S6: `getCategoryName_handlesException_returnsNull`
  - テストデータ: `productCategoryRepository.findById(99)` が例外をスロー
  - テスト内容: `getCategoryName(99)` を実行
  - 期待結果: `null` を返す

- TC-S7: `searchAllProductsAndSetModel_returnsFalseWhenNoProductsFound`
  - テストデータ: `productRepository.findAllWithPaging(0,10)` が空リスト
  - テスト内容: `searchAllProductsAndSetModel(1, model)` を実行
  - 期待結果: `false` を返す。`model` に `infoMessage=\"該当する商品情報がありません\"`、`searched=true`、`selectedCategoryId=0` が設定される

- TC-S8: `searchAllProductsAndSetModel_returnsTrueAndSetsPagingForFoundProducts`
  - テストデータ: `productRepository.findAllWithPaging(0,10)` が商品リスト、`productRepository.countAll()` が `1`
  - テスト内容: `searchAllProductsAndSetModel(1, model)` を実行
  - 期待結果: `true` を返す。`model` に `productList`, `currentPage=1`, `totalPages=1`, `totalCount=1`, `hasPrevious=false`, `hasNext=false`, `searched=true` が設定される

- TC-S9: `searchAllProductsAndSetModel_usesPageOneWhenPageLessThanOne`
  - テストデータ: `page=-3`（負の値）、`productRepository` は通常の結果を返す
  - テスト内容: `searchAllProductsAndSetModel(-3, model)` を実行
  - 期待結果: ページは 1 として扱われ、`findAllWithPaging(0,10)` が呼ばれる

- TC-S10: `searchAllProductsAndSetModel_handlesRepositoryException_setsErrorAndReturnsFalse`
  - テストデータ: `productRepository.findAllWithPaging(0,10)` が例外をスロー
  - テスト内容: `searchAllProductsAndSetModel(1, model)` を実行
  - 期待結果: `false` を返す。`model` に `errorMessage`（例外メッセージを含む）と `searched=true` が設定される

- TC-S11: `searchProductsByCategoryAndSetModel_returnsFalseWhenCategoryProductsNotFound`
  - テストデータ: `productRepository.findByCategoryWithPaging(2,0,10)` が空リスト、`productCategoryRepository.findById(2)` が `{id:2,name:\"文具\"}` を返す
  - テスト内容: `searchProductsByCategoryAndSetModel(2,1,model)` を実行
  - 期待結果: `false` を返す。`model` に `infoMessage=\"該当する商品情報がありません\"`、`searched=true`、`selectedCategoryId=2`、`selectedCategoryName=文具` が設定される

- TC-S12: `searchProductsByCategoryAndSetModel_returnsTrueAndSetsPagingForCategoryProducts`
  - テストデータ: `productRepository.findByCategoryWithPaging(2,0,10)` が商品リスト、`productRepository.countByCategory(2)` が `1`、`productCategoryRepository.findById(2)` がカテゴリを返す
  - テスト内容: `searchProductsByCategoryAndSetModel(2,1,model)` を実行
  - 期待結果: `true` を返す。`model` に `productList`, `currentPage=1`, `totalPages=1`, `totalCount=1`, `selectedCategoryId=2`, `selectedCategoryName=文具`, `searched=true` が設定される

- TC-S13: `searchProductsByCategoryAndSetModel_handlesRepositoryException_setsErrorAndReturnsFalse`
  - テストデータ: `productRepository.findByCategoryWithPaging(2,0,10)` が例外をスロー
  - テスト内容: `searchProductsByCategoryAndSetModel(2,1,model)` を実行
  - 期待結果: `false` を返す。`model` に `errorMessage` と `searched=true` が設定される

## 注意点
- これらのテストはスライステストではなく `@SpringBootTest` だが、DB 依存は排除しているためテスト実行時に外部 DB は不要。
- 実 DB を使う統合テスト（スキーマ初期化やテストデータ投入を含む）を別途作成する場合は指示をください。

