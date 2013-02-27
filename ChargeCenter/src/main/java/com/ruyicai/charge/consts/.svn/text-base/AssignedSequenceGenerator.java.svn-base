package com.ruyicai.charge.consts;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.time.FastDateFormat;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

public class AssignedSequenceGenerator extends TableGenerator implements
		PersistentIdentifierGenerator, Configurable {

	private String prefix;
	private Integer fmtWidth;
	private FastDateFormat sdf;
	private Integer INCREMENT_PARAM = 0;

	@Override
	public void configure(Type type, Properties params, Dialect dialect)
			throws MappingException {
		super.configure(IntegerType.INSTANCE, params, dialect);
		if (params.containsKey(Const.Seq_prefix))
			prefix = (String) params.getProperty(Const.Seq_prefix);
		if (params.containsKey(Const.Seq_fmtWidth))
			fmtWidth = new Integer(params.getProperty(Const.Seq_fmtWidth));
		if (params.containsKey(Const.Seq_Date))
			sdf = FastDateFormat.getInstance(params.getProperty(Const.Seq_Date));
		if (params.containsKey(TableGenerator.INCREMENT_PARAM))
			INCREMENT_PARAM = new Integer(params.getProperty(TableGenerator.INCREMENT_PARAM));
	}

	@Override
	public Serializable generate(SessionImplementor session, Object obj)
			throws HibernateException {
		int seq = (Integer) super.generate(session, obj)+1+INCREMENT_PARAM;
		StringBuffer sbf = new StringBuffer();
		if (prefix != null) {
			sbf.append(prefix);
		}
		if (sdf != null) {
			Date now = new Date();
			sbf.append(sdf.format(now));
		}
		if (fmtWidth != null) {
			sbf.append(format(seq, fmtWidth));
		} else {
			sbf.append(seq);
		}
		return sbf.toString();
	}

	private String format(int num, int width) {
		if (num < 0)
			return "";
		StringBuffer sb = new StringBuffer();
		String s = "" + num;
		if (s.length() < width) {
			int addNum = width - s.length();
			for (int i = 0; i < addNum; i++) {
				sb.append("0");
			}
			sb.append(s);
		} else {
			return s.substring(s.length() - width, s.length());
		}
		return sb.toString();
	}
}
