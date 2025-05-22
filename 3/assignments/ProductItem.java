// 商品情報を保持するクラス
public class ProductItem {
	public String productName;
	public double unitPrice;
	public int quantity;

	// コンストラクタ
	public ProductItem(String productName, double unitPrice, int quantity) {
			this.productName = productName;
			this.unitPrice = unitPrice;
			this.quantity = quantity;
	}

	// 小計を返すメソッド
	public double getSubtotal() {
			return unitPrice * quantity;
	}

	// レシート風に商品情報を返すメソッド
	public String toString() {
			return String.format("商品名: %s, 単価: %.2f, 数量: %d, 小計: %.0f",
							productName, unitPrice, quantity, getSubtotal());
	}
}