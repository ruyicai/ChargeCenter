package com.ruyicai.charge.dna.v2.thirdpart;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import com.ruyicai.charge.dna.v2.common.Formatter;
import com.ruyicai.charge.dna.v2.common.Strings;
import com.ruyicai.charge.dna.v2.common.Utilities;


/** 银联语音支付接口参数封装类，参数说明请参照<<银联语音支付平台接口规范>>文档。
 * @author Administrator
 *
 */
public class PosMessage implements Serializable
{

    private static final long serialVersionUID = -7784871050544876882L;
    public static int LENGTH_LENGTH = 4;
    private String mProcCode = "";
    public static int ProcCode_Length = 4;

    /**
     * @return 交易代码	ProcCode	Char	4	M	M	4001
     */
    public String getProcCode()
    {
        return mProcCode;
    }

    /**
     * @param value
     */
    public void setProcCode(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mProcCode = value.trim();
//        if (mProcCode.length() != ProcCode_Length)
//            throw new RuntimeException(
//                "Invalid InTransactionMessage.ProcCode.length(): " + mProcCode.length());
        this.getBitMap()[0] = 0x01;
    }
    private byte[] mBitMap;
    public static int BitMap_Length = 64;

    /**
     * @return 交易代码	ProcCode	Char	4	M	M	4001
     */
    public byte[] getBitMap()
    {
        return mBitMap;
    }

    /**
     * @param value
     */
    public void setBitMap(byte[] value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mBitMap = value;
//        if (mBitMap.length != BitMap_Length)
//            throw new RuntimeException(
//                "Invalid InTransactionMessage.BitMap.length(): " + mBitMap.length);
    }
    private String mAccountNum = "";

    /**
     * @return 2 	用户账号	a..2000(LLLLVAR) 	M	M 	填写手机号
     */
    public String getAccountNum()
    {
        return mAccountNum;
    }

    /**
     * @param value
     */
    public void setAccountNum(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mAccountNum = value;
//        if (mAccountNum.length() > 2000)
//            throw new RuntimeException(
//                "Invalid PosMessage.AccountNum.length(): " + mAccountNum.length());
        this.getBitMap()[1] = 0x01;
    }
    private String mProcessCode = "";
    public static int ProcessCode_Length = 6;

    /**
     * @return 3 	处理码	N6 	M 	M 	“190000”
     */
    public String getProcessCode()
    {
        return mProcessCode;
    }

    /**
     * @param value
     */
    public void setProcessCode(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mProcessCode = value;
//        if (mProcessCode.length() != ProcessCode_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.ProcessCode.length(): " + mProcessCode.length());
        this.getBitMap()[2] = 0x01;
    }
    private String mAmount = "";
    public static int Amount_Length = 12;

    /**
     * @return 4 	交易金额	N12 	M 	M 
     */
    public String getAmount()
    {
        return mAmount;
    }

    /**
     * @param value
     */
    public void setAmount(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mAmount = value;
//        if (mAmount.length() > Amount_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.Amount.length(): " + mAmount.length());
        mAmount = Strings.padLeft(mAmount, '0', Amount_Length);
        this.getBitMap()[3] = 0x01;
    }
    private String mCurCode = "";
    public static int CurCode_Length = 2;

    /**
     * @return 7 	传输日期和时间	N10(MMDDhhmmss) 	M 	M  
     */
    public String getCurCode()
    {
        return mCurCode;
    }

    /**
     * @param value
     */
    public void setCurCode(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mCurCode = value;
//        if (mCurCode.length() != CurCode_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.CurCode.length(): " + mCurCode.length());
        this.getBitMap()[4] = 0x01;
    }
    private String mTransDatetime = "";
    public static int TransDatetime_Length = 10;

    /**
     * @return 7 	传输日期和时间	N10(MMDDhhmmss) 	M 	M  
     */
    public String getTransDatetime()
    {
        return mTransDatetime;
    }

    /**
     * @param value
     */
    public void setTransDatetime(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mTransDatetime = value;
//        if (mTransDatetime.length() != TransDatetime_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.TransDatetime.length(): " + mTransDatetime.length());
        this.getBitMap()[6] = 0x01;
    }
    private String mAcqSsn = "";
    public static int AcqSsn_Length = 6;

    /**
     * @return 11	系统跟踪号(流水号)	n6	M	M
     */
    public String getAcqSsn()
    {
        return mAcqSsn;
    }

