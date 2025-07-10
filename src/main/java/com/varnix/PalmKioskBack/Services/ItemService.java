package com.varnix.PalmKioskBack.Services;

import com.varnix.PalmKioskBack.Entities.Item;
import com.varnix.PalmKioskBack.Repositories.CategoryRepository;
import com.varnix.PalmKioskBack.Repositories.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService{

    private final ItemRepository itemRepository;


    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public boolean existsById(Long id) {
        return itemRepository.existsById(id);
    }
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    public Optional<Item> findById(long id) {
        return itemRepository.findById(id);
    }

    public void save(Item item) {
        itemRepository.save(item);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public void deleteById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item with id " + id + " not found"));
        itemRepository.delete(item);
    }


}
