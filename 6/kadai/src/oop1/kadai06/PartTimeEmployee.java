package oop1.kadai06;

public class PartTimeEmployee extends Employee {
	private double hoursWorked;

	public static final double INCOME_TAX_RATE_PARTTIME = 0.05;
	public static final double MIN_TAXABLE_GROSS_PAY_PARTTIME = 80000.0;

	public PartTimeEmployee(String employeeId, String name, double hourlyRate, double hoursWorked) {
		super(employeeId, name, hourlyRate);
		this.hoursWorked = hoursWorked;
	}

	@Override
	public double calculateGrossPay() {
		return basePay * hoursWorked;
	}

	@Override
	public double calculateTotalDeductions() {
		return calculateIncomeTax();
	}

	@Override
	public String getEmployeeTypeName() {
		return "アルバイト";
	}

	public double getHoursWorked() {
		return hoursWorked;
	}

	public double getHourlyRate() {
		return basePay;
	}

	public double calculateIncomeTax() {
		double grossPay = calculateGrossPay();
		return grossPay >= MIN_TAXABLE_GROSS_PAY_PARTTIME ?
			grossPay * INCOME_TAX_RATE_PARTTIME : 0.0;
	}
}