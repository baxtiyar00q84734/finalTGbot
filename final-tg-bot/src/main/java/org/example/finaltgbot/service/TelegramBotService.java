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
import java.util.Optional;

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

                offset =(update.getUpdate_id() + 1);
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
            System.out.println("User not found, starting registration...");
            userService.startRegistration(chatId);
            sendMessage(chatId, "Welcome! Please register to place an order. What is your first name?");
            return;
        }

        if (user.getRegistrationStep() != RegistrationStep.COMPLETED) {
            System.out.println("User registration is incomplete, handling registration steps...");
            handleRegistrationSteps(chatId, message, user);
        } else {
            if (message.equalsIgnoreCase("/order")) {
                if (isUserEligibleForOrder(user)) {
                    user.setOrderStep(OrderStep.SELECT_PRODUCT);
                    userService.save(user);

                    handleOrder(chatId, message, user);
                } else {
                    sendMessage(chatId, "You are not eligible to place an order at this moment.");
                }

            } else if (message.equalsIgnoreCase("/delete_order")) {
                handleDeleteOrderCommand(chatId);
            } else if (user.getOrderStep() != OrderStep.COMPLETED) {
                handleOrder(chatId, message, user);
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
        Order currentOrder = orderService.getCurrentOrderForUser(user);

        switch (user.getOrderStep()) {
            case SELECT_PRODUCT:
                SendMessageDTO response = getMakeChoiceMessage((long) chatId, user.getLanguage());
                telegramConfig.sendStructuredMessage(response);

                Product selectedProduct = productService.getProductByName(message);
                currentOrder.setProduct(selectedProduct);
                orderService.save(currentOrder);


                user.setOrderStep(OrderStep.SET_QUANTITY);
                userService.save(user);


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

    public void handleDeleteOrderCommand(Long chatId) {
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


}


