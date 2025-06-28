package com.varnix.PalmKioskBack.Repositories;

import com.varnix.PalmKioskBack.Entities.Category;
import com.varnix.PalmKioskBack.Entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findItemByName(String name);
    Optional<Item> findItemById(Long id);
    Optional<Item> findItemByCategory(Category category);
    Optional<Item> findItemByPrice(int price);

}
