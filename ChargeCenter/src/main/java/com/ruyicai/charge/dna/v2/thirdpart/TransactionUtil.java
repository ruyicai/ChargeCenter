package com.ruyicai.charge.dna.v2.thirdpart;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ruyicai.charge.dna.v2.common.Formatter;
import com.ruyicai.charge.dna.v2.common.Strings;
import com.ruyicai.charge.dna.v2.common.ToolKit;
import com.ruyicai.charge.dna.v2.common.Util;
import com.ruyicai.charge.dna.v2.common.Utilities;
import com.ruyicai.charge.dna.v2.common.encrpt.RSA;

/** 银联语音支付平台交易服务接口工具类
 * 
 * @author Administrator
 */
public class TransactionUtil {

    /** get the mac string block of pos message
     * 
     * @param pm　message
     * @return　MAC String
     */
    public static String getMacString(PosMessage pm) {
        String macStr = pm.getProcCode()
                + (pm.getBitMap()[1] == 0x01 ? " " + pm.getAccountNum().trim() : "")
                + (pm.getBitMap()[2] == 0x01 ? " " + pm.getProcessCode() : "")
                + (pm.getBitMap()[3] == 0x01 ? " " + pm.getAmount() : "")
                + (pm.getBitMap()[6] == 0x01 ? " " + pm.getTransDatetime() : "")
                + (pm.getBitMap()[10] == 0x01 ? " " + pm.getAcqSsn() : "")
                + (pm.getBitMap()[15] == 0x01 ? " " + pm.getUpsNo() : "")
                + (pm.getBitMap()[16] == 0x01 ? " " + pm.getTsNo() : "")
                + (pm.getBitMap()[17] == 0x01 ? " " + pm.getReference().trim().toUpperCase() : "")
                + (pm.getBitMap()[38] == 0x01 ? " " + pm.getRespCode().toUpperCase() : "")
                + (pm.getBitMap()[40] == 0x01 ? " " + pm.getTerminalNo().trim() : "")
                + (pm.getBitMap()[41] == 0x01 ? " " + pm.getMerchantNo().trim().toUpperCase() : "")
                + (pm.getBitMap()[42] == 0x01 ? " " + pm.getOrderNo().trim().toUpperCase() : "")
                + (pm.getBitMap()[43] == 0x01 ? " " + pm.getOrderState().trim() : "");

        ToolKit.writeLog(TransactionUtil.class.getName(), "MacString", "[" + macStr + "]");
        return macStr;
    }

    /**
     * get the binary length of pos message
     * 
     * @param pm message
     * @return message length
     * @throws UnsupportedEncodingException
     */
    public static int getLength(PosMessage pm)
            throws UnsupportedEncodingException {
        return PosMessage.ProcCode_Length + PosMessage.BitMap_Length / 8 + (pm.getBitMap()[1] == 0x01 ? pm.getAccountNum().length() + 4
                : 0) + (pm.getBitMap()[2] == 0x01 ? PosMessage.ProcessCode_Length
                : 0) + (pm.getBitMap()[3] == 0x01 ? PosMessage.Amount_Length : 0) + (pm.getBitMap()[4] == 0x01 ? PosMessage.CurCode_Length : 0) + (pm.getBitMap()[6] == 0x01 ? PosMessage.TransDatetime_Length
                : 0) + (pm.getBitMap()[10] == 0x01 ? PosMessage.AcqSsn_Length : 0) + (pm.getBitMap()[11] == 0x01 ? PosMessage.Ltime_Length : 0) + (pm.getBitMap()[12] == 0x01 ? PosMessage.Ldate_Length : 0) + (pm.getBitMap()[14] == 0x01 ? PosMessage.SettleDate_Length
                : 0) + (pm.getBitMap()[15] == 0x01 ? PosMessage.UpsNo_Length : 0) + (pm.getBitMap()[16] == 0x01 ? PosMessage.TsNo_Length : 0) + (pm.getBitMap()[17] == 0x01 ? PosMessage.Reference_Length : 0) + (pm.getBitMap()[18] == 0x01 ? pm.getReturnAddress().length() + 2
                : 0) + (pm.getBitMap()[38] == 0x01 ? PosMessage.RespCode_Length : 0) + (pm.getBitMap()[39] == 0x01 ? pm.getRemark().getBytes(
                "GB2312").length + 3 : 0) + (pm.getBitMap()[40] == 0x01 ? PosMessage.TerminalNo_Length
                : 0) + (pm.getBitMap()[41] == 0x01 ? PosMessage.MerchantNo_Length
                : 0) + (pm.getBitMap()[42] == 0x01 ? pm.getOrderNo().length() + 2
                : 0) + (pm.getBitMap()[43] == 0x01 ? PosMessage.OrderState_Length
                : 0) + (pm.getBitMap()[44] == 0x01 ? pm.getDescription().getBytes(
                "GB2312").length + 2 : 0) + (pm.getBitMap()[45] == 0x01 ? PosMessage.ValidTime_Length : 0) + (pm.getBitMap()[46] == 0x01 ? PosMessage.OrderType_Length : 0) + (pm.getBitMap()[47] == 0x01 ? pm.getTransData().getBytes("GB2312").length + 3
                : 0) + (pm.getBitMap()[51] == 0x01 ? PosMessage.Pin_Length : 0) + (pm.getBitMap()[62] == 0x01 ? PosMessage.LoginPin_Length : 0) + (pm.getBitMap()[63] == 0x01 ? PosMessage.Mac_Length : 0);
    }

