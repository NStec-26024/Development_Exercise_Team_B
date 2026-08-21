package com.example.fullness.stationary.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.service.AdminProductService;

@SpringBootTest
public class AdminProductServiceimplTest {

        @Autowired
        private AdminProductService adminProductService;

        @MockBean
        private ProductRepository productRepository;

        @MockBean
        private ProductCategoryRepository productCategoryRepository;

        /**
         * 全カテゴリを正常に取得できることをテスト。
         */
        @Test
        @DisplayName("全カテゴリを取得できる")
        void testGetAllCategories() {

                ProductCategory category1 = mock(ProductCategory.class);
                ProductCategory category2 = mock(ProductCategory.class);

                when(category1.getId()).thenReturn(1);
                when(category1.getName()).thenReturn("ノート");

                when(category2.getId()).thenReturn(2);
                when(category2.getName()).thenReturn("ペン");

                List<ProductCategory> categories = Arrays.asList(category1, category2);

                when(productCategoryRepository.findAll())
                                .thenReturn(categories);

                List<ProductCategory> result = adminProductService.getAllCategories();

                assertNotNull(result);
                assertEquals(2, result.size());
                assertSame(category1, result.get(0));
                assertSame(category2, result.get(1));

                verify(productCategoryRepository, times(1)).findAll();
        }

        /**
         * Repositoryがnullを返した場合、空リストを返すことをテスト。
         */
        @Test
        @DisplayName("全カテゴリ取得でRepositoryがnullを返した場合は空リスト")
        void testGetAllCategoriesReturnsEmptyWhenNull() {

                when(productCategoryRepository.findAll())
                                .thenReturn(null);

                List<ProductCategory> result = adminProductService.getAllCategories();

                assertNotNull(result);
                assertTrue(result.isEmpty());

                verify(productCategoryRepository, times(1)).findAll();
        }

        /**
         * Repositoryで例外が発生した場合、空リストを返すことをテスト。
         */
        @Test
        @DisplayName("全カテゴリ取得で例外が発生した場合は空リスト")
        void testGetAllCategoriesReturnsEmptyWhenException() {

                when(productCategoryRepository.findAll())
                                .thenThrow(new RuntimeException("DB error"));

                List<ProductCategory> result = adminProductService.getAllCategories();

                assertNotNull(result);
                assertTrue(result.isEmpty());

                verify(productCategoryRepository, times(1)).findAll();
        }

        /**
         * カテゴリIDからカテゴリ名を取得できることをテスト。
         */
        @Test
        @DisplayName("カテゴリIDからカテゴリ名を取得できる")
        void testGetCategoryName() {

                ProductCategory category = mock(ProductCategory.class);

                when(category.getName()).thenReturn("ノート");
                when(productCategoryRepository.findById(1))
                                .thenReturn(category);

                String result = adminProductService.getCategoryName(1);

                assertEquals("ノート", result);

                verify(productCategoryRepository, times(1))
                                .findById(1);
        }

        /**
         * categoryIdがnullの場合。
         */
        @Test
        @DisplayName("カテゴリIDがnullの場合はnullを返す")
        void testGetCategoryNameWithNull() {

                String result = adminProductService.getCategoryName(null);

                assertNull(result);

                verify(productCategoryRepository, never())
                                .findById(anyInt());
        }

        /**
         * categoryIdが0の場合。
         */
        @Test
        @DisplayName("カテゴリIDが0の場合はnullを返す")
        void testGetCategoryNameWithZero() {

                String result = adminProductService.getCategoryName(0);

                assertNull(result);

                verify(productCategoryRepository, never())
                                .findById(anyInt());
        }

        /**
         * 存在しないカテゴリの場合。
         */
        @Test
        @DisplayName("存在しないカテゴリIDの場合はnullを返す")
        void testGetCategoryNameWhenNotFound() {

                when(productCategoryRepository.findById(999))
                                .thenReturn(null);

                String result = adminProductService.getCategoryName(999);

                assertNull(result);

                verify(productCategoryRepository, times(1))
                                .findById(999);
        }

        /**
         * カテゴリ取得時に例外が発生した場合。
         */
        @Test
        @DisplayName("カテゴリ取得で例外が発生した場合はnull")
        void testGetCategoryNameWhenException() {

                when(productCategoryRepository.findById(1))
                                .thenThrow(new RuntimeException("DB error"));

                String result = adminProductService.getCategoryName(1);

                assertNull(result);

                verify(productCategoryRepository, times(1))
                                .findById(1);
        }

