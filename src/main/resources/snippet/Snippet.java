package snippet;

public class Snippet {
	databaseChangeLog:
	  - changeSet:
	      id: 1
	      author: your_name
	      changes:
	        - createTable:
	            tableName: example_table
	            columns:
	              - column:
	                  name: id
	                  type: BIGINT
	                  autoIncrement: true
	                  constraints:
	                    primaryKey: true
	         
	
}

