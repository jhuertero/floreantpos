package com.floreantpos.ui.views.payment;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.JOptionPane;

import net.authorize.data.creditcard.CardType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.floreantpos.POSConstants;
import com.floreantpos.PosException;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.config.CardConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.CardReader;
import com.floreantpos.model.CashTransaction;
import com.floreantpos.model.CouponAndDiscount;
import com.floreantpos.model.CreditCardTransaction;
import com.floreantpos.model.Customer;
import com.floreantpos.model.Gratuity;
import com.floreantpos.model.MerchantGateway;
import com.floreantpos.model.PaymentType;
import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketCouponAndDiscount;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.report.JReportPrintService;
import com.floreantpos.services.PosTransactionService;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.ui.dialog.CouponAndDiscountDialog;
import com.floreantpos.ui.dialog.DiscountListDialog;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.dialog.PaymentTypeSelectionDialog;
import com.floreantpos.ui.dialog.TransactionCompletionDialog;
import com.floreantpos.ui.views.SwitchboardView;
import com.floreantpos.ui.views.TicketDetailView;
import com.floreantpos.ui.views.order.RootView;
import com.floreantpos.util.POSUtil;

public class SettleTicketView extends POSDialog implements CardInputListener {
	public final static String VIEW_NAME = "PAYMENT_VIEW";

	private String previousViewName = SwitchboardView.VIEW_NAME;

	private com.floreantpos.swing.TransparentPanel leftPanel = new com.floreantpos.swing.TransparentPanel(new BorderLayout());
	private com.floreantpos.swing.TransparentPanel rightPanel = new com.floreantpos.swing.TransparentPanel(new BorderLayout());

	private TicketDetailView ticketDetailView;
	private PaymentView paymentView;
	protected List<Ticket> ticketsToSettle;

	private double tenderedAmount;

	private String cardName;

	public SettleTicketView() {
		super(Application.getPosWindow(), true);
		setTitle("Settle ticket");

		getContentPane().setLayout(new BorderLayout(5, 5));

		ticketDetailView = new TicketDetailView();
		paymentView = new PaymentView(this);

		leftPanel.add(ticketDetailView);
		rightPanel.add(paymentView);

		getContentPane().add(leftPanel, BorderLayout.CENTER);
		getContentPane().add(rightPanel, BorderLayout.EAST);
	}

	public void setCurrentTicket(Ticket currentTicket) {
		ticketsToSettle = new ArrayList<Ticket>();
		ticketsToSettle.add(currentTicket);

		ticketDetailView.setTickets(getTicketsToSettle());
		paymentView.updateView();
	}

	private void updateModel() {
		List<Ticket> ticketsToSettle = getTicketsToSettle();

		for (Ticket ticket : ticketsToSettle) {
			ticket.calculatePrice();
		}
	}

	public void doApplyCoupon() {// GEN-FIRST:event_btnApplyCoupondoApplyCoupon
		try {
			List<Ticket> tickets = getTicketsToSettle();

			for (Ticket ticket : tickets) {
				if (ticket.getCouponAndDiscounts() != null && ticket.getCouponAndDiscounts().size() > 0) {
					POSMessageDialog.showError(com.floreantpos.POSConstants.DISCOUNT_COUPON_LIMIT_);
					return;
				}
			}

			Ticket ticket = tickets.get(0);
			CouponAndDiscountDialog dialog = new CouponAndDiscountDialog();
			dialog.setTicket(ticket);
			dialog.initData();
			dialog.open();
			if (!dialog.isCanceled()) {
				TicketCouponAndDiscount coupon = dialog.getSelectedCoupon();
				ticket.addTocouponAndDiscounts(coupon);

				updateModel();

				TicketDAO.getInstance().saveOrUpdate(ticket);
				ticketDetailView.updateView();
				paymentView.updateView();
			}
		} catch (Exception e) {
			POSMessageDialog.showError(this, com.floreantpos.POSConstants.ERROR_MESSAGE, e);
		}
	}// GEN-LAST:event_btnApplyCoupondoApplyCoupon