    /**
     * @param value
     */
    public void setAcqSsn(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mAcqSsn = value;
//        if (mAcqSsn.length() != AcqSsn_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.AcqSsn.length(): " + mAcqSsn.length());
        this.getBitMap()[10] = 1;
    }
    private String mLtime = "";
    public static int Ltime_Length = 6;

    /**
     * @return 12	本地交易时间	n6(hhmmss)	M	M
     */
    public String getLtime()
    {

        return mLtime;
    }

    /**
     * @param value
     */
    public void setLtime(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mLtime = value.trim();
//        if (mLtime.length() != Ltime_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.Ltime.length(): " + mLtime.length());
        this.getBitMap()[11] = 1;
    }
    private String mLdate = "";
    public static int Ldate_Length = 4;

    /**
     * @return 13	本地交易日期	n4(MMDD)	M	M
     */
    public String getLdate()
    {
        return mLdate;
    }

    /**
     * @param value
     */
    public void setLdate(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mLdate = value;
//        if (mLdate.length() != Ldate_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.Ldate.length(): " + mLdate.length());
        this.getBitMap()[12] = 1;

    }
    private String mSettleDate = "";
    public static int SettleDate_Length = 4;

    /**
     * @return 15 	清算日期	N4 		M+ 
     */
    public String getSettleDate()
    {
        return mSettleDate;
    }

    /**
     * @param value
     */
    public void setSettleDate(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mSettleDate = value;
//        if (mSettleDate.length() != SettleDate_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.SettleDate.length(): " + mSettleDate.length());
        this.getBitMap()[14] = 0x01;

    }
    private String mUpsNo = "";
    public static int UpsNo_Length = 12;

    /**
     * @return 
     */
    public String getUpsNo()
    {
        return mUpsNo;
    }

    /**
     * @param value
     */
    public void setUpsNo(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mUpsNo = value;
//        if (mUpsNo.length() != UpsNo_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.UpsNo.length(): " + mUpsNo.length());
        this.getBitMap()[15] = 0x01;

    }
    private String mTsNo = "";
    public static int TsNo_Length = 6;

    /**
     * @return 
     */
    public String getTsNo()
    {
        return mTsNo;
    }

    /**
     * @param value
     */
    public void setTsNo(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mTsNo = value;
//        if (mTsNo.length() != TsNo_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.TsNo.length(): " + mTsNo.length());
        this.getBitMap()[16] = 0x01;

    }
    private String mReference = "";
    public static int Reference_Length = 20;

    /**
     * @return 
     */
    public String getReference()
    {
        return mReference;
    }

    /**
     * @param value
     */
    public void setReference(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        
        if (value.length() < Reference_Length)
            value = Strings.padRight(value, Reference_Length);
        mReference = value;
//        if (mReference.length() != Reference_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.Reference.length(): " + mReference.length());
        this.getBitMap()[17] = 0x01;

    }
    private String mReturnAddress = "";

    /**
     * @return 
     */
    public String getReturnAddress()
    {
        return mReturnAddress;
    }

    /**
     * @param value
     */
    public void setReturnAddress(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mReturnAddress = value;
//        if (mReturnAddress.length() > 99)
//            throw new RuntimeException(
//                "Invalid PosMessage.ReturnAddress.length(): " + mReturnAddress.length());
        this.getBitMap()[18] = 0x01;

    }
    private String mRespCode = "";
    public static int RespCode_Length = 4;

    /**
     * @return 39	响应码	An2		M
     */
    public String getRespCode()
    {
        return mRespCode;
    }

    /**
     * @param value
     */
    public void setRespCode(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mRespCode = value;
//        if (mRespCode.length() != RespCode_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.RespCode.length(): " + mRespCode.length());
        this.getBitMap()[38] = 0x01;
    }
    private String mRemark;

    /**
     * @return 40	备注	ans…200(LLLVAR)		C	
     */
    public String getRemark()
    {
        return mRemark;
    }

    /**
     * @param value
     * @throws UnsupportedEncodingException 
     */
    public void setRemark(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mRemark = value;

        this.getBitMap()[39] = 0x01;
    }
    private String mTerminalNo = "";
    public static int TerminalNo_Length = 8;

    /**
     * @return 41	受卡机终端标识码	Ans8	M	M	
     */
    public String getTerminalNo()
    {
        return mTerminalNo;
    }

