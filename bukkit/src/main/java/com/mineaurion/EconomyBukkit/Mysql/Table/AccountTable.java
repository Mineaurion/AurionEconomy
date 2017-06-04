package com.mineaurion.EconomyBukkit.Mysql.Table;

public class AccountTable extends DatabaseTable {
	public static final String TABLE_NAME = "account";

	public final String createTableMySQL = "CREATE TABLE IF NOT EXISTS `" + getPrefix()
			+ "account` ( `ID` INT NOT NULL AUTO_INCREMENT , "
			+ "`UUID` VARCHAR(50) NOT NULL , "
			+ "`NAME` VARCHAR(100) NOT NULL , "
			+ "`BALANCE` DOUBLE DEFAULT NULL , "
			+ "`INFINIMONNEY` BOOLEAN NOT NULL DEFAULT FALSE , "
			+ "PRIMARY KEY (`ID`), UNIQUE (`UUID`)) ENGINE = InnoDB CHARSET=utf8;";

	public final String selectEntrywithName = "SELECT * FROM " + getPrefix() + "account" + " WHERE NAME=?";

	public final String selectEntryUUID = "SELECT * FROM " + getPrefix() + "account" + " WHERE UUID=?";
	
	public final String selectBalance = "SELECT `BALANCE` FROM " + getPrefix() + "account" + " WHERE UUID=?";
	
	public final String selectinfini = "SELECT `INFINIMONNEY` FROM " + getPrefix() + "account" + " WHERE UUID=?";
	
	public final String inserNewPlayer = "INSERT INTO " + getPrefix() + "account" + "(UUID,NAME,BALANCE) VALUES(?,?,?)";

	public final String insertEntryAllInfo = "INSERT INTO " + getPrefix() + "account"
			+ "(UUID,NAME,BALANCE,INFINIMONNEY) VALUES(?,?,?,?)";

	public final String updateInfinitemoneyEntryUUID = "UPDATE " + getPrefix() + "account"
			+ " SET INFINIMONNEY=? WHERE UUID=?";

	public final String updatebalance = "UPDATE " + getPrefix() + "account" + " SET BALANCE=? WHERE UUID=?";

	public final String updateNameByUuid = "UPDATE " + getPrefix() + "account" + " SET NAME=? WHERE UUID=?";

	public final String updateUuidByName = " UPDATE " + getPrefix() + "account" + " SET UUID=? WHERE NAME=?";
	
	public final String deleteEntry = "DELETE FROM " + getPrefix() + "account" + " WHERE UUID=?";
	
	public final String top = "SELECT * FROM " + getPrefix() + "account" + " WHERE INFINIMONNEY=false ORDER BY BALANCE DESC LIMIT 10";
	
	public AccountTable(String prefix) {
		super(prefix);
	}
}
