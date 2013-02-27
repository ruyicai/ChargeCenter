package com.ruyicai.charge.dna.thirdpart;

import java.io.UnsupportedEncodingException;

import com.ruyicai.charge.dna.common.Strings;
import com.ruyicai.charge.dna.common.ToolKit;
import com.ruyicai.charge.dna.common.Utilities;
import com.ruyicai.charge.dna.thirdpart.PosMessage;

public class TransactionUtil
{

    /**
     * get the mac string block of pos message
     * 
     * @param pm
     * @return
     */
    public static String getMacString(PosMessage pm)
    {
        String macStr = pm.getProcCode() +
            (pm.getBitMap()[1] == 0x01 ? " " + pm.getAccountNum().trim() : "") +
            (pm.getBitMap()[2] == 0x01 ? " " + pm.getProcessCode() : "") +
            (pm.getBitMap()[3] == 0x01 ? " " + pm.getAmount() : "") +
            (pm.getBitMap()[6] == 0x01 ? " " + pm.getTransDatetime() : "") +
            (pm.getBitMap()[10] == 0x01 ? " " + pm.getAcqSsn() : "") +
            (pm.getBitMap()[15] == 0x01 ? " " + pm.getUpsNo() : "") +
            (pm.getBitMap()[16] == 0x01 ? " " + pm.getTsNo() : "") +
            (pm.getBitMap()[17] == 0x01 ? " " + pm.getReference().trim().toUpperCase() : "") +
            (pm.getBitMap()[38] == 0x01 ? " " + pm.getRespCode().toUpperCase() : "") +
            (pm.getBitMap()[40] == 0x01 ? " " + pm.getTerminalNo().trim() : "") +
            (pm.getBitMap()[41] == 0x01 ? " " + pm.getMerchantNo().trim().toUpperCase() : "") +
            (pm.getBitMap()[42] == 0x01 ? " " + pm.getOrderNo().trim().toUpperCase() : "") +
            (pm.getBitMap()[43] == 0x01 ? " " + pm.getOrderState().trim() : "");

        ToolKit.writeLog(TransactionUtil.class.getName(), "MacString", "[" + macStr + "]");
        return macStr;
    }

