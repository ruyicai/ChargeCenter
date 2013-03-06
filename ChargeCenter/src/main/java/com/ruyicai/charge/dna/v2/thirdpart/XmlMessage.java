package com.ruyicai.charge.dna.v2.thirdpart;

import java.io.Serializable;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/** 银联语音支付接口参数封装类，参数说明请参照<<银联语音支付平台接口规范>>文档。
 * @author Administrator
 *
 */
public class XmlMessage extends PosMessage implements Serializable {

    private static final long serialVersionUID = -7784871050544876882L;
    private String mCustName = "";

    /**
     * @return
     */
    public String getCustName() {
        return mCustName;
    }

    /**
     * @param value
     */
    public void setCustName(String value) {
        mCustName = value;
    }
    private String mCustId = "";

    /**
     * @return
     */
    public String getCustId() {
        return mCustId;
    }

    /**
     * @param value
     */
    public void setCustId(String value) {
        mCustId = value;
    }
    private String mBankName = "";

    /**
     * @return
     */
    public String getBankName() {
        return mBankName;
    }

    /**
     * @param value
     */
    public void setBankName(String value) {
        mBankName = value;
    }
    private String mCustAddress = "";

    /**
     * @return
     */
    public String getCustAddress() {
        return mCustAddress;
    }

    /**
     * @param value
     */
    public void setCustAddress(String value) {
        mCustAddress = value;
    }
    private String mBankAddress = "";

    /**
     * @return
     */
    public String getBankAddress() {
        return mBankAddress;
    }

    /**
     * @param value
     */
    public void setBankAddress(String value) {
        mBankAddress = value;
    }
    private String mBeneficiary = "";

    /**
     * @return
     */
    public String getBeneficiary() {
        return mBeneficiary;
    }

    /**
     * @param value
     */
    public void setBeneficiary(String value) {
        mBeneficiary = value;
    }
    private String mCustIp = "";

    /**
     * @return
     */
    public String getCustIp() {
        return mCustIp;
    }

    /**
     * @param value
     */
    public void setCustIp(String value) {
        mCustIp = value;
    }
    private String mBindCode = "";

    /**
     * @return
     */
    public String getBindCode() {
        return mBindCode;
    }

    /**
     * @param value
     */
    public void setBindCode(String value) {
        mBindCode = value;
    }
    private String mCustPhoto = "";

    /**
     * @return
     */
    public String getCustPhoto() {
        return mCustPhoto;
    }

    /**
     * @param value
     */
    public void setCustPhoto(String value) {
        mCustPhoto = value;
    }
    private String mCustCert = "";

    /**
     * @return
     */
    public String getCustCert() {
        return mCustCert;
    }

    /**
     * @param value
     */
    public void setCustCert(String value) {
        mCustCert = value;
    }

    public XmlMessage() {
    }

