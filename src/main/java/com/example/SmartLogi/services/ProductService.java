package com.example.SmartLogi.services;

import com.example.SmartLogi.dto.DeactivateProductDTO;
import com.example.SmartLogi.dto.ProductRequestDTO;
import com.example.SmartLogi.dto.ProductResponseDTO;
import com.example.SmartLogi.entities.Category;
import com.example.SmartLogi.entities.Inventory;
import com.example.SmartLogi.entities.Product;
import com.example.SmartLogi.entities.SalesOrderLine;
import com.example.SmartLogi.enums.OrderLineStatus;
import com.example.SmartLogi.exception.BusinessException;
import com.example.SmartLogi.exception.ResourceNotFoundException;
import com.example.SmartLogi.mapper.ProductMapper;
import com.example.SmartLogi.repositories.CategoryRepository;
import com.example.SmartLogi.repositories.InventoryRepository;
import com.example.SmartLogi.repositories.ProductRepository;
import com.example.SmartLogi.repositories.SalesOrderLineRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class ProductService {

     @Autowired
    private ProductRepository repository;

     @Autowired
     private CategoryRepository categoryRepository;

     @Autowired
     private SalesOrderLineRepository salesOrderLineRepository;

     @Autowired
     private InventoryRepository inventoryRepository;

     @Autowired
    private   ProductMapper mapper;

    public ProductResponseDTO createProduct(ProductRequestDTO dto){
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = Product.builder()
                .sku(dto.sku())
                .name(dto.name())
                .category(category)
                .purchasePrice(dto.purchasePrice())
                .sellingPrice(dto.sellingPrice())
                .active(dto.active())
                .createdAt(LocalDateTime.now())
                .build();

        return mapper.toDTO(repository.save(product));
    }

    public List<ProductResponseDTO> getAllProducts(){
        return  repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ProductResponseDTO getProductById(Long id) {
        Optional<Product> product = repository.findById(id);
        return product.map(mapper::toDTO).orElse(null);
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        Product product = repository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));


        product.setName(dto.name());
        product.setSku(dto.sku());
        product.setPurchasePrice(dto.purchasePrice());
        product.setSellingPrice(dto.sellingPrice());
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);
        product.setActive(dto.active());
        return mapper.toDTO(repository.save(product));
    }


    public boolean deleteProduct(Long id) {
            if (!repository.existsById(id)) return false;
            repository.deleteById(id);
            return true;

    }


    public  ProductResponseDTO deactivate(DeactivateProductDTO dto) {

        Product product = repository.findBySkuAndActive(dto.getSku(), true).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        List<SalesOrderLine> lines = salesOrderLineRepository.findAllByProductSku(product.getSku());

        lines.forEach(line -> {
            if(line.getProduct().getId().equals(product.getId()) && line.getStatus().equals(OrderLineStatus.RESERVED)
                    ||line.getStatus().equals(OrderLineStatus.PARTIALLY_RESERVED)
                    ||line.getStatus().equals(OrderLineStatus.CREATED)
            ) {
                throw new BusinessException("Product is already reserved ");
            }
        });

        List<Inventory> inventories = inventoryRepository.findByProductSku(dto.getSku());

        inventories.forEach(inventory -> {
            if(inventory.getQuantityReserved() > 0 || inventory.getQuantityOnHand() >0){
                throw new BusinessException("Product is still in inventory ");
            }
        });

        product.setActive(false);
        return mapper.toDTO(repository.save(product));
    }





}
