package org.gustas.inventory.inventorymanagementsystem.domain.item.integrity;

import org.gustas.inventory.inventorymanagementsystem.domain.item.entity.Item;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ItemIntegrity {

    public void checkItemNotNull(Item item) {
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
    }

}
