package DataDealer;

import DbConn.EvidenceData;

public class DataDealerTool {

	public static EvidenceData getsimHash(EvidenceData data)
	{
		//����Ӧ��һЩ����Ŀǰ���ҷ���ԭ�������ݰ�
		data.simhaString=data.contentString;
		return data;
	}
}
