package org.example.finaltgbot.service;

import lombok.RequiredArgsConstructor;
import org.example.finaltgbot.config.TelegramConfig;
import org.example.finaltgbot.dto.telegram.TelegramRoot;
import org.example.finaltgbot.dto.telegram.send.TelegramSendDTO;
import org.example.finaltgbot.entity.Order;
import org.example.finaltgbot.entity.Product;
import org.example.finaltgbot.entity.User;
import org.example.finaltgbot.enums.OrderStatus;
import org.example.finaltgbot.enums.OrderStep;
import org.example.finaltgbot.enums.RegistrationStep;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelegramBotService {

    private final TelegramConfig telegramConfig;
    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;

    private int offset = 0;

    @Scheduled(fixedRate = 3000)
    public void getMessage() {
        System.out.println("Offset : " + offset);

        TelegramRoot updates = telegramConfig.getUpdates(offset);

        if (!updates.getResult().isEmpty()) {
            updates.getResult().forEach(update -> {
                int chatId = update.getMessage().getChat().getId();
                String messageText = update.getMessage().getText();

                processMessage(chatId, messageText);

                offset = update.getUpdate_id() + 1;
            });
        }
    }

    public void sendMessage(int chatId, String text) {
        TelegramSendDTO message = new TelegramSendDTO();
        message.setChatId(chatId);
        message.setText(text);
        telegramConfig.sendMessage(chatId, text);
    }

//    public void processMessage(int chatId, String message) {
//        User user = userService.findByChatId(chatId);
//
//        if (user == null) {
//            user = userService.startRegistration(chatId);
//            sendMessage(chatId, "Welcome! Please register to place an order. What is your first name?");
//        } else if (user.getRegistrationStep() != RegistrationStep.COMPLETED) {
//            handleRegistrationSteps(chatId, message, user);
//
//        } else {
//            if (message.equalsIgnoreCase("/order")) {
//                user.setOrderStep(OrderStep.SELECT_PRODUCT);
//                userService.save(user);
//                sendMessage(chatId, "Please choose a product category");
//            }else if (message.equalsIgnoreCase("/delete_order")) {
//                handleDeleteOrderCommand(Integer.parseInt(message));
//                boolean isDeleted = orderService.deleteOrderByUserId(user.getId());
//                if (isDeleted) {
//                    sendMessage(chatId, "Your active order has been successfully deleted.");
//                } else {
//                    sendMessage(chatId, "No active orders found to delete.");
//                }
//            } else if (user.getOrderStep() != OrderStep.COMPLETED) {
//                handleOrder(chatId, message, user);
//            }
//        }
//
//    }

    public void processMessage(int chatId, String message) {
        User user = userService.findByChatId(chatId);

        if (user == null) {
            user = userService.startRegistration(chatId);
            sendMessage(chatId, "Welcome! Please register to place an order. What is your first name?");
        } else if (user.getRegistrationStep() != RegistrationStep.COMPLETED) {
            handleRegistrationSteps(chatId, message, user);
        } else {
            if (message.equalsIgnoreCase("/order")) {
                user.setOrderStep(OrderStep.SELECT_PRODUCT);
                userService.save(user);
                sendMessage(chatId, "Please choose a product category");
            } else if (message.equalsIgnoreCase("/delete_order")) {
                handleDeleteOrderCommand(chatId);
            } else if (user.getOrderStep() != OrderStep.COMPLETED) {
                handleOrder(chatId, message, user);
            }
        }
    }

    private void handleRegistrationSteps(int chatId, String message, User user) {
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

    private void handleOrder(int chatId, String message, User user) {
        Order currentOrder = orderService.getCurrentOrderForUser(user); // Fetch or create an order tied to the user

        switch (user.getOrderStep()) {
            case SELECT_PRODUCT:
                Product selectedProduct = productService.getProductById(Long.parseLong(message)); // Assuming the message is product ID
                currentOrder.setProduct(selectedProduct);
                orderService.save(currentOrder);

                user.setOrderStep(OrderStep.SET_QUANTITY);
                userService.save(user);

                sendMessage(chatId, "How many units would you like to order?");
                break;

            case SET_QUANTITY:
                try {
                    int quantity = Integer.parseInt(message);
                    currentOrder.setQuantity(quantity);
                    orderService.save(currentOrder);

                    user.setOrderStep(OrderStep.CONFIRM_ORDER);
                    userService.save(user);

                    sendMessage(chatId, "Confirm your order? Type 'yes' to confirm or 'no' to cancel.");
                } catch (NumberFormatException e) {
                    sendMessage(chatId, "Please enter a valid number for quantity.");
                }
                break;

            case CONFIRM_ORDER:
                if (message.equalsIgnoreCase("yes")) {
                    currentOrder.setStatus(OrderStatus.PENDING);
                    orderService.save(currentOrder);

                    user.setOrderStep(OrderStep.COMPLETED);
                    userService.save(user);

                    sendMessage(chatId, "Thank you! Your order has been placed.");
                } else if (message.equalsIgnoreCase("no")) {
                    currentOrder.setStatus(OrderStatus.CANCELED);
                    orderService.save(currentOrder);

                    user.setOrderStep(OrderStep.COMPLETED);
                    userService.save(user);

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

    public void handleDeleteOrderCommand(int chatId) {
        User user = userService.getUserByChatId(chatId);

        Optional<Order> optionalOrder = orderService.getCurrentOrderForUsers(user);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            Long id = order.getId();
            orderService.delete(id);
            sendMessage(chatId, "Your pending order has been deleted successfully.");
        } else {
            sendMessage(chatId, "You don't have any pending orders to delete.");
        }
    }



}