    /**
     * convert pos message to binary
     * 
     * @param pm message
     * @return data
     * @throws Exception
     */
    public static byte[] toBytes(PosMessage pm) throws Exception {

        byte[] bytes = new byte[getLength(pm)];
        int index = 0;

        byte[] tempBytes = pm.getProcCode().getBytes("US-ASCII");
        System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
        index += tempBytes.length;

        tempBytes = Utilities.ascBytes2BinBytes(pm.getBitMap());
        System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
        index += tempBytes.length;

        if (pm.getBitMap()[1] == 0x01) {
            String len = com.ruyicai.charge.dna.v2.common.Strings.padLeft("" + pm.getAccountNum().length(), '0', 4);
            tempBytes = (len + pm.getAccountNum()).getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[2] == 0x01) {
            tempBytes = pm.getProcessCode().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[3] == 0x01) {
            tempBytes = pm.getAmount().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[4] == 0x01) {
            tempBytes = pm.getCurCode().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[6] == 0x01) {
            tempBytes = pm.getTransDatetime().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[10] == 0x01) {
            tempBytes = pm.getAcqSsn().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[11] == 0x01) {
            tempBytes = pm.getLtime().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[12] == 0x01) {
            tempBytes = pm.getLdate().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[14] == 0x01) {
            tempBytes = pm.getSettleDate().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[15] == 0x01) {
            tempBytes = pm.getUpsNo().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[16] == 0x01) {
            tempBytes = pm.getTsNo().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[17] == 0x01) {
            tempBytes = pm.getReference().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[18] == 0x01) {
            String len = com.ruyicai.charge.dna.v2.common.Strings.padLeft("" + pm.getReturnAddress().length(), '0', 2);
            tempBytes = (len + pm.getReturnAddress()).getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[38] == 0x01) {
            tempBytes = pm.getRespCode().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[39] == 0x01) {

            tempBytes = pm.getRemark().getBytes("GB2312");
            byte[] len = com.ruyicai.charge.dna.v2.common.Strings.padLeft("" + tempBytes.length,
                    '0', 3).getBytes("US-ASCII");

            System.arraycopy(len, 0, bytes, index, len.length);
            index += len.length;

            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[40] == 0x01) {
            tempBytes = pm.getTerminalNo().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[41] == 0x01) {
            tempBytes = pm.getMerchantNo().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[42] == 0x01) {
            String len = com.ruyicai.charge.dna.v2.common.Strings.padLeft("" + pm.getOrderNo().length(), '0', 2);
            tempBytes = (len + pm.getOrderNo()).getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[43] == 0x01) {
            tempBytes = pm.getOrderState().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[44] == 0x01) {

            tempBytes = pm.getDescription().getBytes("GB2312");
            byte[] len = com.ruyicai.charge.dna.v2.common.Strings.padLeft("" + tempBytes.length,
                    '0', 2).getBytes("US-ASCII");

            System.arraycopy(len, 0, bytes, index, len.length);
            index += len.length;

            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[45] == 0x01) {
            tempBytes = pm.getValidTime().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[46] == 0x01) {
            tempBytes = pm.getOrderType().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[47] == 0x01) {
//            String len = com.ruyicai.charge.dna.v2.common.Strings.padLeft("" + pm.getTransData().length(), '0', 3);
//            tempBytes = (len + pm.getTransData()).getBytes("US-ASCII");
//            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
//            index += tempBytes.length;

            tempBytes = pm.getTransData().getBytes("GB2312");
            byte[] len = com.ruyicai.charge.dna.v2.common.Strings.padLeft("" + tempBytes.length,
                    '0', 3).getBytes("US-ASCII");

            System.arraycopy(len, 0, bytes, index, len.length);
            index += len.length;

            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[51] == 0x01) {
            tempBytes = pm.getPin().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[62] == 0x01) {
            tempBytes = pm.getLoginPin().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[63] == 0x01) {
            tempBytes = pm.getMac().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        return bytes;
    }

    /**
     * translate SocketMessage to WebService Message
     * 
     * @param request message
     * @return jaws message
     */
    public static com.ruyicai.charge.dna.v2.thirdpart.jaws.PosMessage translate(
            PosMessage request) {
        com.ruyicai.charge.dna.v2.thirdpart.jaws.PosMessage pm = new com.ruyicai.charge.dna.v2.thirdpart.jaws.PosMessage();

        pm.setProcCode(request.getProcCode());
        pm.setBitMap(request.getBitMap());

        if (pm.getBitMap()[1] == 0x01) {
            pm.setAccountNum(request.getAccountNum());
        }

        if (pm.getBitMap()[2] == 0x01) {
            pm.setProcessCode(request.getProcessCode());
        }

        if (pm.getBitMap()[3] == 0x01) {
            pm.setAmount(request.getAmount());
        }

        if (pm.getBitMap()[4] == 0x01) {
            pm.setCurCode(request.getCurCode());
        }

        if (pm.getBitMap()[6] == 0x01) {
            pm.setTransDatetime(request.getTransDatetime());
        }

        if (pm.getBitMap()[10] == 0x01) {
            pm.setAcqSsn(request.getAcqSsn());
        }

        if (pm.getBitMap()[11] == 0x01) {
            pm.setLtime(request.getLtime());
        }

        if (pm.getBitMap()[12] == 0x01) {
            pm.setLdate(request.getLdate());
        }

        if (pm.getBitMap()[14] == 0x01) {
            pm.setSettleDate(request.getSettleDate());
        }

        if (pm.getBitMap()[15] == 0x01) {
            pm.setUpsNo(request.getUpsNo());
        }
        if (pm.getBitMap()[16] == 0x01) {
            pm.setTsNo(request.getTsNo());
        }
        if (pm.getBitMap()[17] == 0x01) {
            pm.setReference(request.getReference());
        }
        if (pm.getBitMap()[18] == 0x01) {
            pm.setReturnAddress(request.getReturnAddress());
        }

        if (pm.getBitMap()[38] == 0x01) {
            pm.setRespCode(request.getRespCode());
        }

        if (pm.getBitMap()[39] == 0x01) {
            pm.setRemark(request.getRemark());
        }

        if (pm.getBitMap()[40] == 0x01) {
            pm.setTerminalNo(request.getTerminalNo());
        }

        if (pm.getBitMap()[41] == 0x01) {
            pm.setMerchantNo(request.getMerchantNo());
        }

        if (pm.getBitMap()[42] == 0x01) {
            pm.setOrderNo(request.getOrderNo());
        }

        if (pm.getBitMap()[43] == 0x01) {
            pm.setOrderState(request.getOrderState());
        }
        if (pm.getBitMap()[44] == 0x01) {
            pm.setDescription(request.getDescription());
        }
        if (pm.getBitMap()[45] == 0x01) {
            pm.setValidTime(request.getValidTime());
        }
        if (pm.getBitMap()[46] == 0x01) {
            pm.setOrderType(request.getOrderType());
        }

        if (pm.getBitMap()[47] == 0x01) {
            pm.setTransData(request.getTransData());
        }

        if (pm.getBitMap()[51] == 0x01) {
            pm.setPin(request.getPin());
        }

        if (pm.getBitMap()[62] == 0x01) {
            pm.setLoginPin(request.getLoginPin());
        }

        if (pm.getBitMap()[63] == 0x01) {
            pm.setMac(request.getMac());
        }

        pm.setBitMap(null);

        return pm;
    }

    /**
     * translate WebService Message to SocketMessage
     * 
     * @param request message
     * @return jaws message
     * @throws UnsupportedEncodingException
     */
    public static PosMessage translate(
            com.ruyicai.charge.dna.v2.thirdpart.jaws.PosMessage request) {
        PosMessage pm = new PosMessage();

        pm.setProcCode(request.getProcCode());
        if (request.getBitMap() != null) {
            pm.setBitMap(request.getBitMap());

            if (pm.getBitMap()[1] == 0x01) {
                pm.setAccountNum(request.getAccountNum());
            }

            if (pm.getBitMap()[2] == 0x01) {
                pm.setProcessCode(request.getProcessCode());
            }

            if (pm.getBitMap()[3] == 0x01) {
                pm.setAmount(request.getAmount());
            }

            if (pm.getBitMap()[4] == 0x01) {
                pm.setCurCode(request.getCurCode());
            }

            if (pm.getBitMap()[6] == 0x01) {
                pm.setTransDatetime(request.getTransDatetime());
            }

            if (pm.getBitMap()[10] == 0x01) {
                pm.setAcqSsn(request.getAcqSsn());
            }

            if (pm.getBitMap()[11] == 0x01) {
                pm.setLtime(request.getLtime());
            }

            if (pm.getBitMap()[12] == 0x01) {
                pm.setLdate(request.getLdate());
            }

            if (pm.getBitMap()[14] == 0x01) {
                pm.setSettleDate(request.getSettleDate());
            }

            if (pm.getBitMap()[15] == 0x01) {
                pm.setUpsNo(request.getUpsNo());
            }
            if (pm.getBitMap()[16] == 0x01) {
                pm.setTsNo(request.getTsNo());
            }
            if (pm.getBitMap()[17] == 0x01) {
                pm.setReference(request.getReference());
            }
            if (pm.getBitMap()[18] == 0x01) {
                pm.setReturnAddress(request.getReturnAddress());
            }

            if (pm.getBitMap()[38] == 0x01) {
                pm.setRespCode(request.getRespCode());
            }

            if (pm.getBitMap()[39] == 0x01) {
                pm.setRemark(request.getRemark());
            }

            if (pm.getBitMap()[40] == 0x01) {
                pm.setTerminalNo(request.getTerminalNo());
            }

            if (pm.getBitMap()[41] == 0x01) {
                pm.setMerchantNo(request.getMerchantNo());
            }

            if (pm.getBitMap()[42] == 0x01) {
                pm.setOrderNo(request.getOrderNo());
            }

            if (pm.getBitMap()[43] == 0x01) {
                pm.setOrderState(request.getOrderState());
            }
            if (pm.getBitMap()[44] == 0x01) {
                pm.setDescription(request.getDescription());
            }
            if (pm.getBitMap()[45] == 0x01) {
                pm.setValidTime(request.getValidTime());
            }
            if (pm.getBitMap()[46] == 0x01) {
                pm.setOrderType(request.getOrderType());
            }

            if (pm.getBitMap()[47] == 0x01) {
                pm.setTransData(request.getTransData());
            }

            if (pm.getBitMap()[51] == 0x01) {
                pm.setPin(request.getPin());
            }

            if (pm.getBitMap()[62] == 0x01) {
                pm.setLoginPin(request.getLoginPin());
            }

            if (pm.getBitMap()[63] == 0x01) {
                pm.setMac(request.getMac());
            }
        } else {
            pm.setAccountNum(request.getAccountNum());

            pm.setProcessCode(request.getProcessCode());

            pm.setAmount(request.getAmount());

            pm.setCurCode(request.getCurCode());

            pm.setTransDatetime(request.getTransDatetime());

            pm.setAcqSsn(request.getAcqSsn());

            pm.setLtime(request.getLtime());

            pm.setLdate(request.getLdate());

            pm.setSettleDate(request.getSettleDate());

            pm.setUpsNo(request.getUpsNo());

            pm.setTsNo(request.getTsNo());

            pm.setReference(request.getReference());

            pm.setReturnAddress(request.getReturnAddress());

            pm.setRespCode(request.getRespCode());

            pm.setRemark(request.getRemark());

            pm.setTerminalNo(request.getTerminalNo());

            pm.setMerchantNo(request.getMerchantNo());

            pm.setOrderNo(request.getOrderNo());

            pm.setOrderState(request.getOrderState());

            pm.setDescription(request.getDescription());

            pm.setValidTime(request.getValidTime());

            pm.setOrderType(request.getOrderType());

            pm.setTransData(request.getTransData());

            pm.setPin(request.getPin());

            pm.setLoginPin(request.getLoginPin());

            pm.setMac(request.getMac());
        }

        return pm;
    }

    public static String getElementValue(String elemName, Document doc) {
        String elemValue = "";
        if (null != doc) {
            Element elem = null;
            elem = (Element) doc.getElementsByTagName(elemName).item(0);
            if (null != elem && null != elem.getFirstChild()) {
                elemValue = elem.getFirstChild().getNodeValue();
            }
        }
        return elemValue;
    }

    public static String getAllElementsValue(String elemName, Document doc) {
        StringBuffer sb = new StringBuffer();
        if (null != doc) {
            NodeList ftpnodes = (NodeList) doc.getElementsByTagName(elemName);
            for (int i = 0; i < ftpnodes.getLength(); i++) {
                sb.append("<" + ftpnodes.item(i).getNodeName() + ">");
                NodeList ftplist = ftpnodes.item(i).getChildNodes();
                for (int k = 0; k < ftplist.getLength(); k++) {
                    Node subnode = ftplist.item(k);
                    if (subnode.getNodeType() == Node.ELEMENT_NODE) {
                        sb.append("<" + subnode.getNodeName() + ">");
                        if (null != subnode.getFirstChild()) {
                            sb.append(subnode.getFirstChild().getNodeValue());
                        }
                        sb.append("</" + subnode.getNodeName() + ">");
                    }
                }
                sb.append("</" + ftpnodes.item(i).getNodeName() + ">");
            }
        }
        return sb.toString();
    }

    public static String getElementValue(String pElemName, String cElemName, Document doc) {
        String cElemValue = "";
        if (null != doc) {
            Element pElem = null;
            Element cElem = null;
            pElem = (Element) doc.getElementsByTagName(pElemName).item(0);
            if (null != pElem) {
                cElem = (Element) pElem.getElementsByTagName(cElemName).item(0);
                if (null != cElem && null != cElem.getFirstChild()) {
                    cElemValue = cElem.getFirstChild().getNodeValue();
                }
            }

        }
        return cElemValue;
    }

    public static String getStringFromDocument(Document doc) {
        String result = null;

        if (doc != null) {
            StringWriter strWtr = new StringWriter();
            StreamResult strResult = new StreamResult(strWtr);
            TransformerFactory tfac = TransformerFactory.newInstance();
            try {
                Transformer t = tfac.newTransformer();
                t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                t.setOutputProperty(OutputKeys.INDENT, "yes");
                t.setOutputProperty(OutputKeys.METHOD, "xml"); // xml, html,// text
                t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                t.transform(new DOMSource(doc.getDocumentElement()), strResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
            result = strResult.getWriter().toString();
        }

        return result;
    }

    public static String getByteToString(byte[] byteArray) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            sb.append("" + byteArray[i]);
        }
        return sb.toString();
    }

    public static String getHexString(byte[] b) {
        String result = "";
        try {
            for (int i = 0; i < b.length; i++) {
                result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Document getDocumentFromString(String xml) {
        StringBuffer sb = new StringBuffer(xml);
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            doc = db.parse(new InputSource(new StringReader(sb.toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static PosMessage xmlToPosMessage(String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(xml)));
        XmlMessage msg = new XmlMessage();
        msg.setProcCode(TransactionUtil.getElementValue("ProcCode", doc));
        msg.setAccountNum(TransactionUtil.getElementValue("AccountNum", doc));
        msg.setProcessCode(TransactionUtil.getElementValue("ProcessCode", doc));
        msg.setAmount(TransactionUtil.getElementValue("Amount", doc));
        msg.setCurCode(TransactionUtil.getElementValue("CurCode", doc));
        msg.setTransDatetime(TransactionUtil.getElementValue("TransDatetime", doc));
        msg.setAcqSsn(TransactionUtil.getElementValue("AcqSsn", doc));
        msg.setLtime(TransactionUtil.getElementValue("Ltime", doc));
        msg.setLdate(TransactionUtil.getElementValue("Ldate", doc));
        msg.setSettleDate(TransactionUtil.getElementValue("SettleDate", doc));
        msg.setUpsNo(TransactionUtil.getElementValue("UpsNo", doc));
        msg.setTsNo(TransactionUtil.getElementValue("TsNo", doc));
        msg.setReference(TransactionUtil.getElementValue("Reference", doc));
        msg.setReturnAddress(TransactionUtil.getElementValue("ReturnAddress", doc));
        msg.setRespCode(TransactionUtil.getElementValue("RespCode", doc));
        msg.setRemark(TransactionUtil.getElementValue("Remark", doc));
        msg.setTerminalNo(TransactionUtil.getElementValue("TerminalNo", doc));
        msg.setMerchantNo(TransactionUtil.getElementValue("MerchantNo", doc));
        msg.setOrderNo(TransactionUtil.getElementValue("OrderNo", doc));
        msg.setOrderState(TransactionUtil.getElementValue("OrderState", doc));
        msg.setDescription(TransactionUtil.getElementValue("Description", doc));
        msg.setValidTime(TransactionUtil.getElementValue("ValidTime", doc));
        msg.setOrderType(TransactionUtil.getElementValue("OrderType", doc));
        msg.setCustName(TransactionUtil.getElementValue("CustName", doc));
        msg.setCustId(TransactionUtil.getElementValue("CustId", doc));
        msg.setBankName(TransactionUtil.getElementValue("BankName", doc));
        msg.setCustAddress(TransactionUtil.getElementValue("CustAddress", doc));
        msg.setBankAddress(TransactionUtil.getElementValue("BankAddress", doc));
        msg.setBeneficiary(TransactionUtil.getElementValue("Beneficiary", doc));
        msg.setCustIp(TransactionUtil.getElementValue("CustIp", doc));
        msg.setBindCode(TransactionUtil.getElementValue("BindCode", doc));
        msg.setCustPhoto(TransactionUtil.getElementValue("CustPhoto", doc));
        msg.setCustCert(TransactionUtil.getElementValue("CustCert", doc));
        msg.setTransData(TransactionUtil.getElementValue("TransData", doc));
        msg.setPin(TransactionUtil.getElementValue("Pin", doc));
        msg.setLoginPin(TransactionUtil.getElementValue("LoginPin", doc));
        msg.setMac(TransactionUtil.getElementValue("Mac", doc));
        return msg;
    }

    public static String posMessageToXml(PosMessage msg) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.newDocument();
        Element root = doc.createElement("x:NetworkRequest");
        root.setAttribute("xmlns:x", "http://www.dna-pay.com");
        root.setAttribute("xmlns:xsi", "http://www.w3.org");
        doc.appendChild(root);

        if (msg.getProcCode() != null) {
            Element fProcCode = doc.createElement("ProcCode");
            fProcCode.appendChild(doc.createTextNode(msg.getProcCode()));
            root.appendChild(fProcCode);
        }

        if (msg.getAccountNum() != null) {
            Element fAccountNum = doc.createElement("AccountNum");
            fAccountNum.appendChild(doc.createTextNode(msg.getAccountNum()));
            root.appendChild(fAccountNum);
        }


        if (msg.getProcessCode() != null) {
            Element fProcessCode = doc.createElement("ProcessCode");
            fProcessCode.appendChild(doc.createTextNode(msg.getProcessCode()));
            root.appendChild(fProcessCode);
        }

        if (msg.getAmount() != null) {
            Element fAmount = doc.createElement("Amount");
            fAmount.appendChild(doc.createTextNode(msg.getAmount()));
            root.appendChild(fAmount);
        }

        if (msg.getCurCode() != null) {
            Element fCurCode = doc.createElement("CurCode");
            fCurCode.appendChild(doc.createTextNode(msg.getCurCode()));
            root.appendChild(fCurCode);
        }

        if (msg.getTransDatetime() != null) {
            Element fTransDatetime = doc.createElement("TransDatetime");
            fTransDatetime.appendChild(doc.createTextNode(msg.getTransDatetime()));
            root.appendChild(fTransDatetime);
        }

        if (msg.getAcqSsn() != null) {
            Element fAcqSsn = doc.createElement("AcqSsn");
            fAcqSsn.appendChild(doc.createTextNode(msg.getAcqSsn()));
            root.appendChild(fAcqSsn);
        }

        if (msg.getLtime() != null) {
            Element fLtime = doc.createElement("Ltime");
            fLtime.appendChild(doc.createTextNode(msg.getLtime()));
            root.appendChild(fLtime);
        }

        if (msg.getLdate() != null) {
            Element fLdate = doc.createElement("Ldate");
            fLdate.appendChild(doc.createTextNode(msg.getLdate()));
            root.appendChild(fLdate);
        }

        if (msg.getSettleDate() != null) {
            Element fSettleDate = doc.createElement("SettleDate");
            fSettleDate.appendChild(doc.createTextNode(msg.getSettleDate()));
            root.appendChild(fSettleDate);
        }

        if (msg.getUpsNo() != null) {
            Element fUpsNo = doc.createElement("UpsNo");
            fUpsNo.appendChild(doc.createTextNode(msg.getUpsNo()));
            root.appendChild(fUpsNo);
        }

        if (msg.getTsNo() != null) {
            Element fTsNo = doc.createElement("TsNo");
            fTsNo.appendChild(doc.createTextNode(msg.getTsNo()));
            root.appendChild(fTsNo);
        }

        if (msg.getReference() != null) {
            Element fReference = doc.createElement("Reference");
            fReference.appendChild(doc.createTextNode(msg.getReference()));
            root.appendChild(fReference);
        }

        if (msg.getReturnAddress() != null) {
            Element fReturnAddress = doc.createElement("ReturnAddress");
            fReturnAddress.appendChild(doc.createTextNode(msg.getReturnAddress()));
            root.appendChild(fReturnAddress);
        }

        if (msg.getRespCode() != null) {
            Element fRespCode = doc.createElement("RespCode");
            fRespCode.appendChild(doc.createTextNode(msg.getRespCode()));
            root.appendChild(fRespCode);
        }

        if (msg.getRemark() != null) {
            Element fRemark = doc.createElement("Remark");
            fRemark.appendChild(doc.createTextNode(msg.getRemark()));
            root.appendChild(fRemark);
        }

        if (msg.getTerminalNo() != null) {
            Element fTerminalNo = doc.createElement("TerminalNo");
            fTerminalNo.appendChild(doc.createTextNode(msg.getTerminalNo()));
            root.appendChild(fTerminalNo);
        }

        if (msg.getMerchantNo() != null) {
            Element fMerchantNo = doc.createElement("MerchantNo");
            fMerchantNo.appendChild(doc.createTextNode(msg.getMerchantNo()));
            root.appendChild(fMerchantNo);
        }

        if (msg.getOrderNo() != null) {
            Element fOrderNo = doc.createElement("OrderNo");
            fOrderNo.appendChild(doc.createTextNode(msg.getOrderNo()));
            root.appendChild(fOrderNo);
        }

        if (msg.getOrderState() != null) {

            Element fOrderState = doc.createElement("OrderState");
            fOrderState.appendChild(doc.createTextNode(msg.getOrderState()));
            root.appendChild(fOrderState);
        }

        if (msg.getDescription() != null) {
            Element fDescription = doc.createElement("Description");
            fDescription.appendChild(doc.createTextNode(msg.getDescription()));
            root.appendChild(fDescription);
        }

        if (msg.getValidTime() != null) {
            Element fValidTime = doc.createElement("ValidTime");
            fValidTime.appendChild(doc.createTextNode(msg.getValidTime()));
            root.appendChild(fValidTime);
        }

        if (msg.getOrderType() != null) {
            Element fOrderType = doc.createElement("OrderType");
            fOrderType.appendChild(doc.createTextNode(msg.getOrderType()));
            root.appendChild(fOrderType);
        }

        if (msg instanceof XmlMessage) {
            XmlMessage xmlMsg = (XmlMessage) msg;

            if (xmlMsg.getCustName() != null) {
                Element fCustName = doc.createElement("CustName");
                fCustName.appendChild(doc.createTextNode(xmlMsg.getCustName()));
                root.appendChild(fCustName);
            }
            if (xmlMsg.getCustId() != null) {
                Element fCustId = doc.createElement("CustId");
                fCustId.appendChild(doc.createTextNode(xmlMsg.getCustId()));
                root.appendChild(fCustId);
            }
            if (xmlMsg.getBankName() != null) {
                Element fBankName = doc.createElement("BankName");
                fBankName.appendChild(doc.createTextNode(xmlMsg.getBankName()));
                root.appendChild(fBankName);
            }
            if (xmlMsg.getCustAddress() != null) {
                Element fCustAddress = doc.createElement("CustAddress");
                fCustAddress.appendChild(doc.createTextNode(xmlMsg.getCustAddress()));
                root.appendChild(fCustAddress);
            }
            if (xmlMsg.getBankAddress() != null) {
                Element fBankAddress = doc.createElement("BankAddress");
                fBankAddress.appendChild(doc.createTextNode(xmlMsg.getBankAddress()));
                root.appendChild(fBankAddress);
            }
            if (xmlMsg.getBeneficiary() != null) {
                Element fBeneficiary = doc.createElement("Beneficiary");
                fBeneficiary.appendChild(doc.createTextNode(xmlMsg.getBeneficiary()));
                root.appendChild(fBeneficiary);
            }
            if (xmlMsg.getCustIp() != null) {
                Element fCustIp = doc.createElement("CustIp");
                fCustIp.appendChild(doc.createTextNode(xmlMsg.getCustIp()));
                root.appendChild(fCustIp);
            }
            if (xmlMsg.getBindCode() != null) {
                Element fBindCode = doc.createElement("BindCode");
                fBindCode.appendChild(doc.createTextNode(xmlMsg.getBindCode()));
                root.appendChild(fBindCode);
            }
            if (xmlMsg.getCustPhoto() != null) {
                Element fCustPhoto = doc.createElement("CustPhoto");
                fCustPhoto.appendChild(doc.createTextNode(xmlMsg.getCustPhoto()));
                root.appendChild(fCustPhoto);
            }
            if (xmlMsg.getCustCert() != null) {
                Element fCustCert = doc.createElement("CustCert");
                fCustCert.appendChild(doc.createTextNode(xmlMsg.getCustCert()));
                root.appendChild(fCustCert);
            }
        }

        if (msg.getTransData() != null) {
            Element fTransData = doc.createElement("TransData");
            fTransData.appendChild(doc.createTextNode(msg.getTransData()));
            root.appendChild(fTransData);
        }

        if (msg.getPin() != null) {
            Element fPin = doc.createElement("Pin");
            fPin.appendChild(doc.createTextNode(msg.getPin()));
            root.appendChild(fPin);
        }

        if (msg.getLoginPin() != null) {
            Element fLoginPin = doc.createElement("LoginPin");
            fLoginPin.appendChild(doc.createTextNode(msg.getLoginPin()));
            root.appendChild(fLoginPin);
        }

        if (msg.getMac() != null) {
            Element fMac = doc.createElement("Mac");
            fMac.appendChild(doc.createTextNode(msg.getMac()));
            root.appendChild(fMac);
        }

        return TransactionUtil.getStringFromDocument(doc);
    }

     public static XmlMessage toXmlMessage(VpcMessage msg) {
        XmlMessage pm = new XmlMessage();

        pm.setProcCode(msg.getMessageType());
        //每张银行卡的信息组成如下：卡类型标识+卡号（其中卡类型标识2位，01：贷记卡；02：借记卡； 03：储值卡）。
        //快捷支付时传入的银行卡可以是多张，多张银行卡时每张银行卡信息之间用“-”分隔。
        //在VPC工作模式为0和2时,请求报文中必填此域。
        String phoneNum = msg.getPhoneNum();
        String accountNum = "";
        String accountType = "01";
        if (!Strings.isNullOrEmpty(msg.getPrimaryAcctNum())) {
            accountType = msg.getPrimaryAcctNum().substring(0, 2);
            accountNum = msg.getPrimaryAcctNum().substring(2);
        }

        if (!Strings.isNullOrEmpty(phoneNum) && !Strings.isNullOrEmpty(accountNum)) {
            if (accountType.equals("02")) {
                pm.setAccountNum("14" + phoneNum + "|" + accountNum);
            } else {
                pm.setAccountNum("21" + phoneNum + "|" + accountNum);
            }
        } else if (!Strings.isNullOrEmpty(accountNum)) {
            if (accountType.equals("02")) {
                pm.setAccountNum("01" + accountNum);
            } else {
                pm.setAccountNum("02" + accountNum);
            }
        } else {
            pm.setAccountNum("04" + phoneNum);
        }

        pm.setProcessCode(msg.getProcessCode());
        pm.setAmount(msg.getAmount());
        pm.setCurCode(msg.getCurCode());
        pm.setTransDatetime(msg.getTransDatetime());
        pm.setAcqSsn(msg.getSysTraceID());
        pm.setLtime(msg.getTransLocalTime());
        pm.setLdate(msg.getTransLocalDate());
        pm.setSettleDate(msg.getSettleDate());
        pm.setUpsNo(msg.getSequenceNum());
        pm.setTsNo(msg.getTermSeqNum());
        pm.setReference(msg.getReference());
        pm.setReturnAddress(msg.getCallBackUrl());
        pm.setRespCode(msg.getRspCode());
        pm.setRemark(msg.getPrivateData());
        pm.setTerminalNo(msg.getTerminalNo());
        pm.setMerchantNo("02" + msg.getMerchantNo().trim());
        pm.setOrderNo("01" + msg.getOrderNo());
        pm.setOrderState(msg.getOrderState());
        pm.setDescription(msg.getOrderDesc());
        pm.setValidTime(msg.getOrderExpireDate());
        pm.setOrderType(msg.getOrderType());
//        pm.setCustName(msg.getCustName());
//        pm.setCustId(msg.getIdNum());
//        pm.setBankName(msg.getBankName());
//        pm.setCustAddress(msg.getCustAddress());
//        pm.setBankAddress(msg.getBankAddress());
//        pm.setBeneficiary(msg.getBeneficiary());
//        pm.setCustIp(msg.getCustIp());
//        pm.setBindCode(msg.getBindCode());
//        pm.setCustPhoto(msg.getCustPhoto());
//        pm.setCustCert(msg.getCustCert());
        pm.setTransData(msg.getTransData());
        pm.setPin(msg.getPin());
        pm.setLoginPin("");
        pm.setMac(msg.getMac());

        return pm;

    }

    public static VpcMessage toVpcMessage(PosMessage msg) {
        VpcMessage pm = new VpcMessage();

        pm.setMessageType(msg.getProcCode());
        //每张银行卡的信息组成如下：卡类型标识+卡号（其中卡类型标识2位，01：贷记卡；02：借记卡； 03：储值卡）。
        //快捷支付时传入的银行卡可以是多张，多张银行卡时每张银行卡信息之间用“-”分隔。
        //在VPC工作模式为0和2时,请求报文中必填此域。

        if (!Strings.isNullOrEmpty(msg.getAccountNum())) {
            String accountType = msg.getAccountNum().substring(0, 2);
            String strs[] = msg.getAccountNum().substring(2).split("\\|");
            if (accountType.equals("14")) {
                pm.setPhoneNum(strs[0]);
                pm.setPrimaryAcctNum("02" + strs[1]);
            } else if (accountType.equals("21")) {
                pm.setPhoneNum(strs[0]);
                pm.setPrimaryAcctNum("01" + strs[1]);
            } else if (accountType.equals("04")) {
                pm.setPhoneNum(strs[0]);
            } else if (accountType.equals("01")) {
                pm.setPrimaryAcctNum("02" + strs[0]);
            } else if (accountType.equals("02")) {
                 pm.setPrimaryAcctNum("01" + strs[0]);
            }
        }

        pm.setProcessCode(msg.getProcessCode());
        pm.setAmount(msg.getAmount());
        pm.setCurCode(msg.getCurCode());
        pm.setTransDatetime(msg.getTransDatetime());
        pm.setSysTraceID(msg.getAcqSsn());
        pm.setTransLocalTime(msg.getLtime());
        pm.setTransLocalDate(msg.getLdate());
        pm.setSettleDate(msg.getSettleDate());
        pm.setSequenceNum(msg.getUpsNo());
        pm.setTermSeqNum(msg.getTsNo());
        pm.setReference(msg.getReference());
        pm.setCallBackUrl(msg.getReturnAddress());
        pm.setRspCode(msg.getRespCode());
        pm.setPrivateData(msg.getRemark());
        pm.setTerminalNo(msg.getTerminalNo());
        pm.setMerchantNo(msg.getMerchantNo().trim().substring(2));
        pm.setOrderNo(msg.getOrderNo().substring(2));
        pm.setOrderState(msg.getOrderState());
        pm.setOrderDesc(msg.getDescription());
        pm.setOrderExpireDate(msg.getValidTime());
        pm.setOrderType(msg.getOrderType());
//        pm.setCustName(msg.getCustName());
//        pm.setCustId(msg.getIdNum());
//        pm.setBankName(msg.getBankName());
//        pm.setCustAddress(msg.getCustAddress());
//        pm.setBankAddress(msg.getBankAddress());
//        pm.setBeneficiary(msg.getBeneficiary());
//        pm.setCustIp(msg.getCustIp());
//        pm.setBindCode(msg.getBindCode());
//        pm.setCustPhoto(msg.getCustPhoto());
//        pm.setCustCert(msg.getCustCert());
        pm.setTransData(msg.getTransData());
        pm.setPin(msg.getPin());
        pm.setPinType("01");
        pm.setMac(msg.getMac());

        return pm;

    }

    public static VpcMessage toVpcMessage(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            doc = db.parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        VpcMessage msg = new VpcMessage();
        msg.setMessageType(TransactionUtil.getElementValue("MessageType", doc));
        msg.setPrimaryAcctNum(TransactionUtil.getElementValue("PrimaryAcctNum", doc));
        msg.setProcessCode(TransactionUtil.getElementValue("ProcessCode", doc));
        msg.setAmount(TransactionUtil.getElementValue("Amount", doc));
        msg.setCurCode(TransactionUtil.getElementValue("CurCode", doc));
        msg.setTransDatetime(TransactionUtil.getElementValue("TransDatetime", doc));
        msg.setSysTraceID(TransactionUtil.getElementValue("SysTraceID", doc));
        msg.setTransLocalTime(TransactionUtil.getElementValue("TransLocalTime", doc));
        msg.setTransLocalDate(TransactionUtil.getElementValue("TransLocalDate", doc));
        msg.setSettleDate(TransactionUtil.getElementValue("SettleDate", doc));
        msg.setSequenceNum(TransactionUtil.getElementValue("SequenceNum", doc));
        msg.setTermSeqNum(TransactionUtil.getElementValue("TermSeqNum", doc));
        msg.setReference(TransactionUtil.getElementValue("Reference", doc));
        msg.setIdType(TransactionUtil.getElementValue("IdType", doc));
        msg.setIdNum(TransactionUtil.getElementValue("IdNum", doc));
        msg.setPhoneNum(TransactionUtil.getElementValue("PhoneNum", doc));
        msg.setRspCode(TransactionUtil.getElementValue("RspCode", doc));
        msg.setPrivateData(TransactionUtil.getElementValue("PrivateData", doc));
        msg.setTerminalNo(TransactionUtil.getElementValue("TerminalNo", doc));
        msg.setMerchantNo(TransactionUtil.getElementValue("MerchantNo", doc));
        msg.setOrderNo(TransactionUtil.getElementValue("OrderNo", doc));
        msg.setOrderState(TransactionUtil.getElementValue("OrderState", doc));
        msg.setOrderDesc(TransactionUtil.getElementValue("OrderDesc", doc));
        msg.setOrderExpireDate(TransactionUtil.getElementValue("OrderExpireDate", doc));
        msg.setOrderType(TransactionUtil.getElementValue("OrderType", doc));
        msg.setTransData(TransactionUtil.getElementValue("TransData", doc));
        msg.setMerchantName(TransactionUtil.getElementValue("MerchantName", doc));
        msg.setCallBackUrl(TransactionUtil.getElementValue("CallBackUrl", doc));
        msg.setPin(TransactionUtil.getElementValue("Pin", doc));
        msg.setPinType(TransactionUtil.getElementValue("PinType", doc));        
        msg.setMac(TransactionUtil.getElementValue("Mac", doc));
        msg.setSign(TransactionUtil.getElementValue("Sign", doc));
        return msg;
    }

    public static String toXml(VpcMessage msg) throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.newDocument();
        Element root = doc.createElement("vpcpay");
//        Element root = doc.createElement("upomp");
//        root.setAttribute("application", "应用名称.Req");
//        root.setAttribute("version", "通讯协议版本号");
//        root.setAttribute("terminalModel", "终端类型");
//        root.setAttribute("terminalOs", "终端系统");
//        root.setAttribute("pluginVersion", "插件版本号");
//        root.setAttribute("pluginSerialNo", "插件编号");
//        root.setAttribute("terminalPhysicalNo", "手机串号");
        doc.appendChild(root);

        for (Field f : msg.getClass().getDeclaredFields()) {
            String fieldName = f.getName();
            String suffixName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            if (!fieldName.equalsIgnoreCase("serialVersionUID")) {
                try {
                    Method m = msg.getClass().getDeclaredMethod("get" + suffixName, new Class[]{});

                    if (null != m && !m.getReturnType().getName().startsWith("dna")
                            && (!m.getReturnType().getName().startsWith("java.util") || m.getReturnType().getName().startsWith("java.util.Date"))) {

                        Object obj = m.invoke(msg, new Object[]{});
                        if (!Strings.isNullOrEmpty(obj)) {
                            Element element = doc.createElement(suffixName);
                            element.appendChild(doc.createTextNode(obj.toString()));
                            root.appendChild(element);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return TransactionUtil.getStringFromDocument(doc);
    }

    /** 银联UPOP密钥加密算法
     * 
     * @param account 帐号
     * @param passwd 密码
     * @param keyPath 证书公钥文件路径
     * @return 加密后的密文
     */
    public static String encryptPin(String account, String passwd, String keyPath) throws IOException {

        String pinStr = passwd;
        String pinLen = String.valueOf(pinStr.length());
        pinStr = Strings.bytePadLeft(pinLen, '0', 2) + pinStr;
        byte[] pin = Util.str2Bcd(pinStr);

        byte[] pinblock = new byte[8];
        System.arraycopy(pin, 0, pinblock, 0, pin.length);
        for (int i = pin.length; i < 8; i++) {
            pinblock[i] = (byte) 0xFF;
        }

        account = Strings.bytePadLeft(account, '0', 13);
        String panStr = account.substring(account.length() - 13, account.length() - 1);
        panStr = Strings.bytePadLeft(panStr, '0', 16);
        byte[] pan = Util.str2Bcd(panStr);

        for (int i = 0; i < pinblock.length; i++) {
            pan[i] = (byte) (pinblock[i] ^ pan[i]);
        }

        return RSA.encrypt(pan, keyPath);
    }

    /** 银联UPOP密钥加密算法
     * 
     * @param passwd 密码
     * @param pubKey base64 
     * @return 加密后的密文
     */
    public static String encryptPin(String passwd, String pubKey) throws IOException {

        String pinStr = passwd;
        String pinLen = String.valueOf(pinStr.length());
        pinStr = Strings.bytePadLeft(pinLen, '0', 2) + pinStr;
        if (pinStr.length() % 2 == 1) {
            pinStr += "0";
        }
        System.out.println("PinStr:" + pinStr);
        byte[] pin = Util.str2Bcd(pinStr);

        byte[] pinblock = new byte[8];
        System.arraycopy(pin, 0, pinblock, 0, pin.length);
        for (int i = pin.length; i < 8; i++) {
            pinblock[i] = (byte) 0xFF;
        }

        return Formatter.base64Encode(RSA.encrypt64(pinblock, pubKey));
    }

    /** 银联UPOP密钥解密算法
     * 
     * @param passwd 密码
     * @param pubKey base64 
     * @return 加密后的密文
     */
    public static String decryptPin(String passwd, String keyStoreFile, String keyStorePasswd) throws IOException {
        byte[] secDate2 = RSA.decrypt(Formatter.base64Decode(passwd), keyStoreFile, keyStorePasswd);
        System.out.println("decrypt=" + new String(secDate2));
        String pd = Util.bcdBytes2Str(secDate2);
        System.out.println("decrypt=" + pd);
        int len = Integer.valueOf(pd.substring(0, 2));
        pd = pd.substring(2, len + 2);
        System.out.println("decrypt=" + pd);
        return pd;
    }
    
        public static void main(String[] args) throws IOException {
        String pin = "GBdjfFkgHJnuA9vlIfPihfRada2M+qXrpZ1/TMLvZP3t+sPEmcL/rk87amAXFK8kfq9F2lejP97vE17jPFpHawOtmysVf+4JabWrLE1Izhb+M+Q/CgOBo+bdXLjo4w+KgqFkQkqW3g1+6fyR4vVkuaZvcnyhTowFdYN/KaaPePg=";
        TransactionUtil.decryptPin(pin, ToolKit.getPropertyFromFile("GDYILIAN_CERT_PFX"), ToolKit.getPropertyFromFile("GDYILIAN_CERT_PFX_PASSWD"));
                
    }
}
