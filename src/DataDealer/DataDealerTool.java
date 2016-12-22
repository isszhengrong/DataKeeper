package DataDealer;

import DbConn.EvidenceData;

public class DataDealerTool {

	public static EvidenceData getsimHash(EvidenceData data)
	{
		//本来应有一些处理，目前暂且返回原来的数据吧
		data.simhaString=data.contentString;
		return data;
	}
}