        /**
         * 全商品検索の正常系。
         */
        @Test
        @DisplayName("全商品を1ページ目から検索できる")
        void testSearchAllProductsAndSetModel() {

                Product product1 = mock(Product.class);
                Product product2 = mock(Product.class);

                List<Product> products = Arrays.asList(product1, product2);

                when(productRepository.findAllWithPaging(0, 10))
                                .thenReturn(products);

                when(productRepository.countAll())
                                .thenReturn(25);

                Model model = new ExtendedModelMap();

                boolean result = adminProductService.searchAllProductsAndSetModel(
                                1, model);

                assertTrue(result);

                assertSame(products, model.getAttribute("productList"));
                assertEquals(1, model.getAttribute("currentPage"));
                assertEquals(3, model.getAttribute("totalPages"));
                assertEquals(25, model.getAttribute("totalCount"));
                assertEquals(false, model.getAttribute("hasPrevious"));
                assertEquals(true, model.getAttribute("hasNext"));
                assertEquals(0, model.getAttribute("selectedCategoryId"));
                assertNull(model.getAttribute("selectedCategoryName"));
                assertEquals(true, model.getAttribute("searched"));

                verify(productRepository, times(1))
                                .findAllWithPaging(0, 10);

                verify(productRepository, times(1))
                                .countAll();
        }

        /**
         * ページ番号が0以下の場合、1ページ目として処理されることをテスト。
         */
        @Test
        @DisplayName("ページ番号が1未満の場合は1ページ目として検索する")
        void testSearchAllProductsWithInvalidPage() {

                List<Product> products = Collections.singletonList(mock(Product.class));

                when(productRepository.findAllWithPaging(0, 10))
                                .thenReturn(products);

                when(productRepository.countAll())
                                .thenReturn(1);

                Model model = new ExtendedModelMap();

                boolean result = adminProductService.searchAllProductsAndSetModel(
                                0, model);

                assertTrue(result);

                assertEquals(1, model.getAttribute("currentPage"));
                assertEquals(1, model.getAttribute("totalPages"));

                verify(productRepository, times(1))
                                .findAllWithPaging(0, 10);
        }

        /**
         * 2ページ目の検索。
         */
        @Test
        @DisplayName("2ページ目ではoffsetが10になる")
        void testSearchAllProductsSecondPage() {

                List<Product> products = Collections.singletonList(mock(Product.class));

                when(productRepository.findAllWithPaging(10, 10))
                                .thenReturn(products);

                when(productRepository.countAll())
                                .thenReturn(25);

                Model model = new ExtendedModelMap();

                boolean result = adminProductService.searchAllProductsAndSetModel(
                                2, model);

                assertTrue(result);

                assertEquals(2, model.getAttribute("currentPage"));
                assertEquals(3, model.getAttribute("totalPages"));
                assertEquals(true, model.getAttribute("hasPrevious"));
                assertEquals(true, model.getAttribute("hasNext"));

                verify(productRepository, times(1))
                                .findAllWithPaging(10, 10);
        }

        /**
         * 全商品検索で商品が0件の場合。
         */
        @Test
        @DisplayName("全商品検索で商品が0件の場合はfalse")
        void testSearchAllProductsWhenEmpty() {

                when(productRepository.findAllWithPaging(0, 10))
                                .thenReturn(Collections.emptyList());

                Model model = new ExtendedModelMap();

                boolean result = adminProductService.searchAllProductsAndSetModel(
                                1, model);

                assertFalse(result);

                assertEquals(
                                "該当する商品情報がありません",
                                model.getAttribute("infoMessage"));

                assertEquals(true, model.getAttribute("searched"));
                assertEquals(0, model.getAttribute("selectedCategoryId"));

                // 商品0件なのでcountAll()は呼ばれない
                verify(productRepository, never()).countAll();
        }

        /**
         * Repositoryがnullを返した場合も0件として扱われることをテスト。
         */
        @Test
        @DisplayName("商品Repositoryがnullを返した場合はfalse")
        void testSearchAllProductsWhenRepositoryReturnsNull() {

                when(productRepository.findAllWithPaging(0, 10))
                                .thenReturn(null);

                Model model = new ExtendedModelMap();

                boolean result = adminProductService.searchAllProductsAndSetModel(
                                1, model);

                assertFalse(result);

                assertEquals(
                                "該当する商品情報がありません",
                                model.getAttribute("infoMessage"));

                assertEquals(true, model.getAttribute("searched"));
        }

