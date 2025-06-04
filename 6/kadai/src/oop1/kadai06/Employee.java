package oop1.kadai06;

abstract class Employee {
	protected String employeeId;
	protected String name;
	protected double basePay;

	public Employee(String employeeId, String name, double basePay) {
		this.employeeId = employeeId;
		this.name = name;
		this.basePay = basePay;
	}

	public String getEmployeeId() {
		return employeeId;
	} 
	public String getName() {
		return name;
	}
	public double getBasePay() {
		return basePay;
	}

	double calculateNetPay() {
		return calculateGrossPay() - calculateTotalDeductions();
	}

	public abstract double calculateGrossPay(); // 総支給額（各種手当込み、控除前）を計算
	public abstract double calculateTotalDeductions(); // 控除額合計を計算
	public abstract String getEmployeeTypeName(); // 従業員の種類を取得

}