	public void doTaxExempt(boolean taxExempt) {// GEN-FIRST:event_doTaxExempt
		List<Ticket> ticketsToSettle = getTicketsToSettle();

		boolean setTaxExempt = taxExempt;
		if (setTaxExempt) {
			int option = JOptionPane.showOptionDialog(this, com.floreantpos.POSConstants.CONFIRM_SET_TAX_EXEMPT, com.floreantpos.POSConstants.CONFIRM,
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (option != JOptionPane.YES_OPTION) {
				return;
			}

			for (Ticket ticket : ticketsToSettle) {
				ticket.setTaxExempt(true);
				ticket.calculatePrice();
				TicketDAO.getInstance().saveOrUpdate(ticket);
			}
		}
		else {
			for (Ticket ticket : ticketsToSettle) {
				ticket.setTaxExempt(false);
				ticket.calculatePrice();
				TicketDAO.getInstance().saveOrUpdate(ticket);
			}
		}

		ticketDetailView.updateView();
		paymentView.updateView();
	}// GEN-LAST:event_doTaxExempt

	public void doSetGratuity() {
		GratuityInputDialog d = new GratuityInputDialog();
		d.setSize(300, 500);
		d.setResizable(false);
		d.open();

		if (d.isCanceled()) {
			return;
		}

		double gratuityAmount = d.getGratuityAmount();
		Gratuity gratuity = new Gratuity();
		gratuity.setAmount(gratuityAmount);

		List<Ticket> tickets = getTicketsToSettle();
		Ticket ticket = tickets.get(0);

		ticket.setGratuity(gratuity);
		ticket.calculatePrice();
		TicketDAO.getInstance().saveOrUpdate(ticket);

		ticketDetailView.updateView();
		paymentView.updateView();
	}

	protected double getTotalAmount() {
		List<Ticket> ticketsToSettle = getTicketsToSettle();
		if (ticketsToSettle == null) {
			return 0;
		}

		double total = 0;
		for (Ticket ticket : ticketsToSettle) {
			total += ticket.getTotalAmount();
		}
		return total;
	}

	public void doViewDiscounts() {// GEN-FIRST:event_btnViewDiscountsdoViewDiscounts
		try {
			List<Ticket> tickets = getTicketsToSettle();

			DiscountListDialog dialog = new DiscountListDialog(tickets);
			dialog.open();

			if (!dialog.isCanceled() && dialog.isModified()) {
				updateModel();

				for (Ticket ticket : tickets) {
					TicketDAO.getInstance().saveOrUpdate(ticket);
				}

				ticketDetailView.updateView();
				paymentView.updateView();
			}
		} catch (Exception e) {
			POSMessageDialog.showError(this, com.floreantpos.POSConstants.ERROR_MESSAGE, e);
		}
	}// GEN-LAST:event_btnViewDiscountsdoViewDiscounts

	public void doSettle() {
		try {

			PaymentTypeSelectionDialog dialog = new PaymentTypeSelectionDialog();
			dialog.setResizable(false);
			dialog.pack();
			dialog.open();
			if (dialog.isCanceled()) {
				return;
			}

			PaymentType paymentType = dialog.getSelectedPaymentType();
			cardName = paymentType.getDisplayString();

			tenderedAmount = paymentView.getTenderedAmount();

			switch (paymentType) {
				case CASH:
					ConfirmPayDialog confirmPayDialog = new ConfirmPayDialog();
					confirmPayDialog.setAmount(tenderedAmount);
					confirmPayDialog.open();

					if (confirmPayDialog.isCanceled()) {
						return;
					}

					if (settleTickets(tenderedAmount, new CashTransaction(), null, null)) {
						setCanceled(false);
						dispose();
					}
					break;

				case CREDIT_VISA:
				case CREDIT_MASTER_CARD:
				case CREDIT_AMEX:
				case CREDIT_DISCOVERY:
					payUsingCard(cardName, tenderedAmount);
					break;

				case DEBIT_VISA:
				case DEBIT_MASTER_CARD:
					payUsingCard(cardName, tenderedAmount);
					break;

				default:
					break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean settleTickets(final double tenderedAmount, PosTransaction posTransaction, String cardType, String cardAuthorizationCode) {
		try {
			setTenderAmount(tenderedAmount);

			double totalAmount = getTotalAmount();
			double dueAmountBeforePaid = paymentView.getDueAmount();

			List<Ticket> ticketsToSettle = getTicketsToSettle();

			if (ticketsToSettle.size() > 1 && tenderedAmount < dueAmountBeforePaid) {
				MessageDialog.showError(com.floreantpos.POSConstants.YOU_CANNOT_PARTIALLY_PAY_MULTIPLE_TICKETS_);
				return false;
			}

			for (Ticket ticket : ticketsToSettle) {
				ticket.setTenderedAmount(tenderedAmount);

				submitMyKalaDiscount(ticket);

				try {
					if (ticket.needsKitchenPrint()) {
						JReportPrintService.printTicketToKitchen(ticket);
					}

					JReportPrintService.printTicket(ticket);
				} catch (Exception ee) {
					POSMessageDialog.showError(Application.getPosWindow(), com.floreantpos.POSConstants.PRINT_ERROR, ee);
				}
			}

			PosTransactionService transactionService = PosTransactionService.getInstance();
			transactionService.settleTickets(ticketsToSettle, tenderedAmount, posTransaction, cardType, cardAuthorizationCode);

			double paidAmount = paymentView.getPaidAmount();
			double dueAmount = paymentView.getDueAmount();

			TransactionCompletionDialog dialog = TransactionCompletionDialog.getInstance();
			dialog.setTickets(ticketsToSettle);
			dialog.setTenderedAmount(tenderedAmount);
			dialog.setTotalAmount(totalAmount);
			dialog.setPaidAmount(paidAmount);
			dialog.setDueAmount(dueAmount);
			dialog.setDueAmountBeforePaid(dueAmountBeforePaid);
			// dialog.setGratuityAmount(gratuityAmount);
			dialog.updateView();
			dialog.pack();
			dialog.open();

			if (dueAmount > 0.0) {
				int option = JOptionPane.showConfirmDialog(Application.getPosWindow(), com.floreantpos.POSConstants.CONFIRM_PARTIAL_PAYMENT,
						com.floreantpos.POSConstants.MDS_POS, JOptionPane.YES_NO_OPTION);
				if (option != JOptionPane.YES_OPTION) {
					RootView.getInstance().showView(SwitchboardView.VIEW_NAME);
					return true;
				}

				setTicketsToSettle(ticketsToSettle);
				return false;
			}
			else {
				return true;
			}
		} catch (Exception e) {
			POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
			return false;
		}
	}

	private void submitMyKalaDiscount(Ticket ticket) throws IOException, MalformedURLException {
		if (ticket.isPropertyValueTrue("mykaladiscount")) {
			String transactionURL = "http://cloud.floreantpos.org/triliant/api_user_transaction.php?";
			transactionURL += "kala_id=" + ticket.getProperty("mykalaid");
			transactionURL += "&trans_id=" + ticket.getId();
			transactionURL += "&product_name=" + URLEncoder.encode(ticket.getTicketItems().get(0).getName(), "utf-8");
			transactionURL += "&product_price=" + ticket.getSubtotalAmount();
			transactionURL += "&product_category=Food";
			transactionURL += "&revenue_center=cash";
			transactionURL += "&store_name=Floreant";
			transactionURL += "&store_zip=17225";
			transactionURL += "&store_id=" + Application.getInstance().getRestaurant().getUniqueId();

			String string = IOUtils.toString(new URL(transactionURL).openStream());
			System.out.println(transactionURL);
			System.out.println(string);

			if (string.contains("\"success\":false")) {
				POSMessageDialog.showError("Coupon already used.");
			}
		}
	}

	private void payUsingCard(String cardName, final double tenderedAmount) throws Exception {
		if (!CardConfig.getMerchantGateway().isCardTypeSupported(cardName)) {
			POSMessageDialog.showError("<html>Card <b>" + cardName + "</b> not supported.</html>");
			return;
		}

		CardReader cardReader = CardConfig.getCardReader();
		switch (cardReader) {
			case SWIPE:
				SwipeCardDialog swipeCardDialog = new SwipeCardDialog(this);
				swipeCardDialog.pack();
				swipeCardDialog.open();
				break;

			case MANUAL:
				ManualCardEntryDialog dialog = new ManualCardEntryDialog(this);
				dialog.pack();
				dialog.open();
				break;

			case EXTERNAL_TERMINAL:
				AuthorizationCodeDialog authorizationCodeDialog = new AuthorizationCodeDialog(this);
				authorizationCodeDialog.pack();
				authorizationCodeDialog.open();
				break;

			default:
				break;
		}

	}

	private void setTenderAmount(double tenderedAmount) {
		List<Ticket> ticketsToSettle = getTicketsToSettle();
		if (ticketsToSettle == null) {
			return;
		}

		for (Ticket ticket : ticketsToSettle) {
			ticket.setTenderedAmount(tenderedAmount);
		}
	}

	public void updatePaymentView() {
		paymentView.updateView();
	}

	public String getPreviousViewName() {
		return previousViewName;
	}

	public void setPreviousViewName(String previousViewName) {
		this.previousViewName = previousViewName;
	}

	public List<Ticket> getTicketsToSettle() {
		return ticketsToSettle;
	}

	public void setTicketsToSettle(List<Ticket> ticketsToSettle) {
		this.ticketsToSettle = ticketsToSettle;

		ticketDetailView.setTickets(getTicketsToSettle());
		paymentView.updateView();
	}

	public TicketDetailView getTicketDetailView() {
		return ticketDetailView;
	}

	@Override
	public void open() {
		super.open();
	}

	@Override
	public void cardInputted(CardInputter inputter) {
		try {
			CardType authorizeNetCardType = CardType.findByValue(cardName);

			if (inputter instanceof SwipeCardDialog) {
				SwipeCardDialog swipeCardDialog = (SwipeCardDialog) inputter;
				String cardString = swipeCardDialog.getCardString();

				if (StringUtils.isEmpty(cardString) || cardString.length() < 16) {
					throw new RuntimeException("Invalid card string");
				}

				ConfirmPayDialog confirmPayDialog = new ConfirmPayDialog();
				confirmPayDialog.setAmount(tenderedAmount);
				confirmPayDialog.open();

				if (confirmPayDialog.isCanceled()) {
					return;
				}

				if (CardConfig.getMerchantGateway() == MerchantGateway.AUTHORIZE_NET) {
					String authorizationCode = AuthorizeDoNetProcessor.process(cardString, tenderedAmount, authorizeNetCardType);
					settleTickets(tenderedAmount, new CreditCardTransaction(), cardName, authorizationCode);
				}

				setCanceled(false);
				dispose();
			}
			else if (inputter instanceof ManualCardEntryDialog) {
				ManualCardEntryDialog mDialog = (ManualCardEntryDialog) inputter;
				String cardNumber = mDialog.getCardNumber();
				String expMonth = mDialog.getExpMonth();
				String expYear = mDialog.getExpYear();

				String authorizationCode = AuthorizeDoNetProcessor.process(cardNumber, expMonth, expYear, tenderedAmount, authorizeNetCardType);
				POSMessageDialog.showMessage(authorizationCode);
				settleTickets(tenderedAmount, new CreditCardTransaction(), cardName, authorizationCode);
			}
			else if (inputter instanceof AuthorizationCodeDialog) {
				AuthorizationCodeDialog authDialog = (AuthorizationCodeDialog) inputter;
				String authorizationCode = authDialog.getAuthorizationCode();
				if (StringUtils.isEmpty(authorizationCode)) {
					throw new PosException("Invalid authorization code");
				}

				settleTickets(tenderedAmount, new CreditCardTransaction(), null, authorizationCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
			POSMessageDialog.showError(e.getMessage());
		}
	}

	public boolean hasMyKalaId() {
		Ticket ticket = getTicketsToSettle().get(0);

		Customer customer = ticket.getCustomer();
		if (customer != null && customer.hasProperty("mykalaid")) {
			return true;
		}

		return false;
	}

	public void makeMyKalaDiscount() {
		try {
			String mykalaid = null;

			Ticket ticket = getTicketsToSettle().get(0);

			boolean mykaladiscountPaid = POSUtil.getBoolean(ticket.getProperty("mykaladiscount"));
			if (mykaladiscountPaid) {
				POSMessageDialog.showError("Kala user already added $" + ticket.getDiscountAmount() + " discount");
				return;
			}

			Customer customer = ticket.getCustomer();
			if (customer != null && customer.hasProperty("mykalaid")) {
				mykalaid = customer.getProperty("mykalaid");
			}
			else {
				mykalaid = JOptionPane.showInputDialog("Enter mykala id:");
			}

			if (StringUtils.isEmpty(mykalaid)) {
				return;
			}

			String getUserInfoURL = "http://cloud.floreantpos.org/triliant/api_user_detail.php?kala_id=" + mykalaid;

			JsonReader reader = Json.createReader(new URL(getUserInfoURL).openStream());
			JsonObject object = reader.readObject();

			boolean success = Boolean.valueOf(object.get("success").toString());
			if (success) {
				String message = object.getString("message").toString();
				String point = object.getString("points");
				String couponno = object.getString("coupon");

				message += "\n" + "You have earned " + point + " points";
				message += "\n" + "Your coupon number is " + couponno;

				int option = JOptionPane.showOptionDialog(Application.getPosWindow(), message, "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
						null, new String[] { "REDEEM", "CANCEL" }, "REDEEM");

				if (option != JOptionPane.OK_OPTION) {
					return;
				}

				String offer = object.getString("offer");
				String offerString = offer.replaceAll("%", "");
				double offerPercentage = Double.parseDouble(offerString);

				TicketCouponAndDiscount coupon = new TicketCouponAndDiscount();
				coupon.setName("mykala_offer_" + object.getString("kala_id"));
				coupon.setType(CouponAndDiscount.PERCENTAGE_PER_ORDER);
				coupon.setValue(offerPercentage * 100.0);

				ticket.addTocouponAndDiscounts(coupon);
				ticket.addProperty("mykalaid", mykalaid);
				ticket.addProperty("mykaladiscount", "true");
				ticket.addProperty("mykalacoupon", couponno);
				ticket.addProperty("mykalapoint", point);
				ticket.addProperty("mykaladiscountpercentage", offer);

				updateModel();

				TicketDAO.getInstance().saveOrUpdate(ticket);
				ticketDetailView.updateView();
				paymentView.updateView();
			}

			else {
				POSMessageDialog.showError(BackOfficeWindow.getInstance(), object.getString("message").toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			POSMessageDialog.showError(BackOfficeWindow.getInstance(), e.getMessage());
		}
	}

}