        /**
         * 全商品検索でRepository例外が発生した場合。
         */
        @Test
        @DisplayName("全商品検索で例外が発生した場合はfalse")
        void testSearchAllProductsWhenException() {

                when(productRepository.findAllWithPaging(0, 10))
                                .thenThrow(new RuntimeException("DB error"));

                Model model = new ExtendedModelMap();

                boolean result = adminProductService.searchAllProductsAndSetModel(
                                1, model);

                assertFalse(result);

                assertNotNull(model.getAttribute("errorMessage"));
                assertTrue(
                                model.getAttribute("errorMessage")
                                                .toString()
                                                .contains("商品情報の取得に失敗しました"));

                assertEquals(true, model.getAttribute("searched"));
        }

        /**
         * カテゴリ別検索の正常系。
         */
        @Test
        @DisplayName("カテゴリを指定して商品を検索できる")
        void testSearchProductsByCategory() {

                ProductCategory category = mock(ProductCategory.class);

                when(category.getName())
                                .thenReturn("ノート");

                when(productCategoryRepository.findById(1))
                                .thenReturn(category);

                Product product1 = mock(Product.class);
                Product product2 = mock(Product.class);

                List<Product> products = Arrays.asList(product1, product2);

                when(productRepository.findByCategoryWithPaging(
                                eq(1), eq(0), eq(10)))
                                .thenReturn(products);

                when(productRepository.countByCategory(1))
                                .thenReturn(15);

                Model model = new ExtendedModelMap();

                boolean result = adminProductService
                                .searchProductsByCategoryAndSetModel(
                                                1, 1, model);

                assertTrue(result);

                assertSame(
                                products,
                                model.getAttribute("productList"));

                assertEquals(
                                1,
                                model.getAttribute("currentPage"));

                assertEquals(
                                2,
                                model.getAttribute("totalPages"));

                assertEquals(
                                15,
                                model.getAttribute("totalCount"));

                assertEquals(
                                1,
                                model.getAttribute("selectedCategoryId"));

                assertEquals(
                                "ノート",
                                model.getAttribute("selectedCategoryName"));

                assertEquals(
                                true,
                                model.getAttribute("searched"));

                assertEquals(
                                false,
                                model.getAttribute("hasPrevious"));

                assertEquals(
                                true,
                                model.getAttribute("hasNext"));

                verify(productRepository, times(1))
                                .findByCategoryWithPaging(1, 0, 10);

                verify(productRepository, times(1))
                                .countByCategory(1);

                verify(productCategoryRepository, times(1))
                                .findById(1);
        }

        /**
         * カテゴリ別検索の2ページ目。
         */
        @Test
        @DisplayName("カテゴリ別検索の2ページ目ではoffsetが10になる")
        void testSearchProductsByCategorySecondPage() {

                ProductCategory category = mock(ProductCategory.class);

                when(category.getName())
                                .thenReturn("ペン");

                when(productCategoryRepository.findById(2))
                                .thenReturn(category);

                List<Product> products = Collections.singletonList(mock(Product.class));

                when(productRepository.findByCategoryWithPaging(
                                eq(2), eq(10), eq(10)))
                                .thenReturn(products);

                when(productRepository.countByCategory(2))
                                .thenReturn(25);

                Model model = new ExtendedModelMap();

                boolean result = adminProductService
                                .searchProductsByCategoryAndSetModel(
                                                2, 2, model);

                assertTrue(result);

                assertEquals(
                                2,
                                model.getAttribute("currentPage"));

                assertEquals(
                                3,
                                model.getAttribute("totalPages"));

                assertEquals(
                                true,
                                model.getAttribute("hasPrevious"));

                assertEquals(
                                true,
                                model.getAttribute("hasNext"));

                verify(productRepository, times(1))
                                .findByCategoryWithPaging(2, 10, 10);
        }

        /**
         * カテゴリに該当する商品が0件の場合。
         */
        @Test
        @DisplayName("カテゴリ別検索で商品が0件の場合はfalse")
        void testSearchProductsByCategoryWhenEmpty() {

                ProductCategory category = mock(ProductCategory.class);

                when(category.getName())
                                .thenReturn("ノート");

                when(productCategoryRepository.findById(1))
                                .thenReturn(category);

                when(productRepository.findByCategoryWithPaging(
                                eq(1), eq(0), eq(10)))
                                .thenReturn(Collections.emptyList());

                Model model = new ExtendedModelMap();

                boolean result = adminProductService
                                .searchProductsByCategoryAndSetModel(
                                                1, 1, model);

                assertFalse(result);

                assertEquals(
                                "該当する商品情報がありません",
                                model.getAttribute("infoMessage"));

                assertEquals(
                                true,
                                model.getAttribute("searched"));

                assertEquals(
                                1,
                                model.getAttribute("selectedCategoryId"));

                assertEquals(
                                "ノート",
                                model.getAttribute("selectedCategoryName"));

                // 商品がないのでcountByCategory()は呼ばれない
                verify(productRepository, never())
                                .countByCategory(anyInt());
        }

