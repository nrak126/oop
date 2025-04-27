public class Receipt {
	private ProductItem[] items;
	
	public Receipt() {
			// 商品情報を保持する配列を初期化
			items = new ProductItem[0];
	}

	public void addProduct(ProductItem item) {
			// 商品を追加する処理
			ProductItem[] newItems = new ProductItem[items.length + 1];
			System.arraycopy(items, 0, newItems, 0, items.length);
			newItems[items.length] = item;
			items = newItems;
	}

	public double getTotalPrice() {
		double totalPrice = 0;
		for(ProductItem item : items) {
			totalPrice += item.getSubtotal();
		}
		return totalPrice;
	}

	public int getTotalQuantity(){
		int totalQuantity = 0;
		for(ProductItem item : items) {
			totalQuantity += item.quantity;
		}
		return totalQuantity;
	}
}