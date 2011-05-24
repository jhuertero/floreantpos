/*
 * KeyStatisticsSalesReportView.java
 *
 * Created on March 10, 2007, 2:53 AM
 */

package com.floreantpos.ui.report;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JRViewer;

import com.floreantpos.main.Application;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.UserType;
import com.floreantpos.model.dao.SalesSummaryDAO;
import com.floreantpos.model.dao.TerminalDAO;
import com.floreantpos.model.dao.UserTypeDAO;
import com.floreantpos.report.SalesAnalysisReportModel;
import com.floreantpos.report.SalesStatistics;
import com.floreantpos.report.SalesAnalysisReportModel.SalesAnalysisData;
import com.floreantpos.report.SalesStatistics.ShiftwiseDataTableModel;
import com.floreantpos.swing.ListComboBoxModel;
import com.floreantpos.ui.dialog.POSMessageDialog;

/**
 *
 * @author  mshahriar
 */
public class SalesSummaryReportView extends javax.swing.JPanel {
	public static final int REPORT_KEY_STATISTICS = 1;
	public static final int REPORT_SALES_ANALYSIS = 2;

	private int reportType;

	/** Creates new form KeyStatisticsSalesReportView */
	public SalesSummaryReportView() {
		initComponents();

		UserTypeDAO dao = new UserTypeDAO();
		List<UserType> userTypes = dao.findAll();
		
		Vector list = new Vector();
		list.add(null);
		list.addAll(userTypes);
		
		cbUserType.setModel(new DefaultComboBoxModel(list));

		TerminalDAO terminalDAO = new TerminalDAO();
		List terminals = terminalDAO.findAll();
		terminals.add(0, com.floreantpos.POSConstants.ALL);
		cbTerminal.setModel(new ListComboBoxModel(terminals));
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		fromDatePicker = new org.jdesktop.swingx.JXDatePicker();
		toDatePicker = new org.jdesktop.swingx.JXDatePicker();
		cbUserType = new javax.swing.JComboBox();
		cbTerminal = new javax.swing.JComboBox();
		btnGo = new javax.swing.JButton();
		jSeparator1 = new javax.swing.JSeparator();
		reportPanel = new javax.swing.JPanel();

		jLabel1.setText(com.floreantpos.POSConstants.FROM + ":");

		jLabel2.setText(com.floreantpos.POSConstants.TO + ":");

		jLabel3.setText(com.floreantpos.POSConstants.USER_TYPE + ":");

		jLabel4.setText(com.floreantpos.POSConstants.TERMINAL + ":");

		btnGo.setText(com.floreantpos.POSConstants.GO);
		btnGo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showReport(evt);
			}
		});

		reportPanel.setLayout(new java.awt.BorderLayout());

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().addContainerGap().add(
						layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE).add(
								layout.createSequentialGroup().add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel1).add(jLabel2)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
										layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false).add(toDatePicker, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(fromDatePicker, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).add(20, 20, 20).add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel3).add(jLabel4)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
										layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false).add(cbTerminal, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(cbUserType, 0, 137, Short.MAX_VALUE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(btnGo,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(reportPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().addContainerGap().add(
						layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel3).add(
								layout.createSequentialGroup().add(cbUserType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
										layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel4).add(cbTerminal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(btnGo))).add(
								layout.createSequentialGroup().add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel1).add(fromDatePicker, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
												layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel2).add(toDatePicker, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(reportPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 303,
						Short.MAX_VALUE).addContainerGap()));

		layout.linkSize(new java.awt.Component[] { cbTerminal, cbUserType, jLabel3, jLabel4 }, org.jdesktop.layout.GroupLayout.VERTICAL);

		layout.linkSize(new java.awt.Component[] { fromDatePicker, jLabel1, jLabel2, toDatePicker }, org.jdesktop.layout.GroupLayout.VERTICAL);

	}// </editor-fold>//GEN-END:initComponents

	private boolean initCriteria() {
		fromDate = fromDatePicker.getDate();
		toDate = toDatePicker.getDate();

		if (fromDate.after(toDate)) {
			POSMessageDialog.showError(Application.getInstance().getBackOfficeWindow(), com.floreantpos.POSConstants.FROM_DATE_CANNOT_BE_GREATER_THAN_TO_DATE_);
			return false;
		}

		dateDiff = (int) ((toDate.getTime() - fromDate.getTime()) * (1.15740741 * Math.pow(10, -8))) + 1;
		userType = (UserType) cbUserType.getSelectedItem();
//		if (userType.equalsIgnoreCase(com.floreantpos.POSConstants.ALL)) {
//			userType = null;
//		}
		terminal = null;
		if (cbTerminal.getSelectedItem() instanceof Terminal) {
			terminal = (Terminal) cbTerminal.getSelectedItem();
		}

		Calendar calendar = Calendar.getInstance();
		calendar.clear();

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(fromDate);

		calendar.set(Calendar.YEAR, calendar2.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, calendar2.get(Calendar.MONTH));
		calendar.set(Calendar.DATE, calendar2.get(Calendar.DATE));
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		fromDate = calendar.getTime();

		calendar.clear();
		calendar2.setTime(toDate);
		calendar.set(Calendar.YEAR, calendar2.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, calendar2.get(Calendar.MONTH));
		calendar.set(Calendar.DATE, calendar2.get(Calendar.DATE));
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		toDate = calendar.getTime();

		return true;
	}

	private void showReport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showReport
		try {
			if (!initCriteria()) {
				return;
			}

			if (reportType == REPORT_KEY_STATISTICS) {
				showKeyStatisticsReport();
			}
			else if (reportType == REPORT_SALES_ANALYSIS) {
				showSalesAnalysisReport();
			}
		} catch (Exception e) {
			POSMessageDialog.showError(this, com.floreantpos.POSConstants.ERROR_MESSAGE, e);
		}
	}//GEN-LAST:event_showReport

	private void showSalesAnalysisReport() throws Exception {
		SalesSummaryDAO dao = new SalesSummaryDAO();
		List<SalesAnalysisData> datas = dao.findSalesAnalysis(fromDate, toDate, userType, terminal);

		Map properties = new HashMap();
		ReportUtil.populateRestaurantProperties(properties);
		properties.put("subtitle", com.floreantpos.POSConstants.SALES_SUMMARY_REPORT);
		properties.put("reportTime", fullDateFormatter.format(new Date()));
		properties.put("fromDate", shortDateFormatter.format(fromDate));
		properties.put("toDate", shortDateFormatter.format(toDate));
		if (userType == null) {
			properties.put("reportType", com.floreantpos.POSConstants.SYSTEM_TOTAL);
		}
		else {
			properties.put("reportType", userType);
		}
		properties.put("shift", com.floreantpos.POSConstants.ALL);
		properties.put("centre", terminal == null ? com.floreantpos.POSConstants.ALL : terminal.getName());
		properties.put("days", String.valueOf(dateDiff));

		JasperReport report = (JasperReport) JRLoader.loadObject(getClass().getResource("/com/floreantpos/ui/report/sales_summary_report2.jasper"));
		JasperPrint print = JasperFillManager.fillReport(report, properties, new JRTableModelDataSource(new SalesAnalysisReportModel(datas)));
		openReport(print);
	}

	private void showKeyStatisticsReport() throws Exception {
		SalesSummaryDAO dao = new SalesSummaryDAO();
		SalesStatistics summary = dao.findKeyStatistics(fromDate, toDate, userType, terminal);

		Map properties = new HashMap();
		ReportUtil.populateRestaurantProperties(properties);
		properties.put("subtitle", com.floreantpos.POSConstants.SALES_SUMMARY_REPORT);
		properties.put("Capacity", String.valueOf(summary.getCapacity()));
		properties.put("GuestCount", String.valueOf(summary.getGuestCount()));
		properties.put("GuestPerSeat", Application.formatNumber(summary.getGuestPerSeat()));
		properties.put("reportTime", fullDateFormatter.format(new Date()));
		properties.put("fromDate", shortDateFormatter.format(fromDate));
		properties.put("toDate", shortDateFormatter.format(toDate));
		if (userType == null) {
			properties.put("reportType", com.floreantpos.POSConstants.SYSTEM_TOTAL);
		}
		else {
			properties.put("reportType", userType.getName());
		}
		properties.put("shift", com.floreantpos.POSConstants.ALL);
		properties.put("centre", terminal == null ? com.floreantpos.POSConstants.ALL : terminal.getName());
		properties.put("days", String.valueOf(dateDiff));

		properties.put("Capacity", String.valueOf(summary.getCapacity()));
		properties.put("GuestCount", String.valueOf(summary.getGuestCount()));
		properties.put("GuestPerSeat", Application.formatNumber(summary.getGuestPerCheck()));
		properties.put("TableTrnOvr", Application.formatNumber(summary.getTableTurnOver()));
		properties.put("AVGGuest", Application.formatNumber(summary.getAvgGuest()));
		properties.put("OpenChecks", String.valueOf(summary.getOpenChecks()));
		properties.put("VOIDChecks", String.valueOf(summary.getVoidChecks()));
		properties.put("OPPDChecks", String.valueOf(" "));
		properties.put("TRNGChecks", String.valueOf(" "));
		properties.put("ROPNChecks", String.valueOf(summary.getRopnChecks()));
		properties.put("MergeChecks", String.valueOf(" "));
		properties.put("LaborHour", Application.formatNumber(summary.getLaborHour()));
		properties.put("LaborSales", Application.formatNumber(summary.getGrossSale()));
		properties.put("Tables", String.valueOf(summary.getTables()));
		properties.put("CheckCount", String.valueOf(summary.getCheckCount()));
		properties.put("GuestPerChecks", Application.formatNumber(summary.getGuestPerCheck()));
		properties.put("TrnOvrTime", String.valueOf(" "));
		properties.put("AVGChecks", Application.formatNumber(summary.getAvgCheck()));
		properties.put("OPENAmount", Application.formatNumber(summary.getOpenAmount()));
		properties.put("VOIDAmount", Application.formatNumber(summary.getVoidAmount()));
		properties.put("PAIDChecks", String.valueOf(summary.getPaidChecks()));
		properties.put("TRNGAmount", String.valueOf(" "));
		properties.put("ROPNAmount", Application.formatNumber(summary.getRopnAmount()));
		properties.put("NTaxChecks", String.valueOf(summary.getNtaxChecks()));
		properties.put("NTaxAmount", Application.formatNumber(summary.getNtaxAmount()));
		properties.put("MergeAmount", String.valueOf(" "));
		properties.put("Labor", Application.formatNumber(summary.getLaborCost()));
		properties.put("LaborCost", Application.formatNumber((summary.getLaborCost() / summary.getGrossSale()) * 100));

		JasperReport report = (JasperReport) JRLoader.loadObject(getClass().getResource("/com/floreantpos/ui/report/sales_summary_report1.jasper"));
		JasperPrint print = JasperFillManager.fillReport(report, properties, new JRTableModelDataSource(new ShiftwiseDataTableModel(summary.getSalesTableDataList())));
		openReport(print);

	}

	private void openReport(JasperPrint print) {
		JRViewer viewer = new JRViewer(print);
		reportPanel.removeAll();
		reportPanel.add(viewer);
		reportPanel.revalidate();
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnGo;
	private javax.swing.JComboBox cbTerminal;
	private javax.swing.JComboBox cbUserType;
	private org.jdesktop.swingx.JXDatePicker fromDatePicker;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JPanel reportPanel;
	private org.jdesktop.swingx.JXDatePicker toDatePicker;
	// End of variables declaration//GEN-END:variables
	private SimpleDateFormat fullDateFormatter = new SimpleDateFormat("yyyy MMM dd, hh:mm a");
	private SimpleDateFormat shortDateFormatter = new SimpleDateFormat("yyyy MMM dd");
	
	private Date fromDate;
	private Date toDate;
	private int dateDiff;
	private UserType userType;
	private Terminal terminal;

	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

}