        /**
         * categoryId = 0の場合、全商品検索にフォールバックすることをテスト。
         */
        @Test
        @DisplayName("カテゴリIDが0の場合は全商品検索になる")
        void testSearchProductsByCategoryWithZero() {

                List<Product> products = Collections.singletonList(mock(Product.class));

                when(productRepository.findAllWithPaging(0, 10))
                                .thenReturn(products);

                when(productRepository.countAll())
                                .thenReturn(1);

                Model model = new ExtendedModelMap();

                boolean result = adminProductService
                                .searchProductsByCategoryAndSetModel(
                                                0, 1, model);

                assertTrue(result);

                assertEquals(
                                products,
                                model.getAttribute("productList"));

                assertEquals(
                                0,
                                model.getAttribute("selectedCategoryId"));

                verify(productRepository, times(1))
                                .findAllWithPaging(0, 10);

                verify(productRepository, times(1))
                                .countAll();

                // カテゴリ検索用Repositoryは呼ばれない
                verify(productRepository, never())
                                .findByCategoryWithPaging(
                                                anyInt(), anyInt(), anyInt());

                verify(productRepository, never())
                                .countByCategory(anyInt());
        }

        /**
         * categoryId = nullの場合も全商品検索になることをテスト。
         */
        @Test
        @DisplayName("カテゴリIDがnullの場合は全商品検索になる")
        void testSearchProductsByCategoryWithNull() {

                List<Product> products = Collections.singletonList(mock(Product.class));

                when(productRepository.findAllWithPaging(0, 10))
                                .thenReturn(products);

                when(productRepository.countAll())
                                .thenReturn(1);

                Model model = new ExtendedModelMap();

                boolean result = adminProductService
                                .searchProductsByCategoryAndSetModel(
                                                null, 1, model);

                assertTrue(result);

                assertEquals(
                                products,
                                model.getAttribute("productList"));

                assertEquals(
                                0,
                                model.getAttribute("selectedCategoryId"));

                verify(productRepository, times(1))
                                .findAllWithPaging(0, 10);
        }

        /**
         * カテゴリ別検索でページ番号が1未満の場合。
         */
        @Test
        @DisplayName("カテゴリ別検索でページ番号が1未満なら1ページ目として扱う")
        void testSearchProductsByCategoryWithInvalidPage() {

                ProductCategory category = mock(ProductCategory.class);

                when(category.getName())
                                .thenReturn("ノート");

                when(productCategoryRepository.findById(1))
                                .thenReturn(category);

                List<Product> products = Collections.singletonList(mock(Product.class));

                when(productRepository.findByCategoryWithPaging(
                                eq(1), eq(0), eq(10)))
                                .thenReturn(products);

                when(productRepository.countByCategory(1))
                                .thenReturn(1);

                Model model = new ExtendedModelMap();

                boolean result = adminProductService
                                .searchProductsByCategoryAndSetModel(
                                                1, 0, model);

                assertTrue(result);

                assertEquals(
                                1,
                                model.getAttribute("currentPage"));

                verify(productRepository, times(1))
                                .findByCategoryWithPaging(1, 0, 10);
        }

        /**
         * カテゴリ別検索でRepository例外が発生した場合。
         */
        @Test
        @DisplayName("カテゴリ別検索で例外が発生した場合はfalse")
        void testSearchProductsByCategoryWhenException() {

                when(productRepository.findByCategoryWithPaging(
                                eq(1), eq(0), eq(10)))
                                .thenThrow(new RuntimeException("DB error"));

                Model model = new ExtendedModelMap();

                boolean result = adminProductService
                                .searchProductsByCategoryAndSetModel(
                                                1, 1, model);

                assertFalse(result);

                assertNotNull(
                                model.getAttribute("errorMessage"));

                assertTrue(
                                model.getAttribute("errorMessage")
                                                .toString()
                                                .contains("商品情報の取得に失敗しました"));

                assertEquals(
                                true,
                                model.getAttribute("searched"));
        }
}
