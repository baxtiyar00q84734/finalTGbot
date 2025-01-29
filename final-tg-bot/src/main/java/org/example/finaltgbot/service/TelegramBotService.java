package org.example.finaltgbot.service;

import lombok.RequiredArgsConstructor;
import org.example.finaltgbot.config.MessageProvider;
import org.example.finaltgbot.config.TelegramConfig;
import org.example.finaltgbot.dto.response.ProductResponseDTO;
import org.example.finaltgbot.dto.telegram.KeyboardButtonDTO;
import org.example.finaltgbot.dto.telegram.ReplyKeyboardMarkupDTO;
import org.example.finaltgbot.dto.telegram.SendMessageDTO;
import org.example.finaltgbot.dto.telegram.TelegramRoot;
import org.example.finaltgbot.dto.telegram.send.TelegramSendDTO;
import org.example.finaltgbot.entity.Order;
import org.example.finaltgbot.entity.Product;
import org.example.finaltgbot.entity.User;
import org.example.finaltgbot.enums.Language;
import org.example.finaltgbot.enums.OrderStatus;
import org.example.finaltgbot.enums.OrderStep;
import org.example.finaltgbot.enums.RegistrationStep;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramBotService {

    private final TelegramConfig telegramConfig;
    private final UserService userService;
    private final MessageProvider messageProvider;
    private final OrderService orderService;
    private final ProductService productService;

    private Long offset = 0L;

    @Scheduled(fixedRate = 3000)
    public void getMessage() {
        System.out.println("Offset : " + offset);

        TelegramRoot updates = telegramConfig.getUpdates(offset);

        if (!updates.getResult().isEmpty()) {
            updates.getResult().forEach(update -> {
                Long chatId = update.getMessage().getChat().getId();
                String messageText = update.getMessage().getText();

                processMessage(chatId, messageText);

                offset = (update.getUpdate_id() + 1);
            });
        }
    }

    public void sendMessage(Long chatId, String text) {
        TelegramSendDTO message = new TelegramSendDTO();
        message.setChatId(chatId);
        message.setText(text);
        telegramConfig.sendSimpleMessage(chatId, text);
    }

    public void processMessage(Long chatId, String message) {
        User user = userService.findByChatId(chatId);

        if (user == null) {
            userService.startRegistration(chatId);
            sendMessage(chatId, "Welcome! Please register to place an order. What is your first name?");
            return;
        }

        if (user.getRegistrationStep() != RegistrationStep.COMPLETED) {
            handleRegistrationSteps(chatId, message, user);
        } else {
            switch (message.toLowerCase()) {
                case "/order":
                    if (isUserEligibleForOrder(user)) {
                    user.setOrderStep(OrderStep.SELECT_PRODUCT);
                    userService.save(user);
                    handleOrder(chatId, message, user);
                } else {
                    sendMessage(chatId, "You are not eligible to place an order at this moment.");
                }
                break;

                case "/delete_order":
                    handleDeleteOrderCommand(chatId, user);
                    break;

                case "/orders_list":
                    handleOrdersListCommand(chatId, user);
                    break;

                default:
                    if (user.getOrderStep() != OrderStep.COMPLETED) {
                        handleOrder(chatId, message, user);
                    }
                    break;
            }
        }
    }

    private void handleRegistrationSteps(Long chatId, String message, User user) {
        switch (user.getRegistrationStep()) {
            case ASK_NAME:
                user.setFirstName(message);
                user.setRegistrationStep(RegistrationStep.ASK_SURNAME);
                userService.save(user);
                sendMessage(chatId, "Thank you! What is your last name?");
                break;
            case ASK_SURNAME:
                user.setLastName(message);
                user.setRegistrationStep(RegistrationStep.ASK_EMAIL);
                userService.save(user);
                sendMessage(chatId, "Great! What is your email address?");
                break;
            case ASK_EMAIL:
                user.setEmail(message);
                user.setRegistrationStep(RegistrationStep.ASK_PASSWORD);
                userService.save(user);
                sendMessage(chatId, "Please set a password.");
                break;
            case ASK_PASSWORD:
                user.setPassword(message);
                user.setRegistrationStep(RegistrationStep.COMPLETED);
                userService.save(user);
                sendMessage(chatId, "Registration complete! You can now place orders with /order.");
                break;
            default:
                sendMessage(chatId, "Sorry, something went wrong with the registration. Let's start over. What is your first name?");
                user.setRegistrationStep(RegistrationStep.ASK_NAME);
                userService.save(user);
                break;
        }
    }

    private void handleOrder(Long chatId, String message, User user) {
        // Check if the user is trying to start a new order
        if (user.getOrderStep() == OrderStep.INITIATE_ORDER || user.getOrderStep() == OrderStep.COMPLETED) {
            // Reset the order step to SELECT_PRODUCT to start a new order
            user.setOrderStep(OrderStep.SELECT_PRODUCT);
            userService.save(user);
        }

        List<Order> currentOrders = orderService.getCurrentOrderForUser (user);

        // If the user is trying to add a new order, we don't need to check for existing orders
        if (user.getOrderStep() == OrderStep.SELECT_PRODUCT) {
            SendMessageDTO response = getMakeChoiceMessage(chatId, user.getLanguage());
            telegramConfig.sendStructuredMessage(response);
            user.setOrderStep(OrderStep.SET_QUANTITY);
            userService.save(user);
            return;
        }

        // Existing logic for handling order steps
        if (!currentOrders.isEmpty()) {
            Order currentOrder = currentOrders.get(0);

            switch (user.getOrderStep()) {
                case SET_QUANTITY:
                    Product selectedProduct = productService.getProductByName(message);
                    if (selectedProduct == null) {
                        sendMessage(chatId, "Invalid product selection. Please choose a product from the list.");
                        return;
                    }

                    currentOrder.setProduct(selectedProduct);
                    orderService.save(currentOrder);

                    sendMessage(chatId, "Please enter the quantity for " + selectedProduct.getName() + ":");
                    user.setOrderStep(OrderStep.CONFIRM_ORDER);
                    userService.save(user);
                    break;

                case CONFIRM_ORDER:
                    try {
                        int quantity = Integer.parseInt(message);
                        if (quantity <= 0) {
                            sendMessage(chatId, "Please enter a valid quantity greater than 0.");
                            return;
                        }

                        currentOrder.setQuantity(quantity);
                        orderService.save(currentOrder);

                        // Provide order summary before confirmation
                        String summary = "Order Summary:\n" +
                                "Product: " + currentOrder.getProduct().getName() + "\n" +
                                "Quantity: " + quantity + "\n" +
                                "Total Price: $" + currentOrder.calculateTotalPrice() + "\n" +
                                "Type 'yes' to confirm or 'no' to cancel.";
                        sendMessage(chatId, summary);

                        user.setOrderStep(OrderStep.COMPLETED);
                        userService.save(user);
                    } catch (NumberFormatException e) {
                        sendMessage(chatId, "Please enter a valid number for quantity.");
                    }
                    break;

                case COMPLETED:
                    if (message.equalsIgnoreCase("yes")) {
                        currentOrder.setStatus(OrderStatus.PENDING);
                        orderService.save(currentOrder);
                        sendMessage(chatId, "Thank you! Your order has been placed.");
                    } else if (message.equalsIgnoreCase("no")) {
                        currentOrder.setStatus(OrderStatus.CANCELED);
                        orderService.save(currentOrder);
                        sendMessage(chatId, "Order canceled.");
                    } else {
                        sendMessage(chatId, "Please type 'yes' to confirm or 'no' to cancel.");
                    }
                    break;

                default:
                    sendMessage(chatId, "Something went wrong. Please type /order to start again.");
                    user.setOrderStep(OrderStep.INITIATE_ORDER);
                    userService.save(user);
                    break;
            }
        }

        if (user.getOrderStep() == OrderStep.DELETE_ORDER) {
            handleOrderDeletion(chatId, message, user);
            user.setOrderStep(OrderStep.COMPLETED);
            userService.save(user);
            return;
        }

    }




    public void handleDeleteOrderCommand(Long chatId, User user) {
        List<Order> activeOrders = orderService.getActiveOrdersByUser (user);

        if (activeOrders.isEmpty()) {
            sendMessage(chatId, "You have no active orders to delete.");
            return;
        }

        StringBuilder messageBuilder = new StringBuilder("Here are your active orders:\n");
        for (int i = 0; i < activeOrders.size(); i++) {
            Order order = activeOrders.get(i);
            Product product = order.getProduct();
            messageBuilder.append(i + 1).append(". ")
                    .append(product != null ? product.getName() : "Unknown")
                    .append(" (Quantity: ").append(order.getQuantity())
                    .append(", Total Price: $").append(order.calculateTotalPrice())
                    .append(")\n");
        }
        messageBuilder.append("Please enter the number of the order you want to delete:");

        sendMessage(chatId, messageBuilder.toString());
        user.setOrderStep(OrderStep.DELETE_ORDER); // Set the order step to DELETE_ORDER
        userService.save(user);
    }

    private void handleOrderDeletion(Long chatId, String message, User user) {
        List<Order> activeOrders = orderService.getActiveOrdersByUser (user);
        try {
            int orderIndex = Integer.parseInt(message) - 1; // Convert user input to index
            if (orderIndex < 0 || orderIndex >= activeOrders.size()) {
                sendMessage(chatId, "Invalid selection. Please enter a valid order number.");
                return;
            }

            Order orderToDelete = activeOrders.get(orderIndex);
            orderService.delete(orderToDelete.getId());
            sendMessage(chatId, "Your order for " + orderToDelete.getProduct().getName() + " has been deleted successfully.");
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Please enter a valid number.");
        }
    }

    private SendMessageDTO getMakeChoiceMessage(Long chatId, Language language) {


        if (chatId == null || language == null) {
            SendMessageDTO sendMessageDTO = new SendMessageDTO();
            sendMessageDTO.setChatId(chatId != null ? chatId : -1);
            sendMessageDTO.setText("Invalid chat ID or language.");
            return sendMessageDTO;
        }

        List<ProductResponseDTO> productList = productService.getAllProducts();
        if (productList.isEmpty()) {
            SendMessageDTO sendMessageDTO = new SendMessageDTO();
            sendMessageDTO.setChatId(chatId);
            sendMessageDTO.setText("No products available.");
            return sendMessageDTO;
        }

        int columnSize = 1;
        int rowCount = productList.size() / columnSize + (productList.size() % columnSize == 0 ? 0 : 1);

        KeyboardButtonDTO[][] buttons = new KeyboardButtonDTO[rowCount][];
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            int remainingItems = productList.size() - rowIndex * columnSize;
            int columns = Math.min(columnSize, remainingItems);

            buttons[rowIndex] = new KeyboardButtonDTO[columns];
            for (int columnIndex = 0; columnIndex < columns; columnIndex++) {
                ProductResponseDTO product = productList.get(rowIndex * columnSize + columnIndex);
                buttons[rowIndex][columnIndex] = new KeyboardButtonDTO(product.getName()); // Use product names
            }
        }

        ReplyKeyboardMarkupDTO replyKeyboardMarkupDTO = new ReplyKeyboardMarkupDTO();
        replyKeyboardMarkupDTO.setKeyboardButtonArray(buttons);
        replyKeyboardMarkupDTO.setOneTimeKeyboard(true);


        SendMessageDTO sendMessageDTO = new SendMessageDTO();
        sendMessageDTO.setChatId(chatId);
        sendMessageDTO.setText(messageProvider.getMessage("question_make_choice", language));
        sendMessageDTO.setReplyKeyboard(replyKeyboardMarkupDTO);

        return sendMessageDTO;
    }


    private boolean isUserEligibleForOrder(User user) {
        return user.getRegistrationStep() == RegistrationStep.COMPLETED
                && user.isActive();
    }

    public void handleOrderCalculation(Long chatId, Order order) {
        try {
            double totalPrice = order.calculateTotalPrice();
            sendMessage(chatId, "The total price for your order is: $" + totalPrice);
        } catch (IllegalStateException e) {
            sendMessage(chatId, "Unable to calculate the total price: " + e.getMessage());
        }
    }

    private void handleOrdersListCommand(Long chatId, User user) {
        List<Order> activeOrders = orderService.getActiveOrdersByUser(user);

        if (activeOrders.isEmpty()) {
            sendMessage(chatId, "You have no active orders.");
            return;
        }

        StringBuilder messageBuilder = new StringBuilder("Here are your active orders:\n");

        for (Order order : activeOrders) {
            Product product = order.getProduct();
            int quantity = order.getQuantity();
            double totalPrice = product != null ? product.getPrice() * quantity : 0;

            messageBuilder.append("- Product: ")
                    .append(product != null ? product.getName() : "Unknown")
                    .append("\n  Quantity: ").append(quantity)
                    .append("\n  Total Price: $").append(totalPrice)
                    .append("\n\n");
        }

        sendMessage(chatId, messageBuilder.toString());
    }


}


