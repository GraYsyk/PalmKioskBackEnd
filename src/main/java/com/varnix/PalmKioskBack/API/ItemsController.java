package com.varnix.PalmKioskBack.API;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varnix.PalmKioskBack.Dtos.ItemDTO;
import com.varnix.PalmKioskBack.Entities.Category;
import com.varnix.PalmKioskBack.Entities.Item;
import com.varnix.PalmKioskBack.Exceptions.AppError;
import com.varnix.PalmKioskBack.Services.CategoryService;
import com.varnix.PalmKioskBack.Services.ItemService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
public class ItemsController {

    private final ItemService itemService;
    private final CategoryService categoryService;

    private final String uploadDir = "uploads/";

    public ItemsController(ItemService itemService,
                           CategoryService categoryService) {
        this.itemService = itemService;
        this.categoryService = categoryService;
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<?> getItem(@PathVariable Long id) {
        Optional<Item> itemOpt = itemService.findById(id);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AppError(HttpStatus.NOT_FOUND.value(), "Item not found"));
        }
        Item item = itemOpt.get();

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(item.getId());
        itemDTO.setName(item.getName());
        itemDTO.setDescription(item.getDescription());
        itemDTO.setPrice(item.getPrice());
        itemDTO.setCategory(item.getCategory().getName());
        itemDTO.setImage(item.getImageUrl());

        return ResponseEntity.ok(itemDTO);
    }

    @GetMapping("/allItems")
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        if (items.isEmpty()) return ResponseEntity.noContent().build();

        List<ItemDTO> itemDTOs = items.stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(itemDTOs);
    }

    @PostMapping(value = "/saveItem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createItem(
            @RequestParam("item") String itemJson,
            @RequestPart("image") MultipartFile imageFile) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ItemDTO itemDTO = mapper.readValue(itemJson, ItemDTO.class);

        System.out.println(">>> createItem reached");

        // Проверка файла
        if (imageFile == null || imageFile.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Image is empty!"));
        }
        // Проверка данных itemDTO
        if (itemDTO == null) {
            return ResponseEntity.badRequest()
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Item data is missing!"));
        }
        if (itemDTO.getName() == null || itemDTO.getName().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Item name is required!"));
        }
        if (itemDTO.getPrice() <= 0) {
            return ResponseEntity.badRequest()
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Price must be positive!"));
        }
        if (itemDTO.getCategory() == null || itemDTO.getCategory().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Category name is required!"));
        }

        // Создаем папку для загрузки, если не существует
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists() && !uploadDirFile.mkdirs()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Could not create upload directory"));
        }

        // Чистим имя файла и берем расширение
        String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
        String fileExtension = "";

        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            fileExtension = originalFilename.substring(dotIndex);
        }

        // Создаем уникальное имя файла
        String fileName = System.currentTimeMillis() + fileExtension;
        Path filePath = Paths.get(uploadDir, fileName);

        // Сохраняем файл
        Files.copy(imageFile.getInputStream(), filePath);

        // Ищем категорию
        Optional<Category> category = categoryService.findByName(itemDTO.getCategory());
        if (category.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Category not found"));
        }

        // Создаем и сохраняем Item
        Item item = new Item();
        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setPrice(itemDTO.getPrice());
        item.setCategory(category.get());

        // Формируем URL картинки
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String imageUrl = baseUrl + "/uploads/" + fileName;
        item.setImageUrl(imageUrl);

        itemService.save(item);

        // Формируем DTO для ответа
        ItemDTO responseDto = new ItemDTO();
        responseDto.setId(item.getId());
        responseDto.setName(item.getName());
        responseDto.setDescription(item.getDescription());
        responseDto.setPrice(item.getPrice());
        responseDto.setCategory(item.getCategory().getName());
        responseDto.setImage(item.getImageUrl());

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping(value = "/item/upd/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateItem(
            @PathVariable Long id,
            @RequestParam("item") String itemJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ItemDTO itemDTO = mapper.readValue(itemJson, ItemDTO.class);

        Optional<Item> itemOpt = itemService.findById(id);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AppError(HttpStatus.NOT_FOUND.value(), "Item not found"));
        }
        Item item = itemOpt.get();

        // Обновляем поля из DTO
        if (itemDTO.getName() != null && !itemDTO.getName().isBlank()) {
            item.setName(itemDTO.getName());
        }
        item.setDescription(itemDTO.getDescription());
        if (itemDTO.getPrice() > 0) {
            item.setPrice(itemDTO.getPrice());
        }

        // Обновляем категорию
        if (itemDTO.getCategory() != null && !itemDTO.getCategory().isBlank()) {
            Optional<Category> categoryOpt = categoryService.findByName(itemDTO.getCategory());
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Category not found"));
            }
            item.setCategory(categoryOpt.get());
        }

        // Обработка новой картинки, если она есть
        if (imageFile != null && !imageFile.isEmpty()) {
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists() && !uploadDirFile.mkdirs()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Could not create upload directory"));
            }

            String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String fileExtension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                fileExtension = originalFilename.substring(dotIndex);
            }

            String fileName = System.currentTimeMillis() + fileExtension;
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(imageFile.getInputStream(), filePath);

            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String imageUrl = baseUrl + "/uploads/" + fileName;
            item.setImageUrl(imageUrl);
        }

        itemService.save(item);

        ItemDTO responseDto = new ItemDTO();
        responseDto.setId(item.getId());
        responseDto.setName(item.getName());
        responseDto.setDescription(item.getDescription());
        responseDto.setPrice(item.getPrice());
        responseDto.setCategory(item.getCategory().getName());
        responseDto.setImage(item.getImageUrl());

        return ResponseEntity.ok(responseDto);
    }


    @DeleteMapping("/item/delete/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        boolean exists = itemService.existsById(id);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AppError(HttpStatus.NOT_FOUND.value(), "Item not found"));
        }
        itemService.deleteById(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }



    private ItemDTO convertToDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setImage(item.getImageUrl());
        dto.setPrice(item.getPrice());
        dto.setCategory(item.getCategory().getName()); // если категория может быть null — добавить проверку
        return dto;
    }

}
