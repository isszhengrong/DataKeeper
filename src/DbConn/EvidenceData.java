package DbConn;

import java.sql.Timestamp;

public class EvidenceData {
  public String idString;
  public String contentString;
  public String simhaString;
  public Timestamp eventtimeTimestamp;
  public String owner_node;
  public String oper_type; 
  public Timestamp getEventtimeTimestamp() {
	return eventtimeTimestamp;
}
public void setEventtimeTimestamp(Timestamp eventtimeTimestamp) {
	this.eventtimeTimestamp = eventtimeTimestamp;
}
public String getOwner_node() {
	return owner_node;
}
public void setOwner_node(String owner_node) {
	this.owner_node = owner_node;
}
public String getOper_type() {
	return oper_type;
}
public void setOper_type(String oper_type) {
	this.oper_type = oper_type;
}
public String getRemark() {
	return remark;
}
public void setRemark(String remark) {
	this.remark = remark;
}
public String remark;
  
public String getIdString() {
	return idString;
}
public void setIdString(String idString) {
	this.idString = idString;
}
public String getContentString() {
	return contentString;
}
public void setContentString(String contentString) {
	this.contentString = contentString;
}
public String getSimhaString() {
	return simhaString;
}
public void setSimhaString(String simhaString) {
	this.simhaString = simhaString;
}
}