    public XmlMessage(String xml) throws Exception {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            doc = db.parse(new InputSource(new StringReader(xml)));
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setProcCode(TransactionUtil.getElementValue("ProcCode", doc));
        this.setAccountNum(TransactionUtil.getElementValue("AccountNum", doc));
        this.setProcessCode(TransactionUtil.getElementValue("ProcessCode", doc));
        this.setAmount(TransactionUtil.getElementValue("Amount", doc));
        this.setCurCode(TransactionUtil.getElementValue("CurCode", doc));
        this.setTransDatetime(TransactionUtil.getElementValue("TransDatetime", doc));
        this.setAcqSsn(TransactionUtil.getElementValue("AcqSsn", doc));
        this.setLtime(TransactionUtil.getElementValue("Ltime", doc));
        this.setLdate(TransactionUtil.getElementValue("Ldate", doc));
        this.setSettleDate(TransactionUtil.getElementValue("SettleDate", doc));
        this.setUpsNo(TransactionUtil.getElementValue("UpsNo", doc));
        this.setTsNo(TransactionUtil.getElementValue("TsNo", doc));
        this.setReference(TransactionUtil.getElementValue("Reference", doc));
        this.setReturnAddress(TransactionUtil.getElementValue("ReturnAddress", doc));
        this.setRespCode(TransactionUtil.getElementValue("RespCode", doc));
        this.setRemark(TransactionUtil.getElementValue("Remark", doc));
        this.setTerminalNo(TransactionUtil.getElementValue("TerminalNo", doc));
        this.setMerchantNo(TransactionUtil.getElementValue("MerchantNo", doc));
        this.setOrderNo(TransactionUtil.getElementValue("OrderNo", doc));
        this.setOrderState(TransactionUtil.getElementValue("OrderState", doc));
        this.setDescription(TransactionUtil.getElementValue("Description", doc));
        this.setValidTime(TransactionUtil.getElementValue("ValidTime", doc));
        this.setOrderType(TransactionUtil.getElementValue("OrderType", doc));
        this.setCustName(TransactionUtil.getElementValue("CustName", doc));
        this.setCustId(TransactionUtil.getElementValue("CustId", doc));
        this.setBankName(TransactionUtil.getElementValue("BankName", doc));
        this.setCustAddress(TransactionUtil.getElementValue("CustAddress", doc));
        this.setBankAddress(TransactionUtil.getElementValue("BankAddress", doc));
        this.setBeneficiary(TransactionUtil.getElementValue("Beneficiary", doc));
        this.setCustIp(TransactionUtil.getElementValue("CustIp", doc));
        this.setBindCode(TransactionUtil.getElementValue("BindCode", doc));
        this.setCustPhoto(TransactionUtil.getElementValue("CustPhoto", doc));
        this.setCustCert(TransactionUtil.getElementValue("CustCert", doc));
        this.setTransData(TransactionUtil.getElementValue("TransDate", doc));
        this.setPin(TransactionUtil.getElementValue("Pin", doc));
        this.setLoginPin(TransactionUtil.getElementValue("LoginPin", doc));
        this.setMac(TransactionUtil.getElementValue("Mac", doc));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public XmlMessage clone() {
        XmlMessage pm = new XmlMessage();
        pm.setProcCode(this.getProcCode());
        pm.setAccountNum(this.getAccountNum());
        pm.setProcessCode(this.getProcessCode());
        pm.setAmount(this.getAmount());
        pm.setCurCode(this.getCurCode());
        pm.setTransDatetime(this.getTransDatetime());
        pm.setAcqSsn(this.getAcqSsn());
        pm.setLtime(this.getLtime());
        pm.setLdate(this.getLdate());
        pm.setSettleDate(this.getSettleDate());
        pm.setUpsNo(this.getUpsNo());
        pm.setTsNo(this.getTsNo());
        pm.setReference(this.getReference());
        pm.setReturnAddress(this.getReturnAddress());
        pm.setRespCode(this.getRespCode());
        pm.setRemark(this.getRemark());
        pm.setTerminalNo(this.getTerminalNo());
        pm.setMerchantNo(this.getMerchantNo().trim());
        pm.setOrderNo(this.getOrderNo());
        pm.setOrderState(this.getOrderState());
        pm.setDescription(this.getDescription());
        pm.setValidTime(this.getValidTime());
        pm.setOrderType(this.getOrderType());
        pm.setCustName(this.getCustName());
        pm.setCustId(this.getCustId());
        pm.setBankName(this.getBankName());
        pm.setCustAddress(this.getCustAddress());
        pm.setBankAddress(this.getBankAddress());
        pm.setBeneficiary(this.getBeneficiary());
        pm.setCustIp(this.getCustIp());
        pm.setBindCode(this.getBindCode());
        pm.setCustPhoto(this.getCustPhoto());
        pm.setCustCert(this.getCustCert());
        pm.setTransData(this.getTransData());
        pm.setPin(this.getPin());
        pm.setLoginPin(this.getLoginPin());
        pm.setMac(this.getMac());

        return pm;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            return pce.getMessage();
        }

        doc = db.newDocument();
        Element root = doc.createElement("x:NetworkRequest");
        root.setAttribute("xmlns:x", "http://www.dna-pay.com");
        root.setAttribute("xmlns:xsi", "http://www.w3.org");
        doc.appendChild(root);

        if (this.getProcCode() != null) {
            Element fProcCode = doc.createElement("ProcCode");
            fProcCode.appendChild(doc.createTextNode(this.getProcCode()));
            root.appendChild(fProcCode);
        }

        if (this.getAccountNum() != null) {
            Element fAccountNum = doc.createElement("AccountNum");
            fAccountNum.appendChild(doc.createTextNode(this.getAccountNum()));
            root.appendChild(fAccountNum);
        }


        if (this.getProcessCode() != null) {
            Element fProcessCode = doc.createElement("ProcessCode");
            fProcessCode.appendChild(doc.createTextNode(this.getProcessCode()));
            root.appendChild(fProcessCode);
        }

        if (this.getAmount() != null) {
            Element fAmount = doc.createElement("Amount");
            fAmount.appendChild(doc.createTextNode(this.getAmount()));
            root.appendChild(fAmount);
        }

        if (this.getCurCode() != null) {
            Element fCurCode = doc.createElement("CurCode");
            fCurCode.appendChild(doc.createTextNode(this.getCurCode()));
            root.appendChild(fCurCode);
        }

        if (this.getTransDatetime() != null) {
            Element fTransDatetime = doc.createElement("TransDatetime");
            fTransDatetime.appendChild(doc.createTextNode(this.getTransDatetime()));
            root.appendChild(fTransDatetime);
        }

        if (this.getAcqSsn() != null) {
            Element fAcqSsn = doc.createElement("AcqSsn");
            fAcqSsn.appendChild(doc.createTextNode(this.getAcqSsn()));
            root.appendChild(fAcqSsn);
        }

        if (this.getLtime() != null) {
            Element fLtime = doc.createElement("Ltime");
            fLtime.appendChild(doc.createTextNode(this.getLtime()));
            root.appendChild(fLtime);
        }

        if (this.getLdate() != null) {
            Element fLdate = doc.createElement("Ldate");
            fLdate.appendChild(doc.createTextNode(this.getLdate()));
            root.appendChild(fLdate);
        }

        if (this.getSettleDate() != null) {
            Element fSettleDate = doc.createElement("SettleDate");
            fSettleDate.appendChild(doc.createTextNode(this.getSettleDate()));
            root.appendChild(fSettleDate);
        }

        if (this.getUpsNo() != null) {
            Element fUpsNo = doc.createElement("UpsNo");
            fUpsNo.appendChild(doc.createTextNode(this.getUpsNo()));
            root.appendChild(fUpsNo);
        }

        if (this.getTsNo() != null) {
            Element fTsNo = doc.createElement("TsNo");
            fTsNo.appendChild(doc.createTextNode(this.getTsNo()));
            root.appendChild(fTsNo);
        }

        if (this.getReference() != null) {
            Element fReference = doc.createElement("Reference");
            fReference.appendChild(doc.createTextNode(this.getReference()));
            root.appendChild(fReference);
        }

        if (this.getReturnAddress() != null) {
            Element fReturnAddress = doc.createElement("ReturnAddress");
            fReturnAddress.appendChild(doc.createTextNode(this.getReturnAddress()));
            root.appendChild(fReturnAddress);
        }

        if (this.getRespCode() != null) {
            Element fRespCode = doc.createElement("RespCode");
            fRespCode.appendChild(doc.createTextNode(this.getRespCode()));
            root.appendChild(fRespCode);
        }

        if (this.getRemark() != null) {
            Element fRemark = doc.createElement("Remark");
            fRemark.appendChild(doc.createTextNode(this.getRemark()));
            root.appendChild(fRemark);
        }

        if (this.getTerminalNo() != null) {
            Element fTerminalNo = doc.createElement("TerminalNo");
            fTerminalNo.appendChild(doc.createTextNode(this.getTerminalNo()));
            root.appendChild(fTerminalNo);
        }

        if (this.getMerchantNo() != null) {
            Element fMerchantNo = doc.createElement("MerchantNo");
            fMerchantNo.appendChild(doc.createTextNode(this.getMerchantNo()));
            root.appendChild(fMerchantNo);
        }

        if (this.getOrderNo() != null) {
            Element fOrderNo = doc.createElement("OrderNo");
            fOrderNo.appendChild(doc.createTextNode(this.getOrderNo()));
            root.appendChild(fOrderNo);
        }

        if (this.getOrderState() != null) {

            Element fOrderState = doc.createElement("OrderState");
            fOrderState.appendChild(doc.createTextNode(this.getOrderState()));
            root.appendChild(fOrderState);
        }

        if (this.getDescription() != null) {
            Element fDescription = doc.createElement("Description");
            fDescription.appendChild(doc.createTextNode(this.getDescription()));
            root.appendChild(fDescription);
        }

        if (this.getValidTime() != null) {
            Element fValidTime = doc.createElement("ValidTime");
            fValidTime.appendChild(doc.createTextNode(this.getValidTime()));
            root.appendChild(fValidTime);
        }

        if (this.getOrderType() != null) {
            Element fOrderType = doc.createElement("OrderType");
            fOrderType.appendChild(doc.createTextNode(this.getOrderType()));
            root.appendChild(fOrderType);
        }

        if (this.getCustName() != null) {
            Element fCustName = doc.createElement("CustName");
            fCustName.appendChild(doc.createTextNode(this.getCustName()));
            root.appendChild(fCustName);
        }
        if (this.getCustId() != null) {
            Element fCustId = doc.createElement("CustId");
            fCustId.appendChild(doc.createTextNode(this.getCustId()));
            root.appendChild(fCustId);
        }
        if (this.getBankName() != null) {
            Element fBankName = doc.createElement("BankName");
            fBankName.appendChild(doc.createTextNode(this.getBankName()));
            root.appendChild(fBankName);
        }
        if (this.getCustAddress() != null) {
            Element fCustAddress = doc.createElement("CustAddress");
            fCustAddress.appendChild(doc.createTextNode(this.getCustAddress()));
            root.appendChild(fCustAddress);
        }
        if (this.getBankAddress() != null) {
            Element fBankAddress = doc.createElement("BankAddress");
            fBankAddress.appendChild(doc.createTextNode(this.getBankAddress()));
            root.appendChild(fBankAddress);
        }
        if (this.getBeneficiary() != null) {
            Element fBeneficiary = doc.createElement("Beneficiary");
            fBeneficiary.appendChild(doc.createTextNode(this.getBeneficiary()));
            root.appendChild(fBeneficiary);
        }
        if (this.getCustIp() != null) {
            Element fCustIp = doc.createElement("CustIp");
            fCustIp.appendChild(doc.createTextNode(this.getCustIp()));
            root.appendChild(fCustIp);
        }
        if (this.getBindCode() != null) {
            Element fBindCode = doc.createElement("BindCode");
            fBindCode.appendChild(doc.createTextNode(this.getBindCode()));
            root.appendChild(fBindCode);
        }
        if (this.getCustPhoto() != null) {
            Element fCustPhoto = doc.createElement("CustPhoto");
            fCustPhoto.appendChild(doc.createTextNode(this.getCustPhoto()));
            root.appendChild(fCustPhoto);
        }
        if (this.getCustCert() != null) {
            Element fCustCert = doc.createElement("CustCert");
            fCustCert.appendChild(doc.createTextNode(this.getCustCert()));
            root.appendChild(fCustCert);
        }

        if (this.getTransData() != null) {
            Element fTransData = doc.createElement("TransData");
            fTransData.appendChild(doc.createTextNode(this.getTransData()));
            root.appendChild(fTransData);
        }

        if (this.getPin() != null) {
            Element fPin = doc.createElement("Pin");
            fPin.appendChild(doc.createTextNode(this.getPin()));
            root.appendChild(fPin);
        }

        if (this.getLoginPin() != null) {
            Element fLoginPin = doc.createElement("LoginPin");
            fLoginPin.appendChild(doc.createTextNode(this.getLoginPin()));
            root.appendChild(fLoginPin);
        }

        if (this.getMac() != null) {
            Element fMac = doc.createElement("Mac");
            fMac.appendChild(doc.createTextNode(this.getMac()));
            root.appendChild(fMac);
        }

        return TransactionUtil.getStringFromDocument(doc);
    }
}
