package de.bstreit.java.oscr.gui.swing.cashregister.ui.factories;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JButton;

import org.apache.commons.lang3.StringUtils;

import de.bstreit.java.oscr.business.offers.AbstractOffer;
import de.bstreit.java.oscr.business.offers.ExtraOffer;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.products.Product;
import de.bstreit.java.oscr.gui.swing.cashregister.ui.MainWindowController;

@Named
public class ButtonFactory {

	@Inject
	private MainWindowController appController;

	public JButton createButtonFor(AbstractOffer<?> offer) {

		if (offer instanceof ProductOffer) {
			return createProductOfferButton((ProductOffer) offer);
		} else if (offer instanceof VariationOffer) {
			return createVariationOfferButton((VariationOffer) offer);
		} else if (offer instanceof ExtraOffer) {
			return createExtraOfferButton((ExtraOffer) offer);
		}

		throw new OfferClassNotImplementedException();
	}

	private JButton createProductOfferButton(final ProductOffer productOffer) {

		final JButton button = new JButton(productOffer.getLabel());
		setDefaults(button);

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				appController.addToBill(productOffer);
			}
		});

		setColourIfNotEmpty(button, productOffer);

		return button;
	}

	private void setDefaults(JButton button) {
		button.setMinimumSize(new Dimension(0, 40));
		button.setInheritsPopupMenu(true);
	}

	private void setColourIfNotEmpty(final JButton button,
			final ProductOffer productOffer) {

		final Product product = productOffer.getOfferedItem();

		if (product.getProductCategory() != null) {

			final String colourAsString = product.getProductCategory()
					.getColour();

			if (StringUtils.isNotBlank(colourAsString)) {
				final Color colour = Color.decode(colourAsString);
				button.setBackground(colour);
			}
		}

	}

	private JButton createVariationOfferButton(final VariationOffer offer) {

		final JButton button = new JButton(offer.getLabel());
		setDefaults(button);

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				appController.setVariationOffer(offer);
			}
		});

		return button;
	}

	private JButton createExtraOfferButton(final ExtraOffer offer) {

		final JButton button = new JButton(offer.getLabel());
		setDefaults(button);

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				appController.addExtraOffer(offer);
			}
		});

		return button;
	}

}
