package com.mineaurion.AurionEconomy.Mysql.Table;

public class LogTable extends DatabaseTable {
	public static final String TABLE_NAME = "log";

	public final String createTableMySQL = "CREATE TABLE IF NOT EXISTS `" + getPrefix() 
			+ "LOG` ( `ID` INT(11) NOT NULL AUTO_INCREMENT , "
			+ "`PlAYER` VARCHAR(100) NOT NULL , "
			+ "`TYPE` VARCHAR(30) NOT NULL , "
			+ "`TIME` DATETIME NOT NULL , "
			+ "`AMOUNT` DOUBLE NULL DEFAULT NULL , "
			+ "PRIMARY KEY (`ID`)) ENGINE = InnoDB CHARSET=utf8;";

	
	public final String insertEntry = "INSERT INTO " + getPrefix() + "LOG"
			+ "(PLAYER,TYPE,TIME,AMOUNT)VALUES(?,?,?,?)";

	public final String selectEntry = "SELECT * FROM `" + getPrefix() + "LOG` ";

	public final String cleanEntry = "DELETE FROM " + getPrefix() + "LOG" + " WHERE TIME <= ?";

	public LogTable(String prefix) {
		super(prefix);
	}
}