    /**
     * get the binary length of pos message
     * 
     * @param pm
     * @return
     * @throws UnsupportedEncodingException
     */
    public static int getLength(PosMessage pm)
        throws UnsupportedEncodingException
    {
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
     * @param pm
     * @return
     * @throws Exception
     */
    public static byte[] toBytes(PosMessage pm) throws Exception
    {

        byte[] bytes = new byte[getLength(pm)];
        int index = 0;

        byte[] tempBytes = pm.getProcCode().getBytes("US-ASCII");
        System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
        index += tempBytes.length;

        tempBytes = Utilities.ascBytes2BinBytes(pm.getBitMap());
        System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
        index += tempBytes.length;

        if (pm.getBitMap()[1] == 0x01)
        {
            String len = Strings.padLeft("" + pm.getAccountNum().length(), '0', 4);
            tempBytes = (len + pm.getAccountNum()).getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[2] == 0x01)
        {
            tempBytes = pm.getProcessCode().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[3] == 0x01)
        {
            tempBytes = pm.getAmount().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[4] == 0x01)
        {
            tempBytes = pm.getCurCode().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[6] == 0x01)
        {
            tempBytes = pm.getTransDatetime().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[10] == 0x01)
        {
            tempBytes = pm.getAcqSsn().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[11] == 0x01)
        {
            tempBytes = pm.getLtime().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[12] == 0x01)
        {
            tempBytes = pm.getLdate().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[14] == 0x01)
        {
            tempBytes = pm.getSettleDate().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[15] == 0x01)
        {
            tempBytes = pm.getUpsNo().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[16] == 0x01)
        {
            tempBytes = pm.getTsNo().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[17] == 0x01)
        {
            tempBytes = pm.getReference().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[18] == 0x01)
        {
            String len = Strings.padLeft("" + pm.getReturnAddress().length(), '0', 2);
            tempBytes = (len + pm.getReturnAddress()).getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[38] == 0x01)
        {
            tempBytes = pm.getRespCode().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[39] == 0x01)
        {

            tempBytes = pm.getRemark().getBytes("GB2312");
            byte[] len = Strings.padLeft("" + tempBytes.length,
                '0', 3).getBytes("US-ASCII");

            System.arraycopy(len, 0, bytes, index, len.length);
            index += len.length;

            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[40] == 0x01)
        {
            tempBytes = pm.getTerminalNo().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[41] == 0x01)
        {
            tempBytes = pm.getMerchantNo().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[42] == 0x01)
        {
            String len = Strings.padLeft("" + pm.getOrderNo().length(), '0', 2);
            tempBytes = (len + pm.getOrderNo()).getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[43] == 0x01)
        {
            tempBytes = pm.getOrderState().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[44] == 0x01)
        {

            tempBytes = pm.getDescription().getBytes("GB2312");
            byte[] len = Strings.padLeft("" + tempBytes.length,
                '0', 2).getBytes("US-ASCII");

            System.arraycopy(len, 0, bytes, index, len.length);
            index += len.length; 

            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[45] == 0x01)
        {
            tempBytes = pm.getValidTime().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[46] == 0x01)
        {
            tempBytes = pm.getOrderType().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[47] == 0x01)
        {
//            String len = dnapay.common.Strings.padLeft("" + pm.getTransData().length(), '0', 3);
//            tempBytes = (len + pm.getTransData()).getBytes("US-ASCII");
//            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
//            index += tempBytes.length;

             tempBytes = pm.getTransData().getBytes("GB2312");
            byte[] len = Strings.padLeft("" + tempBytes.length,
                '0', 3).getBytes("US-ASCII");

            System.arraycopy(len, 0, bytes, index, len.length);
            index += len.length;

            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[51] == 0x01)
        {
            tempBytes = pm.getPin().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[62] == 0x01)
        {
            tempBytes = pm.getLoginPin().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        if (pm.getBitMap()[63] == 0x01)
        {
            tempBytes = pm.getMac().getBytes("US-ASCII");
            System.arraycopy(tempBytes, 0, bytes, index, tempBytes.length);
            index += tempBytes.length;
        }

        return bytes;
    }

    /**
     * translate SocketMessage to WebService Message
     * 
     * @param request
     * @return
     */
    public static com.ruyicai.charge.dna.thirdpart.jaws.PosMessage translate(
        PosMessage request)
    {
    	com.ruyicai.charge.dna.thirdpart.jaws.PosMessage pm = new com.ruyicai.charge.dna.thirdpart.jaws.PosMessage();

        pm.setProcCode(request.getProcCode());
        pm.setBitMap(request.getBitMap());

        if (pm.getBitMap()[1] == 0x01)
        {
            pm.setAccountNum(request.getAccountNum());
        }

        if (pm.getBitMap()[2] == 0x01)
        {
            pm.setProcessCode(request.getProcessCode());
        }

        if (pm.getBitMap()[3] == 0x01)
        {
            pm.setAmount(request.getAmount());
        }

        if (pm.getBitMap()[4] == 0x01)
        {
            pm.setCurCode(request.getCurCode());
        }

        if (pm.getBitMap()[6] == 0x01)
        {
            pm.setTransDatetime(request.getTransDatetime());
        }

        if (pm.getBitMap()[10] == 0x01)
        {
            pm.setAcqSsn(request.getAcqSsn());
        }

        if (pm.getBitMap()[11] == 0x01)
        {
            pm.setLtime(request.getLtime());
        }

        if (pm.getBitMap()[12] == 0x01)
        {
            pm.setLdate(request.getLdate());
        }

        if (pm.getBitMap()[14] == 0x01)
        {
            pm.setSettleDate(request.getSettleDate());
        }

        if (pm.getBitMap()[15] == 0x01)
        {
            pm.setUpsNo(request.getUpsNo());
        }
        if (pm.getBitMap()[16] == 0x01)
        {
            pm.setTsNo(request.getTsNo());
        }
        if (pm.getBitMap()[17] == 0x01)
        {
            pm.setReference(request.getReference());
        }
        if (pm.getBitMap()[18] == 0x01)
        {
            pm.setReturnAddress(request.getReturnAddress());
        }

        if (pm.getBitMap()[38] == 0x01)
        {
            pm.setRespCode(request.getRespCode());
        }

        if (pm.getBitMap()[39] == 0x01)
        {
            pm.setRemark(request.getRemark());
        }

        if (pm.getBitMap()[40] == 0x01)
        {
            pm.setTerminalNo(request.getTerminalNo());
        }

        if (pm.getBitMap()[41] == 0x01)
        {
            pm.setMerchantNo(request.getMerchantNo());
        }

        if (pm.getBitMap()[42] == 0x01)
        {
            pm.setOrderNo(request.getOrderNo());
        }

        if (pm.getBitMap()[43] == 0x01)
        {
            pm.setOrderState(request.getOrderState());
        }
        if (pm.getBitMap()[44] == 0x01)
        {
            pm.setDescription(request.getDescription());
        }
        if (pm.getBitMap()[45] == 0x01)
        {
            pm.setValidTime(request.getValidTime());
        }
        if (pm.getBitMap()[46] == 0x01)
        {
            pm.setOrderType(request.getOrderType());
        }

        if (pm.getBitMap()[47] == 0x01)
        {
            pm.setTransData(request.getTransData());
        }

        if (pm.getBitMap()[51] == 0x01)
        {
            pm.setPin(request.getPin());
        }

        if (pm.getBitMap()[62] == 0x01)
        {
            pm.setLoginPin(request.getLoginPin());
        }

        if (pm.getBitMap()[63] == 0x01)
        {
            pm.setMac(request.getMac());
        }

        pm.setBitMap(null);

        return pm;
    }

    /**
     * translate WebService Message to SocketMessage
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    public static PosMessage translate(
    		com.ruyicai.charge.dna.thirdpart.jaws.PosMessage request)
    {
        PosMessage pm = new PosMessage();

        pm.setProcCode(request.getProcCode());
        if (request.getBitMap() != null)
        {
            pm.setBitMap(request.getBitMap());

            if (pm.getBitMap()[1] == 0x01)
            {
                pm.setAccountNum(request.getAccountNum());
            }

            if (pm.getBitMap()[2] == 0x01)
            {
                pm.setProcessCode(request.getProcessCode());
            }

            if (pm.getBitMap()[3] == 0x01)
            {
                pm.setAmount(request.getAmount());
            }

            if (pm.getBitMap()[4] == 0x01)
            {
                pm.setCurCode(request.getCurCode());
            }

            if (pm.getBitMap()[6] == 0x01)
            {
                pm.setTransDatetime(request.getTransDatetime());
            }

            if (pm.getBitMap()[10] == 0x01)
            {
                pm.setAcqSsn(request.getAcqSsn());
            }

            if (pm.getBitMap()[11] == 0x01)
            {
                pm.setLtime(request.getLtime());
            }

            if (pm.getBitMap()[12] == 0x01)
            {
                pm.setLdate(request.getLdate());
            }

            if (pm.getBitMap()[14] == 0x01)
            {
                pm.setSettleDate(request.getSettleDate());
            }

            if (pm.getBitMap()[15] == 0x01)
            {
                pm.setUpsNo(request.getUpsNo());
            }
            if (pm.getBitMap()[16] == 0x01)
            {
                pm.setTsNo(request.getTsNo());
            }
            if (pm.getBitMap()[17] == 0x01)
            {
                pm.setReference(request.getReference());
            }
            if (pm.getBitMap()[18] == 0x01)
            {
                pm.setReturnAddress(request.getReturnAddress());
            }

            if (pm.getBitMap()[38] == 0x01)
            {
                pm.setRespCode(request.getRespCode());
            }

            if (pm.getBitMap()[39] == 0x01)
            {
                pm.setRemark(request.getRemark());
            }

            if (pm.getBitMap()[40] == 0x01)
            {
                pm.setTerminalNo(request.getTerminalNo());
            }

            if (pm.getBitMap()[41] == 0x01)
            {
                pm.setMerchantNo(request.getMerchantNo());
            }

            if (pm.getBitMap()[42] == 0x01)
            {
                pm.setOrderNo(request.getOrderNo());
            }

            if (pm.getBitMap()[43] == 0x01)
            {
                pm.setOrderState(request.getOrderState());
            }
            if (pm.getBitMap()[44] == 0x01)
            {
                pm.setDescription(request.getDescription());
            }
            if (pm.getBitMap()[45] == 0x01)
            {
                pm.setValidTime(request.getValidTime());
            }
            if (pm.getBitMap()[46] == 0x01)
            {
                pm.setOrderType(request.getOrderType());
            }

            if (pm.getBitMap()[47] == 0x01)
            {
                pm.setTransData(request.getTransData());
            }

            if (pm.getBitMap()[51] == 0x01)
            {
                pm.setPin(request.getPin());
            }

            if (pm.getBitMap()[62] == 0x01)
            {
                pm.setLoginPin(request.getLoginPin());
            }

            if (pm.getBitMap()[63] == 0x01)
            {
                pm.setMac(request.getMac());
            }
        }
        else
        {
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
}
