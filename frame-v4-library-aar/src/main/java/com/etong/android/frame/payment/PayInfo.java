package com.etong.android.frame.payment;

public class PayInfo {
	int type=0;//0：微信支付1：支付宝2：财付通3：信用卡4：储蓄卡
	String orderId;//订单id
	String txnAmt;//金额
	String busId;//业务id
	String payBiz;//支付类型
	String accNo;//卡号
	String paycode;//支付编码
	String TradeName;//商品名称
	String TradeInfo;//商品信息
//	String spbill_create_ip;//微信支付app端ip
	
	/**获得支付类型  0：微信支付;1：支付宝;2：财付通;3：信用卡;4：储蓄卡*/
	public int getType() {
		return type;
	}
	/**设置支付类型  0：微信支付;1：支付宝;2：财付通;3：信用卡;4：储蓄卡*/
	public void setType(int type) {
		this.type = type;
	}

	/**获得订单id*/
	public String getOrderId() {
		return orderId;
	}
	/**设置订单id*/
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	/**获得金额*/
	public String getTxnAmt() {
		return txnAmt;
	}
	/**设置金额*/
	public void setTxnAmt(String txnAmt) {
		this.txnAmt = txnAmt;
	}
	/**获得业务id*/
	public String getBusId() {
		return busId;
	}
	/**设置业务id*/
	public void setBusId(String payId) {
		this.busId = payId;
	}
	/**获得支付类型(银联支付使用)  01:消费;02:预授权;03:预授权完成;31:消费撤消;32:预授权撤消33:预授权完成撤消*/
	public String getPayBiz() {
		return payBiz;
	}
	/**设置支付类型(银联支付使用)*/
	public void setPayBiz(String payBiz) {
		this.payBiz = payBiz;
	}
	/**获得卡号(银联支付使用)*/
	public String getAccNo() {
		return accNo;
	}
	/**设置卡号(银联支付使用)*/
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	/**获得支付编码*/
	public String getPaycode() {
		return paycode;
	}
	/**设置支付编码*/
	public void setPaycode(String paycode) {
		this.paycode = paycode;
	}
	/**获得商品名称(支付宝支付使用)*/
	public String getTradeName() {
		return TradeName;
	}
	/**设置商品名称(支付宝支付使用)*/
	public void setTradeName(String TradeName) {
		this.TradeName = TradeName;
	}
	/**获得商品信息(支付宝支付使用)*/
	public String getTradeInfo() {
		return TradeInfo;
	}
	/**设置商品信息(支付宝支付使用)*/
	public void setTradeInfo(String TradeInfo) {
		this.TradeInfo = TradeInfo;
	}
//	/**获得app端ip(微信支付使用)*/
//	public String getSpbill_create_ip() {
//		return spbill_create_ip;
//	}
//	/**设置app端ip(微信支付使用)*/
//	public void setSpbill_create_ip(String spbill_create_ip) {
//		this.spbill_create_ip = spbill_create_ip;
//	}
}
