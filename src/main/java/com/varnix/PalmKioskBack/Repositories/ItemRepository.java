package com.varnix.PalmKioskBack.Repositories;

import com.varnix.PalmKioskBack.Entities.Category;
import com.varnix.PalmKioskBack.Entities.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {
    Optional<Item> findItemByName(String name);
    Optional<Item> findItemById(Long id);
    Optional<Item> findItemByCategory(Category category);
    Optional<Item> findItemByPrice(int price);
}
