package com.electronics.store.service;

import com.electronics.store.dao.CategoryDao;
import com.electronics.store.dao.CustomerDao;
import com.electronics.store.dao.OrderDao;
import com.electronics.store.dao.ProductDao;
import com.electronics.store.dao.OrderItemDao;
import com.electronics.store.dao.ProductCategoryDao;
import com.electronics.store.dao.ReviewDao;

import com.electronics.store.model.Category;
import com.electronics.store.model.Customer;
import com.electronics.store.model.Order;
import com.electronics.store.model.OrderItem;
import com.electronics.store.model.Product;
import com.electronics.store.model.ProductCategory;
import com.electronics.store.model.Review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class StoreService
{
    private static final Logger logger = LoggerFactory.getLogger(StoreService.class);
    private final ProductDao productDao = new ProductDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private final CustomerDao customerDao = new CustomerDao();
    private final OrderDao orderDao = new OrderDao();
    private final ReviewDao reviewDao = new ReviewDao();
    private final OrderItemDao orderItemDao = new OrderItemDao();
    private final ProductCategoryDao productCategoryDao = new ProductCategoryDao();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        logger.info("Запуск приложения");
        while (true) {
            printMainMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> manageProducts();
                case 2 -> manageCategories();
                case 3 -> manageCustomers();
                case 4 -> manageOrders();
                case 5 -> manageReviews();
                case 6 -> manageProductCategories(); // Новый пункт для связи товаров и категорий
                case 7 -> manageOrderItems(); // Новый пункт
                case 8 -> { // Выход теперь 8
                    logger.info("Завершение работы приложения");
                    System.exit(0);
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n=== Управление магазином ===");
        System.out.println("1. Товары");
        System.out.println("2. Категории");
        System.out.println("3. Клиенты");
        System.out.println("4. Заказы");
        System.out.println("5. Отзывы");
        System.out.println("6. Связи товаров и категорий");
        System.out.println("7. Состав заказов"); // Новый пункт
        System.out.println("8. Выход"); // Сдвигаем выход на 8
        System.out.print("Выберите раздел: ");
    }

    private void printCrudMenu(String entity) {
        System.out.println("\n=== Управление " + entity + " ===");
        System.out.println("1. Добавить");
        System.out.println("2. Показать все");
        System.out.println("3. Обновить");
        System.out.println("4. Удалить");
        System.out.println("5. Назад");
        System.out.print("Выберите действие: ");
    }

    // Управление товарами
    private void manageProducts() {
        while (true) {
            printCrudMenu("товарами");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> addProduct();
                case 2 -> showAllProducts();
                case 3 -> updateProduct();
                case 4 -> deleteProduct();
                case 5 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private void addProduct() {
        System.out.print("\nНазвание товара: ");
        String name = scanner.nextLine();
        System.out.print("Цена: ");
        double price = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Описание: ");
        String description = scanner.nextLine();
        Product product = new Product(name, price, description);
        productDao.save(product);
        System.out.println("Товар добавлен! ID: " + product.getId());
    }

    private void showAllProducts() {
        List<Product> products = productDao.findAll();
        if (products.isEmpty()) {
            System.out.println("\nСписок товаров пуст!");
            return;
        }
        System.out.println("\n=== Список товаров ===");
        products.forEach(p -> System.out.printf("%d. %s - %.2f - %s\n", p.getId(), p.getName(), p.getPrice(), p.getDescription()));
    }

    private void updateProduct() {
        showAllProducts();
        System.out.print("\nВведите ID товара для обновления: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Product existingProduct = productDao.findById(id);
        if (existingProduct == null) {
            System.out.println("Товар с ID " + id + " не найден!");
            return;
        }
        System.out.print("Новое название: ");
        String newName = scanner.nextLine();
        System.out.print("Новая цена: ");
        double newPrice = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Новое описание: ");
        String newDescription = scanner.nextLine();
        Product product = new Product(newName, newPrice, newDescription);
        product.setId(id);
        productDao.update(product);
        System.out.println("Товар обновлен!");
    }

    private void deleteProduct() {
        showAllProducts();
        System.out.print("\nВведите ID товара для удаления: ");
        int id = scanner.nextInt();
        Product existingProduct = productDao.findById(id);
        if (existingProduct == null) {
            System.out.println("Товар с ID " + id + " не найден!");
            return;
        }
        productDao.delete(id);
        System.out.println("Товар удален!");
    }

    // Управление категориями
    private void manageCategories() {
        while (true) {
            printCrudMenu("категориями");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> addCategory();
                case 2 -> showAllCategories();
                case 3 -> updateCategory();
                case 4 -> deleteCategory();
                case 5 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private void addCategory() {
        System.out.print("\nНазвание категории: ");
        String name = scanner.nextLine();
        Category category = new Category(name);
        categoryDao.save(category);
        System.out.println("Категория добавлена! ID: " + category.getId());
    }

    private void showAllCategories() {
        List<Category> categories = categoryDao.findAll();
        if (categories.isEmpty()) {
            System.out.println("\nСписок категорий пуст!");
            return;
        }
        System.out.println("\n=== Список категорий ===");
        categories.forEach(c -> System.out.printf("%d. %s\n", c.getId(), c.getName()));
    }

    private void updateCategory() {
        showAllCategories();
        System.out.print("\nВведите ID категории для обновления: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Category existingCategory = categoryDao.findById(id);
        if (existingCategory == null) {
            System.out.println("Категория с ID " + id + " не найдена!");
            return;
        }
        System.out.print("Новое название: ");
        String newName = scanner.nextLine();
        Category category = new Category(newName);
        category.setId(id);
        categoryDao.update(category);
        System.out.println("Категория обновлена!");
    }

    private void deleteCategory() {
        showAllCategories();
        System.out.print("\nВведите ID категории для удаления: ");
        int id = scanner.nextInt();
        Category existingCategory = categoryDao.findById(id);
        if (existingCategory == null) {
            System.out.println("Категория с ID " + id + " не найдена!");
            return;
        }
        categoryDao.delete(id);
        System.out.println("Категория удалена!");
    }

    // Управление клиентами
    private void manageCustomers() {
        while (true) {
            printCrudMenu("клиентами");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> addCustomer();
                case 2 -> showAllCustomers();
                case 3 -> updateCustomer();
                case 4 -> deleteCustomer();
                case 5 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private void addCustomer() {
        System.out.print("\nИмя клиента: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Телефон: ");
        String phone = scanner.nextLine();
        Customer customer = new Customer(name, email, phone);
        customerDao.save(customer);
        System.out.println("Клиент добавлен! ID: " + customer.getId());
    }

    private void showAllCustomers() {
        List<Customer> customers = customerDao.findAll();
        if (customers.isEmpty()) {
            System.out.println("\nСписок клиентов пуст!");
            return;
        }
        System.out.println("\n=== Список клиентов ===");
        customers.forEach(c -> System.out.printf("%d. %s - %s - %s\n", c.getId(), c.getName(), c.getEmail(), c.getPhone()));
    }

    private void updateCustomer() {
        showAllCustomers();
        System.out.print("\nВведите ID клиента для обновления: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Customer existingCustomer = customerDao.findById(id);
        if (existingCustomer == null) {
            System.out.println("Клиент с ID " + id + " не найден!");
            return;
        }
        System.out.print("Новое имя: ");
        String newName = scanner.nextLine();
        System.out.print("Новый email: ");
        String newEmail = scanner.nextLine();
        System.out.print("Новый телефон: ");
        String newPhone = scanner.nextLine();
        Customer customer = new Customer(newName, newEmail, newPhone);
        customer.setId(id);
        customerDao.update(customer);
        System.out.println("Клиент обновлен!");
    }

    private void deleteCustomer() {
        showAllCustomers();
        System.out.print("\nВведите ID клиента для удаления: ");
        int id = scanner.nextInt();
        Customer existingCustomer = customerDao.findById(id);
        if (existingCustomer == null) {
            System.out.println("Клиент с ID " + id + " не найден!");
            return;
        }
        customerDao.delete(id);
        System.out.println("Клиент удален!");
    }

    // Управление заказами
    private void manageOrders() {
        while (true) {
            printCrudMenu("заказами");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> addOrder();
                case 2 -> showAllOrders();
                case 3 -> updateOrder();
                case 4 -> deleteOrder();
                case 5 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private void addOrder() {
        showAllCustomers();
        System.out.print("\nВведите ID клиента: ");
        int customerId = scanner.nextInt();
        scanner.nextLine();
        if (customerDao.findById(customerId) == null) {
            System.out.println("Клиент с ID " + customerId + " не найден!");
            return;
        }
        System.out.print("Дата заказа (dd.MM.yyyy): ");
        String dateStr = scanner.nextLine();
        Date orderDate;
        try {
            orderDate = new SimpleDateFormat("dd.MM.yyyy").parse(dateStr);
        } catch (Exception e) {
            System.out.println("Неверный формат даты!");
            return;
        }
        System.out.print("Статус (например, processing): ");
        String status = scanner.nextLine();
        Order order = new Order(customerId, orderDate, status);
        orderDao.save(order);
        System.out.println("Заказ добавлен! ID: " + order.getId());
    }

    private void showAllOrders() {
        List<Order> orders = orderDao.findAll();
        if (orders.isEmpty()) {
            System.out.println("\nСписок заказов пуст!");
            return;
        }
        System.out.println("\n=== Список заказов ===");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        orders.forEach(o -> System.out.printf("%d. Клиент ID: %d - Дата: %s - Статус: %s\n",
                o.getId(), o.getCustomerId(), sdf.format(o.getOrderDate()), o.getStatus()));
    }

    private void updateOrder() {
        showAllOrders();
        System.out.print("\nВведите ID заказа для обновления: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Order existingOrder = orderDao.findById(id);
        if (existingOrder == null) {
            System.out.println("Заказ с ID " + id + " не найден!");
            return;
        }
        showAllCustomers();
        System.out.print("Новый ID клиента: ");
        int newCustomerId = scanner.nextInt();
        scanner.nextLine();
        if (customerDao.findById(newCustomerId) == null) {
            System.out.println("Клиент с ID " + newCustomerId + " не найден!");
            return;
        }
        System.out.print("Новая дата заказа (dd.MM.yyyy): ");
        String dateStr = scanner.nextLine();
        Date newOrderDate;
        try {
            newOrderDate = new SimpleDateFormat("dd.MM.yyyy").parse(dateStr);
        } catch (Exception e) {
            System.out.println("Неверный формат даты!");
            return;
        }
        System.out.print("Новый статус: ");
        String newStatus = scanner.nextLine();
        Order order = new Order(newCustomerId, newOrderDate, newStatus);
        order.setId(id);
        orderDao.update(order);
        System.out.println("Заказ обновлен!");
    }

    private void deleteOrder() {
        showAllOrders();
        System.out.print("\nВведите ID заказа для удаления: ");
        int id = scanner.nextInt();
        Order existingOrder = orderDao.findById(id);
        if (existingOrder == null) {
            System.out.println("Заказ с ID " + id + " не найден!");
            return;
        }
        orderDao.delete(id);
        System.out.println("Заказ удален!");
    }

    // ===== Методы для связи товаров и категорий =====
    // Управление связями товаров и категорий
    private void manageProductCategories() {
        while (true) {
            System.out.println("\n=== Управление связями ===");
            System.out.println("1. Добавить товар в категорию");
            System.out.println("2. Показать категории товара");
            System.out.println("3. Назад");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> addProductToCategoryWithSelection();
                case 2 -> showCategoriesForProductWithSelection();
                case 3 -> { return; }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private void addProductToCategoryWithSelection() {
        // Показываем список товаров
        System.out.println("\nСписок товаров:");
        List<Product> products = productDao.findAll();
        if (products.isEmpty()) {
            System.out.println("Нет доступных товаров!");
            return;
        }
        products.forEach(p -> System.out.printf("%d. %s\n", p.getId(), p.getName()));

        System.out.print("\nВыберите ID товара: ");
        int productId = scanner.nextInt();
        scanner.nextLine();

        // Проверяем существование товара
        if (productDao.findById(productId) == null) {
            System.out.println("Товар с таким ID не существует!");
            return;
        }

        // Показываем список категорий
        System.out.println("\nСписок категорий:");
        List<Category> categories = categoryDao.findAll();
        if (categories.isEmpty()) {
            System.out.println("Нет доступных категорий!");
            return;
        }
        categories.forEach(c -> System.out.printf("%d. %s\n", c.getId(), c.getName()));

        System.out.print("\nВыберите ID категории: ");
        int categoryId = scanner.nextInt();
        scanner.nextLine();

        // Проверяем существование категории
        if (categoryDao.findById(categoryId) == null) {
            System.out.println("Категория с таким ID не существует!");
            return;
        }

        // Добавляем связь
        try {
            productCategoryDao.addProductToCategory(productId, categoryId);
            System.out.println("Товар успешно добавлен в категорию!");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showCategoriesForProductWithSelection() {
        // Показываем список товаров
        System.out.println("\nСписок товаров:");
        List<Product> products = productDao.findAll();
        if (products.isEmpty()) {
            System.out.println("Нет доступных товаров!");
            return;
        }
        products.forEach(p -> System.out.printf("%d. %s\n", p.getId(), p.getName()));

        System.out.print("\nВыберите ID товара: ");
        int productId = scanner.nextInt();
        scanner.nextLine();

        // Получаем категории товара
        List<Integer> categoryIds = productCategoryDao.findCategoriesByProduct(productId);
        if (categoryIds.isEmpty()) {
            System.out.println("У товара нет категорий!");
            return;
        }

        // Показываем категории
        System.out.println("\nКатегории товара:");
        categoryIds.forEach(catId -> {
            Category cat = categoryDao.findById(catId);
            if (cat != null) {
                System.out.printf("%d. %s\n", cat.getId(), cat.getName());
            }
        });
    }

    // Управление отзывами
    private void manageReviews() {
        while (true) {
            printCrudMenu("отзывами");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> addReview();
                case 2 -> showAllReviews();
                case 3 -> updateReview();
                case 4 -> deleteReview();
                case 5 -> { return; }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private void addReview() {
        System.out.print("\nID товара: ");
        int productId = scanner.nextInt();
        System.out.print("ID клиента: ");
        int customerId = scanner.nextInt();
        System.out.print("Рейтинг (1-5): ");
        int rating = scanner.nextInt();
        scanner.nextLine(); // Очистка буфера
        System.out.print("Комментарий: ");
        String comment = scanner.nextLine();

        Review review = new Review(productId, customerId, rating, comment);
        reviewDao.save(review);
        System.out.println("Отзыв добавлен! ID: " + review.getId());
    }

    private void showAllReviews() {
        List<Review> reviews = reviewDao.findAll();
        if (reviews.isEmpty()) {
            System.out.println("\nСписок отзывов пуст!");
            return;
        }
        System.out.println("\n=== Список отзывов ===");
        reviews.forEach(r -> System.out.printf(
                "%d. Товар ID: %d, Клиент ID: %d, Рейтинг: %d, Комментарий: %s\n",
                r.getId(), r.getProductId(), r.getCustomerId(), r.getRating(), r.getComment()
        ));
    }

    private void updateReview() {
        showAllReviews();
        System.out.print("\nВведите ID отзыва для обновления: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Review existingReview = reviewDao.findById(id);
        if (existingReview == null) {
            System.out.println("Отзыв с ID " + id + " не найден!");
            return;
        }

        System.out.print("Новый рейтинг (1-5): ");
        int newRating = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Новый комментарий: ");
        String newComment = scanner.nextLine();

        Review updatedReview = new Review(
                existingReview.getProductId(),
                existingReview.getCustomerId(),
                newRating,
                newComment
        );
        updatedReview.setId(id);
        reviewDao.update(updatedReview);
        System.out.println("Отзыв обновлен!");
    }

    private void deleteReview() {
        showAllReviews();
        System.out.print("\nВведите ID отзыва для удаления: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Review existingReview = reviewDao.findById(id);
        if (existingReview == null) {
            System.out.println("Отзыв с ID " + id + " не найден!");
            return;
        }

        reviewDao.delete(id);
        System.out.println("Отзыв удален!");
    }

    // Управление составом заказов
    private void manageOrderItems() {
        while (true) {
            System.out.println("\n=== Управление составом заказов ===");
            System.out.println("1. Добавить товар в заказ");
            System.out.println("2. Просмотреть состав заказа");
            System.out.println("3. Удалить товар из заказа");
            System.out.println("4. Назад");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> addOrderItem();
                case 2 -> showOrderItems();
                case 3 -> deleteOrderItem();
                case 4 -> { return; }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private void addOrderItem() {
        showAllOrders();
        System.out.print("\nВведите ID заказа: ");
        int orderId = scanner.nextInt();

        showAllProducts();
        System.out.print("Введите ID товара: ");
        int productId = scanner.nextInt();

        System.out.print("Введите количество: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        OrderItem orderItem = new OrderItem(orderId, productId, quantity);
        orderItemDao.save(orderItem);
        System.out.println("Товар добавлен в заказ!");
    }

    private void showOrderItems() {
        showAllOrders();
        System.out.print("\nВведите ID заказа для просмотра состава: ");
        int orderId = scanner.nextInt();
        scanner.nextLine();

        List<OrderItem> items = orderItemDao.findByOrderId(orderId);
        if (items.isEmpty()) {
            System.out.println("Заказ пуст!");
            return;
        }

        System.out.println("\n=== Состав заказа ===");
        items.forEach(item -> {
            Product product = productDao.findById(item.getProductId());
            System.out.printf("Товар: %s (ID: %d) - Количество: %d\n",
                    product.getName(), product.getId(), item.getQuantity());
        });
    }

    private void deleteOrderItem() {
        showAllOrders();
        System.out.print("\nВведите ID заказа: ");
        int orderId = scanner.nextInt();

        List<OrderItem> items = orderItemDao.findByOrderId(orderId);
        if (items.isEmpty()) {
            System.out.println("Заказ пуст!");
            return;
        }

        System.out.println("\nТовары в заказе:");
        items.forEach(item -> {
            Product product = productDao.findById(item.getProductId());
            System.out.printf("ID позиции: %d - Товар: %s (Количество: %d)\n",
                    item.getId(), product.getName(), item.getQuantity());
        });

        System.out.print("Введите ID позиции для удаления: ");
        int itemId = scanner.nextInt();
        scanner.nextLine();

        orderItemDao.delete(itemId);
        System.out.println("Товар удалён из заказа!");
    }
}