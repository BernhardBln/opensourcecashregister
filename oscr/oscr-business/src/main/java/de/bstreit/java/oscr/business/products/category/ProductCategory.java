package de.bstreit.java.oscr.business.products.category;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import de.bstreit.java.oscr.business.base.persistence.AbstractPersistentObjectWithContinuance;

/**
 * Product categories (drinks, food, ...) help to visualise the buttons in the
 * cash register front-end better, and also provide additional information for
 * controlling.
 * <p>
 * Can be optional so we don't force people to define categories.
 * <p>
 * We should not allow people to delete categories, as there might be archived
 * products assigned to them (and I guess we don't want to loose the info).
 * <b>Better:</b> Hide archived category in UI (unless assigned to sth that is
 * displayed)
 * 
 */
@Entity
public class ProductCategory extends
    AbstractPersistentObjectWithContinuance<ProductCategory> {

  /**
   * The name of the category.
   * 
   * By setting unique to true, we do not allow two categories with the same
   * name to exist in different periods of time. The purpose of that is simply
   * to use the continuance feature only to hide categories, but not to have
   * them exist parallel in time.
   */
  @Column(unique = true)
  private String name;

  /** Colour for this category (used in UI) */
  @Column(length = 7)
  private String colour;

  @Column(nullable = false, columnDefinition = "SMALLINT DEFAULT 0")
  private int orderNumber = 0;


  @SuppressWarnings("unused")
  private ProductCategory() {
    // for spring
  }

  public ProductCategory(String name) {
    super(new Date(), null);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String getColour() {
    return colour;
  }

  public void setColour(String colour) {
    colour = colour.trim().toLowerCase();

    if (!colour.startsWith("#")) {
      colour = "#" + colour;
    }

    this.colour = colour;
  }


  public void setOrderNumber(int orderNumber) {
    this.orderNumber = orderNumber;
  }


  public int getOrderNumber() {
    return orderNumber;
  }

  @Override
  protected void additionalEqualsForSubclasses(EqualsBuilder equalsBuilder,
      ProductCategory otherObject) {

    equalsBuilder.append(name, otherObject.name);
  }

  @Override
  protected void additionalHashcodeForSubclasses(HashCodeBuilder builder) {
    builder.append(name);
  }

  @Override
  public String toString() {
    return name;
  }

}
