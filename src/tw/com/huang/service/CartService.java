package tw.com.huang.service;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;
import tw.com.huang.dao.DBConn;
import tw.com.huang.dao.SQLPrepareProcess;

@Getter
@Setter
public class CartService {

	private List<String[]> cart;// 購物車
	private List<String> price;// 各項產品單價
	private int count = 0;// 總價
	private int shipping = 4000;// 運費門檻
	private int shippingCost = 160;// 運費
	private DBConn dbc;
	private SQLPrepareProcess sq;
	private String[] dbconnParam = new String[4];

	private String pid = "";// 產品ID
	private int x = 0;// 已存在的產品編號
	private boolean Exist = false;
	
	public CartService(HttpServletRequest request) {
		String rootpath = request.getServletContext().getRealPath("/") + "/";
		String path = "setting/";
		String dbcfile = "dbconn_param.txt";
		File f = new File(rootpath + path + dbcfile);
		Scanner sc;
		try {
			sc = new Scanner(f);
			int l = 0;
			while (sc.hasNextLine()) {
				this.dbconnParam[l] = sc.nextLine().toString();
				l = l + 1;
			}
			sc.close();
			this.dbc = new DBConn(this.dbconnParam);
			this.sq = new SQLPrepareProcess(dbc.getConnection());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 更新購物車
	public void updateCart(List<String[]> cart, String quant) {
		for (int i = 0; i < cart.size(); i++) {
			String[] product = (String[]) cart.get(i);
			int updatecount = Integer.parseInt(product[2]) * Integer.parseInt(quant);
			String updatecountstr = String.valueOf(updatecount);
			price.set(i, updatecountstr);
		}
		count = 0;
		for (int i = 0; i < price.size(); i++) {
			int p = Integer.parseInt(price.get(i).toString());
			count = count + p;
		}
		// 低於4000加上運費160
		if (count < shipping) {
			count = count + shippingCost;
		}
	}

//	// 新增產品
//	public void addCart(List<String[]> cart, String quant) {
//		for (int i = 0; i < cart.size(); i++) {
//			String[] product = (String[]) cart.get(i);
//			if (product[0].equals(pid)) {
//				x = i;
//				Exist = true;
//				break;
//			}
//		}
//		if (!Exist) {// 如果購物車中不存在，則加入
//			sq.selectDataFromTable("product", "product_id", pid);
//			List<String[]> add = sq.getQueryRow();
//			cart.add((String[]) add.get(0));
//			String[] product = (String[]) add.get(0);
//			String subprice = product[2];
//			if (request.getParameter("quantity") != null) {
//				int q = Integer.parseInt(request.getParameter("quantity"));
//				int subtotal = q * Integer.parseInt(subprice);
//				subprice = String.valueOf(subtotal);
//				price.add(subprice);
//				count = count + subtotal;
//			} else {
//				price.add(subprice);
//				count = count + Integer.parseInt(subprice);
//			}
//			// 低於4000加上運費160
//			if (count < 4000) {
//				count = count + 160;
//			}
//			sq.closeSQLStatement();
//			dbc.closeConnection();
//		} else {
//			if (request.getParameter("quantity") != null) {
//				String[] product = (String[]) cart.get(x);
//				String quant = request.getParameter("quantity");
//				int updateprice = Integer.parseInt(product[2]) * Integer.parseInt(quant);
//				String updatepricestr = String.valueOf(updateprice);
//				int updatecount = count - Integer.parseInt(price.get(x).toString()) + updateprice;
//				price.set(x, updatepricestr);
//				// 低於4000加上運費160
//				if (updatecount < 4000) {
//					updatecount = updatecount + 160;
//				}
//			}
//			sq.closeSQLStatement();
//			dbc.closeConnection();
//		}
//	}

}
