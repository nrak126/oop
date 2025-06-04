package oop1.kadai06;

public class FullTimeEmployee extends Employee implements CommuteAllowanceCalculable {
	private double overtimeHours; // 残業時間
private double bonus; // 賞与
private double commuteAllowance; // 交通費

	public static final double STANDARD_MONTHLY_HOURS = 160.0; // 月の平均所定労働時間
	public static final double OVERTIME_RATE_MULTIPLIER = 1.25; // 残業手当の割増率
	public static final double SOCIAL_INSURANCE_RATE = 0.15; // 社会保険料率、基本給に対する割合
	public static final double INCOME_TAX_RATE_FULLTIME = 0.10; // 所得税率、総支給額に対する割合

	public FullTimeEmployee(String employeeId, String name, double basePay, double overtimeHours, double bonus, double commuteAllowance) {
		super(employeeId, name, basePay);
		this.overtimeHours = overtimeHours;
		this.bonus = bonus;
		this.commuteAllowance = commuteAllowance;
	}

	@Override
	public double calculateGrossPay() {
		return basePay + calculateOvertimePay() + bonus + commuteAllowance;
	}

	@Override
	public double calculateTotalDeductions() {
		return calculateSocialInsurance() + calculateIncomeTax();
	}

	@Override
	public String getEmployeeTypeName() {
		return "正社員";
	}

	public double getCommuteAllowance() {
		return commuteAllowance;
	}

	public double calculateOvertimePay() {
			return STANDARD_MONTHLY_HOURS > 0 ? 
			(basePay / STANDARD_MONTHLY_HOURS) * OVERTIME_RATE_MULTIPLIER * overtimeHours
			: 0;
	}

	public double getBonus() {
		return bonus;
	}

	public double calculateSocialInsurance() {
		return basePay * SOCIAL_INSURANCE_RATE;
	}

	public double calculateIncomeTax() {
		return calculateGrossPay() * INCOME_TAX_RATE_FULLTIME;
	}

	public double getOvertimeHours() {
		return overtimeHours;
	}
}
