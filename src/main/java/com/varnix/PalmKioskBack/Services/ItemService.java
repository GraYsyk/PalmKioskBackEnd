package com.varnix.PalmKioskBack.Services;

import com.varnix.PalmKioskBack.Entities.Item;
import com.varnix.PalmKioskBack.Repositories.CategoryRepository;
import com.varnix.PalmKioskBack.Repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService{

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
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

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

}
