package de.bstreit.java.oscr.business.products.category.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.bstreit.java.oscr.business.products.category.ProductCategory;

public interface IProductCategoryRepository extends
		JpaRepository<ProductCategory, Long> {

	@Query("FROM ProductCategory WHERE validTo IS NULL")
	public List<ProductCategory> findActiveProductCategories();

	public ProductCategory findByName(String name);

}