    /**
     * @param value
     */
    public void setTerminalNo(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mTerminalNo = value;
//        if (mTerminalNo.length() != TerminalNo_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.TerminalNo.length(): " + mTerminalNo.length());
        this.getBitMap()[40] = 0x01;
    }
    private String mMerchantNo = "";
    public static int MerchantNo_Length = 17;

    /**
     * @return 42 	受卡方标识码	ans17 	M 	M 	商户类型（2）+填写商户号	（15）
     */
    public String getMerchantNo()
    {
        return mMerchantNo;
    }

    /**
     * @param value
     */
    public void setMerchantNo(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        if (value.length() < MerchantNo_Length)
            value = Strings.padRight(value, MerchantNo_Length);
        mMerchantNo = value;

//        if (mMerchantNo.length() != MerchantNo_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.MerchantNo.length(): " + mMerchantNo.length());
        this.getBitMap()[41] = 0x01;
    }
    private String mOrderNo = "";

    /**
     * 43	订单编号	ans40	M	M	类型（n2）+订单编号
     */
    public String getOrderNo()
    {
        return mOrderNo;
    }

    /**
     * @param value
     */
    public void setOrderNo(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mOrderNo = value;
//        if (mOrderNo.length() > 99)
//            throw new RuntimeException(
//                "Invalid PosMessage.OrderNo.length(): " + mOrderNo.length());
        this.getBitMap()[42] = 0x01;
    }
    private String mOrderState = "";
    public static int OrderState_Length = 2;

    /**
     * @return 
     */
    public String getOrderState()
    {
        return mOrderState;
    }

    /**
     * @param value
     */
    public void setOrderState(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mOrderState = value;
//        if (mOrderState.length() != OrderState_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.OrderState.length(): " + mOrderState.length());
        this.getBitMap()[43] = 0x01;

    }
    private String mDescription;

    /**
     * @return 40	Description	ans…200(LLLVAR)		C	
     */
    public String getDescription()
    {
        return mDescription;
    }

    /**
     * @param value 
     */
    public void setDescription(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mDescription = value;

        this.getBitMap()[44] = 0x01;
    }
    private String mValidTime = "";
    public static int ValidTime_Length = 14;

    /**
     * @return 
     */
    public String getValidTime()
    {
        return mValidTime;
    }

    /**
     * @param value
     */
    public void setValidTime(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mValidTime = value;
//        if (mValidTime.length() != ValidTime_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.ValidTime.length(): " + mValidTime.length());
        this.getBitMap()[45] = 0x01;

    }
    private String mOrderType = "";
    public static int OrderType_Length = 2;

    /**
     * @return 
     */
    public String getOrderType()
    {
        return mOrderType;
    }

    /**
     * @param value
     */
    public void setOrderType(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mOrderType = value;
//        if (mOrderType.length() != OrderType_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.OrderType.length(): " + mOrderType.length());
        this.getBitMap()[46] = 0x01;

    }
    private String mTransData;

    /**
     * @return 48	交换数据	ans…200(LLLVAR)		C	当39域为“00”时必选
     */
    public String getTransData()
    {
        return mTransData;
    }

    /**
     * @param value
     */
    public void setTransData(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mTransData = value;
//        if (mTransData.length() > 999)
//            throw new RuntimeException(
//                "Invalid PosMessage.TransData.length(): " + mTransData.length());
        this.getBitMap()[47] = 0x01;
    }
    private String mPin;
    public static int Pin_Length = 32;

    /**
     * @return 63	个人密码	an32 	M 
     */
    public String getPin()
    {
        return mPin;
    }

    /**
     * @param value
     */
    public void setPin(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mPin = value;
//        if (mPin.length() != Pin_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.Pin.length(): " + mPin.length());
        this.getBitMap()[51] = 0x01;
    }
    private String mLoginPin;
    public static int LoginPin_Length = 32;

    /**
     * @return 63 	登陆密码	B64 	M 
     */
    public String getLoginPin()
    {
        return mLoginPin;
    }

    /**
     * @param value
     */
    public void setLoginPin(String value)
    {
         if (Strings.isNullOrEmpty(value))
            return;

        mLoginPin = value;
//        if (mLoginPin.length() != LoginPin_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.LoginPin.length(): " + mLoginPin.length());
        this.getBitMap()[62] = 0x01;
    }
    private String mMac;
    public static int Mac_Length = 32;

    /**
     * @return 64 	MAC 	an32 	M 	M  
     */
    public String getMac()
    {
        return mMac;
    }

