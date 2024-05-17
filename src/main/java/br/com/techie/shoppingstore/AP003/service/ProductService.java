package br.com.techie.shoppingstore.AP003.service;

import br.com.techie.shoppingstore.AP003.dto.form.ProductFORM;
import br.com.techie.shoppingstore.AP003.dto.form.ProductUpdateFORM;
import br.com.techie.shoppingstore.AP003.dto.view.ProductVIEW;
import br.com.techie.shoppingstore.AP003.mapper.forms.ProductFormMapper;
import br.com.techie.shoppingstore.AP003.mapper.updates.ProductUpdateMapper;
import br.com.techie.shoppingstore.AP003.mapper.views.ProductViewMapper;
import br.com.techie.shoppingstore.AP003.mapper.forms.ServerAttributeFormMapper;
import br.com.techie.shoppingstore.AP003.model.Product;
import br.com.techie.shoppingstore.AP003.model.ServerAttribute;
import br.com.techie.shoppingstore.AP003.repository.CategoryRepository;
import br.com.techie.shoppingstore.AP003.repository.ProductRepository;
import br.com.techie.shoppingstore.AP003.repository.ServerAttributeRepository;
import br.com.techie.shoppingstore.AP003.service.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class ProductService {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ServerAttributeRepository serverAttributeRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ProductFormMapper productFormMapper;

  @Autowired
  private ProductViewMapper productViewMapper;

  @Autowired
  private ProductUpdateMapper productUpdateMapper;

  @Autowired
  private ServerAttributeFormMapper serverAttributeFormMapper;

  @Transactional(readOnly = true)
  public Page<ProductVIEW> findAllPaged(Pageable pageable) {
    return productRepository.findAll(pageable).map(x -> productViewMapper.map(x));
  }

  @Transactional(readOnly = true)
  public ProductVIEW findById(Long id) {
    Optional<Product> obj = productRepository.findById(id);
    Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    ServerAttribute attributes =  serverAttributeRepository.findByProductId(id).orElseThrow(() -> new EntityNotFoundException("Attributes not found"));
    return productViewMapper.map(entity, attributes);
  }

  @Transactional
  public ProductVIEW insert(ProductFORM dto) {
    Product entity = productFormMapper.map(dto);
    entity.setCategory(categoryRepository.findById(dto.category_id()).orElseThrow(() -> new EntityNotFoundException("Category not found!")));
    ServerAttribute attributes = serverAttributeFormMapper.map(dto, entity);
    productRepository.save(entity);
    serverAttributeRepository.save(attributes);
    return productViewMapper.map(entity, attributes);
  }

  @Transactional
  public ProductVIEW update(ProductUpdateFORM dto) {
    Product entity = productRepository.findById(dto.product_id()).orElseThrow(() -> new EntityNotFoundException("Product not found!"));
    entity.setCategory(categoryRepository.findById(dto.category_id()).orElseThrow(() -> new EntityNotFoundException("Category not found!")));
    entity = productUpdateMapper.map(dto, entity);
    productRepository.save(entity);


    return productViewMapper.map(entity);
  }

  public void delete(Long id) {
    productRepository.deleteById(id);
  }
}