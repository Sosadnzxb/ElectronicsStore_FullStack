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

import java.text.ParseException;
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
            int choice;
            while (true) {
                try {
                    String input = scanner.nextLine();
                    choice = parseIntStrict(input, "Пожалуйста, введите одно целое число!");
                    if (choice < 1 || choice > 8) {
                        System.out.println("Неверный выбор! Попробуйте снова (1-8).");
                        continue;
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            switch (choice) {
                case 1 -> manageProducts();
                case 2 -> manageCategories();
                case 3 -> manageCustomers();
                case 4 -> manageOrders();
                case 5 -> manageReviews();
                case 6 -> manageProductCategories();
                case 7 -> manageOrderItems();
                case 8 -> {
                    logger.info("Завершение работы приложения");
                    System.exit(0);
                }
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
        System.out.println("7. Состав заказов");
        System.out.println("8. Выход");
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
            int choice;
            while (true) {
                try {
                    String input = scanner.nextLine();
                    choice = parseIntStrict(input, "Пожалуйста, введите одно целое число!");
                    if (choice < 1 || choice > 5) {
                        System.out.println("Неверный выбор! Попробуйте снова (1-5).");
                        continue;
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            switch (choice) {
                case 1 -> addProduct();
                case 2 -> showAllProducts();
                case 3 -> updateProduct();
                case 4 -> deleteProduct();
                case 5 -> {
                    return;
                }
            }
        }
    }

    private void addProduct() {
        String name;
        while (true) {
            System.out.print("\nНазвание товара: ");
            name = scanner.nextLine();
            if (name.trim().isEmpty()) {
                System.out.println("Название товара не может быть пустым!");
                continue;
            }
            if (!name.matches("^(?!\\d+$)[\\p{IsCyrillic}a-zA-Z0-9 ]+$")) {
                System.out.println("Название должно содержать буквы, можно цифры и пробелы, но не только цифры!");
                continue;
            }
            break;
        }

        double price;
        while (true) {
            System.out.print("Цена: ");
            try {
                String input = scanner.nextLine();
                price = parseDoubleStrict(input, "Цена должна быть числом!");
                if (price <= 0) {
                    System.out.println("Цена должна быть положительной!");
                    continue;
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        String description;
        while (true) {
            System.out.print("Описание: ");
            description = scanner.nextLine();
            if (description.trim().isEmpty()) {
                System.out.println("Описание не может быть пустым!");
                continue;
            }
            if (!description.matches("^(?!\\d+$)[\\p{IsCyrillic}a-zA-Z0-9 ]+$")) {
                System.out.println("Описание должно содержать буквы, можно цифры и пробелы, но не только цифры!");
                continue;
            }
            break;
        }

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
        int id = parseIntWithRetry("\nВведите ID товара для обновления: ",
                "ID товара должен быть целым числом и положительным!",
                1, -1);
        Product existingProduct = productDao.findById(id);
        if (existingProduct == null) {
            System.out.println("Товар с ID " + id + " не найден!");
            return;
        }

        String newName;
        while (true) {
            System.out.print("Новое название: ");
            newName = scanner.nextLine();
            if (newName.trim().isEmpty()) {
                System.out.println("Название товара не может быть пустым!");
                continue;
            }
            if (!newName.matches("^(?!\\d+$)[\\p{IsCyrillic}a-zA-Z0-9 ]+$")) {
                System.out.println("Название должно содержать буквы, можно цифры и пробелы, но не только цифры!");
                continue;
            }
            break;
        }

        double newPrice;
        while (true) {
            System.out.print("Новая цена: ");
            try {
                String input = scanner.nextLine();
                newPrice = parseDoubleStrict(input, "Цена должна быть числом!");
                if (newPrice <= 0) {
                    System.out.println("Цена должна быть положительной!");
                    continue;
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        String newDescription;
        while (true) {
            System.out.print("Новое описание: ");
            newDescription = scanner.nextLine();
            if (newDescription.trim().isEmpty()) {
                System.out.println("Описание не может быть пустым!");
                continue;
            }
            if (!newDescription.matches("^(?!\\d+$)[\\p{IsCyrillic}a-zA-Z0-9 ]+$")) {
                System.out.println("Описание должно содержать буквы, можно цифры и пробелы, но не только цифры!");
                continue;
            }
            break;
        }

        Product product = new Product(newName, newPrice, newDescription);
        product.setId(id);
        productDao.update(product);
        System.out.println("Товар обновлен!");
    }

    private void deleteProduct() {
        int id;
        while (true) {
            showAllProducts();
            id = parseIntWithRetry("\nВведите ID товара для удаления: ",
                    "ID товара должен быть целым числом и положительным!",
                    1, -1);
            Product existingProduct = productDao.findById(id);
            if (existingProduct == null) {
                System.out.println("Товар с ID " + id + " не найден! Попробуйте снова.");
                continue;
            }
            productDao.delete(id);
            System.out.println("Товар удален!");
            break;
        }
    }

    // Управление категориями
    private void manageCategories() {
        while (true) {
            printCrudMenu("категориями");
            int choice;
            while (true) {
                try {
                    String input = scanner.nextLine();
                    choice = parseIntStrict(input, "Пожалуйста, введите одно целое число!");
                    if (choice < 1 || choice > 5) {
                        System.out.println("Неверный выбор! Попробуйте снова (1-5).");
                        continue;
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            switch (choice) {
                case 1 -> addCategory();
                case 2 -> showAllCategories();
                case 3 -> updateCategory();
                case 4 -> deleteCategory();
                case 5 -> {
                    return;
                }
            }
        }
    }

    private void addCategory() {
        String name;
        while (true) {
            System.out.print("\nНазвание категории: ");
            name = scanner.nextLine();
            if (name.trim().isEmpty()) {
                System.out.println("Название категории не может быть пустым!");
                continue;
            }
            if (!name.matches("^(?!\\d+$)[\\p{IsCyrillic}a-zA-Z0-9 ]+$")) {
                System.out.println("Название категории должно содержать буквы, можно цифры и пробелы, но не только цифры!");
                continue;
            }
            break;
        }

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
        int id = parseIntWithRetry("\nВведите ID категории для обновления: ",
                "ID категории должен быть целым числом и положительным!",
                1, -1);
        Category existingCategory = categoryDao.findById(id);
        if (existingCategory == null) {
            System.out.println("Категория с ID " + id + " не найдена!");
            return;
        }

        String newName;
        while (true) {
            System.out.print("Новое название: ");
            newName = scanner.nextLine();
            if (newName.trim().isEmpty()) {
                System.out.println("Название категории не может быть пустым!");
                continue;
            }
            if (!newName.matches("^(?!\\d+$)[\\p{IsCyrillic}a-zA-Z0-9 ]+$")) {
                System.out.println("Название категории должно содержать буквы, можно цифры и пробелы, но не только цифры!");
                continue;
            }
            break;
        }

        Category category = new Category(newName);
        category.setId(id);
        categoryDao.update(category);
        System.out.println("Категория обновлена!");
    }

    private void deleteCategory() {
        int id;
        while (true) {
            showAllCategories();
            id = parseIntWithRetry("\nВведите ID категории для удаления: ",
                    "ID категории должен быть целым числом и положительным!",
                    1, -1);
            Category existingCategory = categoryDao.findById(id);
            if (existingCategory == null) {
                System.out.println("Категория с ID " + id + " не найдена! Попробуйте снова.");
                continue;
            }
            categoryDao.delete(id);
            System.out.println("Категория удалена!");
            break;
        }
    }

    // Управление клиентами
    private void manageCustomers() {
        while (true) {
            printCrudMenu("клиентами");
            int choice;
            while (true) {
                try {
                    String input = scanner.nextLine();
                    choice = parseIntStrict(input, "Пожалуйста, введите одно целое число!");
                    if (choice < 1 || choice > 5) {
                        System.out.println("Неверный выбор! Попробуйте снова (1-5).");
                        continue;
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            switch (choice) {
                case 1 -> addCustomer();
                case 2 -> showAllCustomers();
                case 3 -> updateCustomer();
                case 4 -> deleteCustomer();
                case 5 -> {
                    return;
                }
            }
        }
    }

    private void addCustomer() {
        String name;
        while (true) {
            System.out.print("\nИмя клиента: ");
            name = scanner.nextLine();
            if (name.trim().isEmpty()) {
                System.out.println("Имя не может быть пустым!");
                continue;
            }
            if (!name.matches("[\\p{IsCyrillic}a-zA-Z ]+")) {
                System.out.println("Имя может содержать только буквы (русские или латинские) и пробелы!");
                continue;
            }
            break;
        }

        String email;
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine();
            if (email.trim().isEmpty()) {
                System.out.println("Email не может быть пустым!");
                continue;
            }
            if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                System.out.println("Неверный формат email! Пример: ivan@example.com");
                continue;
            }
            break;
        }

        String phone;
        while (true) {
            System.out.print("Телефон: ");
            phone = scanner.nextLine();
            if (phone.trim().isEmpty()) {
                System.out.println("Телефон не может быть пустым!");
                continue;
            }
            if (!phone.matches("\\+375(29|33|44|25)\\d{7}")) {
                System.out.println("Неверный формат телефона! Пример: +375291234567");
                continue;
            }
            break;
        }

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
        int id = parseIntWithRetry("\nВведите ID клиента для обновления: ",
                "ID клиента должен быть целым числом и положительным!",
                1, -1);
        Customer existingCustomer = customerDao.findById(id);
        if (existingCustomer == null) {
            System.out.println("Клиент с ID " + id + " не найден!");
            return;
        }

        String newName;
        while (true) {
            System.out.print("Новое имя: ");
            newName = scanner.nextLine();
            if (newName.trim().isEmpty()) {
                System.out.println("Имя не может быть пустым!");
                continue;
            }
            if (!newName.matches("[\\p{IsCyrillic}a-zA-Z ]+")) {
                System.out.println("Имя может содержать только буквы (русские или латинские) и пробелы!");
                continue;
            }
            break;
        }

        String newEmail;
        while (true) {
            System.out.print("Новый email: ");
            newEmail = scanner.nextLine();
            if (newEmail.trim().isEmpty()) {
                System.out.println("Email не может быть пустым!");
                continue;
            }
            if (!newEmail.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                System.out.println("Неверный формат email! Пример: ivan@example.com");
                continue;
            }
            break;
        }

        String newPhone;
        while (true) {
            System.out.print("Новый телефон: ");
            newPhone = scanner.nextLine();
            if (newPhone.trim().isEmpty()) {
                System.out.println("Телефон не может быть пустым!");
                continue;
            }
            if (!newPhone.matches("\\+375(29|33|44|25)\\d{7}")) {
                System.out.println("Неверный формат телефона! Пример: +375291234567");
                continue;
            }
            break;
        }

        Customer customer = new Customer(newName, newEmail, newPhone);
        customer.setId(id);
        customerDao.update(customer);
        System.out.println("Клиент обновлен!");
    }

    private void deleteCustomer() {
        int id;
        while (true) {
            showAllCustomers();
            id = parseIntWithRetry("\nВведите ID клиента для удаления: ",
                    "ID клиента должен быть целым числом и положительным!",
                    1, -1);
            Customer existingCustomer = customerDao.findById(id);
            if (existingCustomer == null) {
                System.out.println("Клиент с ID " + id + " не найден! Попробуйте снова.");
                continue;
            }
            customerDao.delete(id);
            System.out.println("Клиент удален!");
            break;
        }
    }

    // Управление заказами
    private void manageOrders() {
        while (true) {
            printCrudMenu("заказами");
            int choice;
            while (true) {
                try {
                    String input = scanner.nextLine();
                    choice = parseIntStrict(input, "Пожалуйста, введите одно целое число!");
                    if (choice < 1 || choice > 5) {
                        System.out.println("Неверный выбор! Попробуйте снова (1-5).");
                        continue;
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            switch (choice) {
                case 1 -> addOrder();
                case 2 -> showAllOrders();
                case 3 -> updateOrder();
                case 4 -> deleteOrder();
                case 5 -> {
                    return;
                }
            }
        }
    }

    private void addOrder() {
        int customerId;
        while (true) {
            showAllCustomers();
            customerId = parseIntWithRetry("\nВведите ID клиента: ",
                    "ID клиента должен быть целым числом и положительным!",
                    1, -1);
            if (customerDao.findById(customerId) == null) {
                System.out.println("Клиент с ID " + customerId + " не найден! Попробуйте снова.");
                continue;
            }
            break;
        }

        Date orderDate;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        sdf.setLenient(false);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date currentDate = calendar.getTime();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        Date maxDate = calendar.getTime();

        while (true) {
            System.out.print("Дата заказа (dd.MM.yyyy): ");
            String dateStr = scanner.nextLine();
            if (dateStr.trim().isEmpty()) {
                System.out.println("Дата не может быть пустой!");
                continue;
            }
            try {
                orderDate = sdf.parse(dateStr);
                Calendar orderCal = Calendar.getInstance();
                orderCal.setTime(orderDate);
                orderCal.set(Calendar.HOUR_OF_DAY, 0);
                orderCal.set(Calendar.MINUTE, 0);
                orderCal.set(Calendar.SECOND, 0);
                orderCal.set(Calendar.MILLISECOND, 0);
                orderDate = orderCal.getTime();
                if (orderDate.before(currentDate) || orderDate.after(maxDate)) {
                    System.out.println("Дата должна быть от сегодня до 31.12." + (calendar.get(Calendar.YEAR)) + "! Пример: 27.06.2025");
                    continue;
                }
                break;
            } catch (ParseException e) {
                System.out.println("Неверный формат даты или дата не существует! Пример: 27.06.2025");
            }
        }

        String status;
        while (true) {
            System.out.println("\nВыберите статус:");
            System.out.println("1. Без товара");
            System.out.println("2. В процессе");
            System.out.println("3. Готов к выдаче");
            int statusChoice = parseIntWithRetry("Введите номер (1-3): ",
                    "Пожалуйста, выберите номер статуса (1-3)!",
                    1, 3);
            status = switch (statusChoice) {
                case 1 -> "без товара";
                case 2 -> "в процессе";
                case 3 -> "готов к выдаче";
                default -> throw new IllegalStateException("Недопустимый выбор статуса");
            };
            break;
        }

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
        int id = parseIntWithRetry("\nВведите ID заказа для обновления: ",
                "ID заказа должен быть целым числом и положительным!",
                1, -1);
        Order existingOrder = orderDao.findById(id);
        if (existingOrder == null) {
            System.out.println("Заказ с ID " + id + " не найден!");
            return;
        }

        int newCustomerId;
        while (true) {
            showAllCustomers();
            newCustomerId = parseIntWithRetry("Новый ID клиента: ",
                    "ID клиента должен быть целым числом и положительным!",
                    1, -1);
            if (customerDao.findById(newCustomerId) == null) {
                System.out.println("Клиент с ID " + newCustomerId + " не найден! Попробуйте снова.");
                continue;
            }
            break;
        }

        Date newOrderDate;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        sdf.setLenient(false);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date currentDate = calendar.getTime();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        Date maxDate = calendar.getTime();

        while (true) {
            System.out.print("Новая дата заказа (dd.MM.yyyy): ");
            String dateStr = scanner.nextLine();
            if (dateStr.trim().isEmpty()) {
                System.out.println("Дата не может быть пустой!");
                continue;
            }
            try {
                newOrderDate = sdf.parse(dateStr);
                Calendar orderCal = Calendar.getInstance();
                orderCal.setTime(newOrderDate);
                orderCal.set(Calendar.HOUR_OF_DAY, 0);
                orderCal.set(Calendar.MINUTE, 0);
                orderCal.set(Calendar.SECOND, 0);
                orderCal.set(Calendar.MILLISECOND, 0);
                newOrderDate = orderCal.getTime();
                if (newOrderDate.before(currentDate) || newOrderDate.after(maxDate)) {
                    System.out.println("Дата должна быть от сегодня до 31.12." + (calendar.get(Calendar.YEAR)) + "! Пример: 27.06.2025");
                    continue;
                }
                break;
            } catch (ParseException e) {
                System.out.println("Неверный формат даты или дата не существует! Пример: 27.06.2025");
            }
        }

        String newStatus;
        while (true) {
            System.out.println("\nВыберите новый статус:");
            System.out.println("1. Без товара");
            System.out.println("2. В процессе");
            System.out.println("3. Готов к выдаче");
            int statusChoice = parseIntWithRetry("Введите номер (1-3): ",
                    "Пожалуйста, выберите номер статуса (1-3)!",
                    1, 3);
            newStatus = switch (statusChoice) {
                case 1 -> "без товара";
                case 2 -> "в процессе";
                case 3 -> "готов к выдаче";
                default -> throw new IllegalStateException("Недопустимый выбор статуса");
            };
            break;
        }

        Order order = new Order(newCustomerId, newOrderDate, newStatus);
        order.setId(id);
        orderDao.update(order);
        System.out.println("Заказ обновлен!");
    }

    private void deleteOrder() {
        int id;
        while (true) {
            showAllOrders();
            id = parseIntWithRetry("\nВведите ID заказа для удаления: ",
                    "ID заказа должен быть целым числом и положительным!",
                    1, -1);
            Order existingOrder = orderDao.findById(id);
            if (existingOrder == null) {
                System.out.println("Заказ с ID " + id + " не найден! Попробуйте снова.");
                continue;
            }
            orderDao.delete(id);
            System.out.println("Заказ удален!");
            break;
        }
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

            int choice;
            while (true) {
                try {
                    String input = scanner.nextLine();
                    choice = parseIntStrict(input, "Пожалуйста, введите одно целое число!");
                    if (choice < 1 || choice > 3) {
                        System.out.println("Неверный выбор! Попробуйте снова (1-3).");
                        continue;
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            switch (choice) {
                case 1 -> addProductToCategoryWithSelection();
                case 2 -> showCategoriesForProductWithSelection();
                case 3 -> {
                    return;
                }
            }
        }
    }

    private void addProductToCategoryWithSelection() {
        System.out.println("\nСписок товаров:");
        List<Product> products = productDao.findAll();
        if (products.isEmpty()) {
            System.out.println("Нет доступных товаров!");
            return;
        }
        products.forEach(p -> System.out.printf("%d. %s\n", p.getId(), p.getName()));

        int productId;
        while (true) {
            productId = parseIntWithRetry("\nВыберите ID товара: ",
                    "ID товара должен быть целым числом и положительным!",
                    1, -1);
            if (productDao.findById(productId) == null) {
                System.out.println("Товар с ID " + productId + " не существует! Попробуйте снова.");
                continue;
            }
            break;
        }

        System.out.println("\nСписок категорий:");
        List<Category> categories = categoryDao.findAll();
        if (categories.isEmpty()) {
            System.out.println("Нет доступных категорий!");
            return;
        }
        categories.forEach(c -> System.out.printf("%d. %s\n", c.getId(), c.getName()));

        int categoryId;
        while (true) {
            categoryId = parseIntWithRetry("\nВыберите ID категории: ",
                    "ID категории должен быть целым числом и положительным!",
                    1, -1);
            if (categoryDao.findById(categoryId) == null) {
                System.out.println("Категория с ID " + categoryId + " не существует! Попробуйте снова.");
                continue;
            }
            break;
        }

        try {
            productCategoryDao.addProductToCategory(productId, categoryId);
            System.out.println("Товар успешно добавлен в категорию!");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showCategoriesForProductWithSelection() {
        System.out.println("\nСписок товаров:");
        List<Product> products = productDao.findAll();
        if (products.isEmpty()) {
            System.out.println("Нет доступных товаров!");
            return;
        }
        products.forEach(p -> System.out.printf("%d. %s\n", p.getId(), p.getName()));

        int productId;
        while (true) {
            productId = parseIntWithRetry("\nВыберите ID товара: ",
                    "ID товара должен быть целым числом и положительным!",
                    1, -1);
            if (productDao.findById(productId) == null) {
                System.out.println("Товар с ID " + productId + " не существует! Попробуйте снова.");
                continue;
            }
            break;
        }

        List<Integer> categoryIds = productCategoryDao.findCategoriesByProduct(productId);
        if (categoryIds.isEmpty()) {
            System.out.println("У товара нет категорий!");
            return;
        }

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
            int choice;
            while (true) {
                try {
                    String input = scanner.nextLine();
                    choice = parseIntStrict(input, "Пожалуйста, введите одно целое число!");
                    if (choice < 1 || choice > 5) {
                        System.out.println("Неверный выбор! Попробуйте снова (1-5).");
                        continue;
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            switch (choice) {
                case 1 -> addReview();
                case 2 -> showAllReviews();
                case 3 -> updateReview();
                case 4 -> deleteReview();
                case 5 -> {
                    return;
                }
            }
        }
    }

    private void addReview() {
        int productId;
        while (true) {
            showAllProducts();
            productId = parseIntWithRetry("\nID товара: ",
                    "ID товара должен быть целым числом и положительным!",
                    1, -1);
            if (productDao.findById(productId) == null) {
                System.out.println("Товар с ID " + productId + " не найден! Попробуйте снова.");
                continue;
            }
            break;
        }

        int customerId;
        while (true) {
            showAllCustomers();
            customerId = parseIntWithRetry("ID клиента: ",
                    "ID клиента должен быть целым числом и положительным!",
                    1, -1);
            if (customerDao.findById(customerId) == null) {
                System.out.println("Клиент с ID " + customerId + " не найден! Попробуйте снова.");
                continue;
            }
            break;
        }

        int rating = parseIntWithRetry("Рейтинг (1-5): ",
                "Рейтинг должен быть целым числом от 1 до 5!",
                1, 5);

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
        int id = parseIntWithRetry("\nВведите ID отзыва для обновления: ",
                "ID отзыва должен быть целым числом и положительным!",
                1, -1);
        Review existingReview = reviewDao.findById(id);
        if (existingReview == null) {
            System.out.println("Отзыв с ID " + id + " не найден!");
            return;
        }

        int newRating = parseIntWithRetry("Новый рейтинг (1-5): ",
                "Рейтинг должен быть целым числом от 1 до 5!",
                1, 5);

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
        int id;
        while (true) {
            showAllReviews();
            id = parseIntWithRetry("\nВведите ID отзыва для удаления: ",
                    "ID отзыва должен быть целым числом и положительным!",
                    1, -1);
            Review existingReview = reviewDao.findById(id);
            if (existingReview == null) {
                System.out.println("Отзыв с ID " + id + " не найден! Попробуйте снова.");
                continue;
            }
            reviewDao.delete(id);
            System.out.println("Отзыв удален!");
            break;
        }
    }

    // Управление составом заказов
    private void manageOrderItems() {
        while (true) {
            if (orderDao.findAll().isEmpty()) {
                System.out.println("\nНет доступных заказов! Добавьте заказы сначала.");
                return;
            }
            if (productDao.findAll().isEmpty()) {
                System.out.println("\nНет доступных товаров! Добавьте товары сначала.");
                return;
            }

            System.out.println("\n=== Управление составом заказов ===");
            System.out.println("1. Добавить товар в заказ");
            System.out.println("2. Просмотреть состав заказа");
            System.out.println("3. Удалить товар из заказа");
            System.out.println("4. Назад");
            System.out.print("Выберите действие: ");

            int choice;
            while (true) {
                try {
                    String input = scanner.nextLine();
                    choice = parseIntStrict(input, "Пожалуйста, введите одно целое число!");
                    if (choice < 1 || choice > 4) {
                        System.out.println("Неверный выбор! Попробуйте снова (1-4).");
                        continue;
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }

            switch (choice) {
                case 1 -> addOrderItem();
                case 2 -> showOrderItems();
                case 3 -> deleteOrderItem();
                case 4 -> {
                    return;
                }
            }
        }
    }

    private void addOrderItem() {
        int orderId;
        while (true) {
            showAllOrders();
            orderId = parseIntWithRetry("\nВведите ID заказа: ",
                    "ID заказа должен быть целым числом и положительным!",
                    1, -1);
            if (orderDao.findById(orderId) == null) {
                System.out.println("Заказ с ID " + orderId + " не найден! Попробуйте снова.");
                continue;
            }
            break;
        }

        int productId;
        while (true) {
            showAllProducts();
            productId = parseIntWithRetry("Введите ID товара: ",
                    "ID товара должен быть целым числом и положительным!",
                    1, -1);
            if (productDao.findById(productId) == null) {
                System.out.println("Товар с ID " + productId + " не найден! Попробуйте снова.");
                continue;
            }
            break;
        }

        int quantity = parseIntWithRetry("Введите количество: ",
                "Количество должно быть целым числом и положительным!",
                1, -1);

        OrderItem orderItem = new OrderItem(orderId, productId, quantity);
        orderItemDao.save(orderItem);
        System.out.println("Товар добавлен в заказ!");
    }

    private void showOrderItems() {
        showAllOrders();
        int orderId = parseIntWithRetry("\nВведите ID заказа для просмотра состава: ",
                "ID заказа должен быть целым числом и положительным!",
                1, -1);

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
        int orderId;
        while (true) {
            showAllOrders();
            orderId = parseIntWithRetry("\nВведите ID заказа: ",
                    "ID заказа должен быть целым числом и положительным!",
                    1, -1);
            List<OrderItem> items = orderItemDao.findByOrderId(orderId);
            if (items.isEmpty()) {
                System.out.println("Заказ с ID " + orderId + " пуст или не существует! Попробуйте снова.");
                continue;
            }
            System.out.println("\nТовары в заказе:");
            items.forEach(item -> {
                Product product = productDao.findById(item.getProductId());
                System.out.printf("ID позиции: %d - Товар: %s (Количество: %d)\n",
                        item.getId(), product.getName(), item.getQuantity());
            });
            break;
        }

        int itemId;
        while (true) {
            itemId = parseIntWithRetry("Введите ID позиции для удаления: ",
                    "ID позиции должен быть целым числом и положительным!",
                    1, -1);
            OrderItem existingItem = orderItemDao.findById(itemId);
            if (existingItem == null || existingItem.getOrderId() != orderId) {
                System.out.println("Позиция с ID " + itemId + " не найдена в этом заказе! Попробуйте снова.");
                continue;
            }
            orderItemDao.delete(itemId);
            System.out.println("Товар удалён из заказа!");
            break;
        }
    }

    private int parseIntStrict(String input, String errorMessage) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Ввод не может быть пустым!");
        }
        // Проверяем, что строка не содержит пробелов или других лишних символов
        if (!input.matches("-?\\d+")) {
            throw new IllegalArgumentException(errorMessage);
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private double parseDoubleStrict(String input, String errorMessage) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Ввод не может быть пустым!");
        }
        if (!input.matches("-?\\d+(\\.\\d+)?")) {
            throw new IllegalArgumentException(errorMessage);
        }
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private int parseIntWithRetry(String prompt, String errorMessage, int minValue, int maxValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println("Ввод не может быть пустым!");
                continue;
            }
            try {
                int value = parseIntStrict(input, errorMessage);
                if (value < minValue || (maxValue != -1 && value > maxValue)) {
                    System.out.println(errorMessage);
                    continue;
                }
                return value;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}