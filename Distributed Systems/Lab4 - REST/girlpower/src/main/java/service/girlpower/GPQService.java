package service.girlpower;

import org.springframework.web.bind.annotation.*;
import service.core.AbstractQuotationService;
import service.core.ClientInfo;
import service.core.NoSuchQuotationException;
import service.core.Quotation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the Girl Power insurance quotation service.
 * 
 * @author Rem
 *
 */
@RestController
public class GPQService extends AbstractQuotationService {
	// All references are to be prefixed with an GP (e.g. GP001000)
	private static final String PREFIX = "GP";
	private static final String COMPANY = "Girl Power Inc.";
	private Map<String, Quotation> quotations = new HashMap<>();

	/** @param info
	 *  This POST method creates and returns a single Quotation for a given client's info **/
	@RequestMapping(value="/quotations",method= RequestMethod.POST)
	public Quotation createQuotation(@RequestBody ClientInfo info) {
		Quotation quotation = generateQuotation(info);
		quotations.put(quotation.getReference(), quotation);
		return quotation;
	}

	/** @param reference
	 * @exception NoSuchQuotationException
	 * This GET method returns a single quotation for a given reference number **/
	@RequestMapping(value="/quotations/{reference}",method=RequestMethod.GET)
	public Quotation getResource(@PathVariable("reference") String reference) {
		Quotation quotation = quotations.get(reference);
		if (quotation == null) throw new NoSuchQuotationException();
		return quotation;
	}

	/** This GET method returns an ArrayList of all quotations previously POSTed **/
	@RequestMapping(value="/quotations",method=RequestMethod.GET)
	public ArrayList<Quotation> listQuotations() {
		ArrayList<Quotation> list = new ArrayList<>();
		for(Quotation quotation: quotations.values()) list.add(quotation);
		return list;
	}

	/**
	 * Quote generation:
	 * 50% discount for being female
	 * 20% discount for no penalty points
	 * 15% discount for < 3 penalty points
	 * no discount for 3-5 penalty points
	 * 100% penalty for > 5 penalty points
	 * 5% discount per year no claims
	 */
	public Quotation generateQuotation(ClientInfo info) {
		// Create an initial quotation between 600 and 1000
		double price = generatePrice(600, 400);

		// Automatic 50% discount for being female
		int discount = (info.getGender() == ClientInfo.FEMALE) ? 50 : 0;

		// Add a points discount
		discount += getPointsDiscount(info);

		// Add a no claims discount
		discount += getNoClaimsDiscount(info);

		// Generate the quotation and send it back
		return new Quotation(COMPANY, generateReference(PREFIX), (price * (100 - discount)) / 100);
	}

	private int getNoClaimsDiscount(ClientInfo info) {
		return 5*info.getNoClaims();
	}

	private int getPointsDiscount(ClientInfo info) {
		int points = info.getPoints();
		if (points == 0) return 20;
		if (points < 3) return 15;
		if (points < 6) return 0;
		return -100;
		
	}
}