    /**
     * @param value
     */
    public void setMac(String value)
    {
        if (Strings.isNullOrEmpty(value))
            return;

        mMac = value;
//        if (mMac.length() != Mac_Length)
//            throw new RuntimeException(
//                "Invalid PosMessage.Mac.length(): " + mMac.length());
        this.getBitMap()[63] = 0x01;
    }

    public PosMessage()
    {
        this.setBitMap(new byte[64]);
        for (int i = 0; i < this.getBitMap().length; i++)
            this.getBitMap()[i] = 0x00;
    }

    public PosMessage(String procCode)
    {
        this.setBitMap(new byte[64]);
        for (int i = 0; i < this.getBitMap().length; i++)
            this.getBitMap()[i] = 0x00;
        this.setProcCode(procCode);
    }

    public PosMessage(byte[] bytes) throws Exception
    {
        int index = 0;

        byte[] tempBytes = new byte[ProcCode_Length];

        System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
        String tmpProcCode = Utilities.ChangeByteToString(tempBytes);
        index += tempBytes.length;

        tempBytes = new byte[BitMap_Length / 8];
        System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
        this.setBitMap(Utilities.binBytes2AscBytes(tempBytes));
        this.setProcCode(tmpProcCode);
        index += tempBytes.length;

        if (this.getBitMap()[1] == 0x01)
        {
            tempBytes = new byte[4];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            String llll = Utilities.ChangeByteToString(tempBytes);
            index += tempBytes.length;

            tempBytes = new byte[Integer.parseInt(llll)];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setAccountNum(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[2] == 0x01)
        {
            tempBytes = new byte[ProcessCode_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setProcessCode(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[3] == 0x01)
        {
            tempBytes = new byte[Amount_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setAmount(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[4] == 0x01)
        {
            tempBytes = new byte[CurCode_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setCurCode(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[6] == 0x01)
        {
            tempBytes = new byte[TransDatetime_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setTransDatetime(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[10] == 0x01)
        {
            tempBytes = new byte[AcqSsn_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setAcqSsn(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[11] == 0x01)
        {
            tempBytes = new byte[Ltime_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setLtime(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[12] == 0x01)
        {
            tempBytes = new byte[Ldate_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setLdate(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[14] == 0x01)
        {
            tempBytes = new byte[SettleDate_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setSettleDate(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[15] == 0x01)
        {
            tempBytes = new byte[UpsNo_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setUpsNo(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }


        if (this.getBitMap()[16] == 0x01)
        {
            tempBytes = new byte[TsNo_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setTsNo(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }


        if (this.getBitMap()[17] == 0x01)
        {
            tempBytes = new byte[Reference_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setReference(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[18] == 0x01)
        {
            tempBytes = new byte[2];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            String lll = Utilities.ChangeByteToString(tempBytes);
            index += tempBytes.length;

            tempBytes = new byte[Integer.parseInt(lll)];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setReturnAddress(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[38] == 0x01)
        {
            tempBytes = new byte[RespCode_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setRespCode(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[39] == 0x01)
        {
            tempBytes = new byte[3];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            String lll = Utilities.ChangeByteToString(tempBytes);
            index += tempBytes.length;

            tempBytes = new byte[Integer.parseInt(lll)];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setRemark(Utilities.ChangeGB2312ByteToString(tempBytes));
            index += tempBytes.length;
        }


        if (this.getBitMap()[40] == 0x01)
        {
            tempBytes = new byte[TerminalNo_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setTerminalNo(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[41] == 0x01)
        {
            tempBytes = new byte[MerchantNo_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setMerchantNo(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[42] == 0x01)
        {
            tempBytes = new byte[2];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            String ll = Utilities.ChangeByteToString(tempBytes);
            index += tempBytes.length;

            tempBytes = new byte[Integer.parseInt(ll)];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setOrderNo(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[43] == 0x01)
        {
            tempBytes = new byte[OrderState_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setOrderState(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[44] == 0x01)
        {
            tempBytes = new byte[2];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            String ll = Utilities.ChangeByteToString(tempBytes);
            index += tempBytes.length;

            tempBytes = new byte[Integer.parseInt(ll)];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setDescription(Utilities.ChangeGB2312ByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[45] == 0x01)
        {
            tempBytes = new byte[ValidTime_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setValidTime(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[46] == 0x01)
        {
            tempBytes = new byte[OrderType_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setOrderType(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[47] == 0x01)
        {
            tempBytes = new byte[3];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            String ll = Utilities.ChangeByteToString(tempBytes);
            index += tempBytes.length;

            tempBytes = new byte[Integer.parseInt(ll)];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setTransData(Utilities.ChangeGB2312ByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[51] == 0x01)
        {
            tempBytes = new byte[Pin_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setPin(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[62] == 0x01)
        {
            tempBytes = new byte[LoginPin_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setLoginPin(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

        if (this.getBitMap()[63] == 0x01)
        {
            tempBytes = new byte[Mac_Length];
            System.arraycopy(bytes, index, tempBytes, 0, tempBytes.length);
            this.setMac(Utilities.ChangeByteToString(tempBytes));
            index += tempBytes.length;
        }

    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("\n" + "[" + "\n");
        sb.append("Class=" + this.getClass().getSimpleName() + "\n");
        sb.append("ProcCode=" + this.getProcCode() + "\n");

        sb.append("BitMap=" + Formatter.formatBytes(this.getBitMap()) + "\n");
        if (this.getBitMap()[1] == 0x01)
            sb.append("AccountNum=" + this.getAccountNum() + "\n");
        if (this.getBitMap()[2] == 0x01)
            sb.append("ProcessCode=" + this.getProcessCode() + "\n");
        if (this.getBitMap()[3] == 0x01)
            sb.append("Amount=" + this.getAmount() + "\n");
        if (this.getBitMap()[4] == 0x01)
            sb.append("CurCode=" + this.getCurCode() + "\n");
        if (this.getBitMap()[6] == 0x01)
            sb.append("TransDatetime=" + this.getTransDatetime() + "\n");
        if (this.getBitMap()[10] == 0x01)
            sb.append("AcqSsn=" + this.getAcqSsn() + "\n");
        if (this.getBitMap()[11] == 0x01)
            sb.append("Ltime=" + this.getLtime() + "\n");
        if (this.getBitMap()[12] == 0x01)
            sb.append("Ldate=" + this.getLdate() + "\n");
        if (this.getBitMap()[14] == 0x01)
            sb.append("SettleDate=" + this.getSettleDate() + "\n");
        if (this.getBitMap()[15] == 0x01)
            sb.append("UpsNo=" + this.getUpsNo() + "\n");
        if (this.getBitMap()[16] == 0x01)
            sb.append("TsNo=" + this.getTsNo() + "\n");
        if (this.getBitMap()[17] == 0x01)
            sb.append("Reference=" + this.getReference() + "\n");
        if (this.getBitMap()[18] == 0x01)
            sb.append("ReturnAddress=" + this.getReturnAddress() + "\n");
        if (this.getBitMap()[38] == 0x01)
            sb.append("RespCode=" + this.getRespCode() + "\n");
        if (this.getBitMap()[39] == 0x01)
            sb.append("Remark=" + this.getRemark() + "\n");
        if (this.getBitMap()[40] == 0x01)
            sb.append("TerminalNo=" + this.getTerminalNo() + "\n");
        if (this.getBitMap()[41] == 0x01)
            sb.append("MerchantNo=" + Strings.formatPassword(this.getMerchantNo(), 5, 5) + "\n");
        if (this.getBitMap()[42] == 0x01)
            sb.append("OrderNo=" + this.getOrderNo() + "\n");
        if (this.getBitMap()[43] == 0x01)
            sb.append("OrderState=" + this.getOrderState() + "\n");
        if (this.getBitMap()[44] == 0x01)
            sb.append("Description=" + this.getDescription() + "\n");
        if (this.getBitMap()[45] == 0x01)
            sb.append("ValidTime=" + this.getValidTime() + "\n");
        if (this.getBitMap()[46] == 0x01)
            sb.append("OrderType=" + this.getOrderType() + "\n");
        if (this.getBitMap()[47] == 0x01)
            sb.append("TransData=" + this.getTransData() + "\n");
        if (this.getBitMap()[51] == 0x01)
            sb.append("Pin=" + this.getPin() + "\n");
        if (this.getBitMap()[62] == 0x01)
            sb.append("LoginPin=" + this.getLoginPin() + "\n");
        if (this.getBitMap()[63] == 0x01)
            sb.append("Mac=" + this.getMac() + "\n");
        sb.append("]");
        return sb.toString();
    }

	public boolean needSecondAcountQuery() {
		return getRespCode().equals("T438") || getRespCode().equals("T437")
				|| getRespCode().equals("T404");
	}
}
