# 1. MySQL

## 1.1. 数据类型

## 1.2. 创建表

```lua
CREATE TABLE `template_source_1` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT,
  `TINYINT_a` tinyint(4) DEFAULT NULL,
  `SMALLINT_a` smallint(6) DEFAULT NULL,
  `INTEGER_a` int(11) DEFAULT NULL,
  `MEDIUMINT_a` mediumint(9) DEFAULT NULL,
  `BIGINT_a` bigint(20) DEFAULT NULL,
  `FLOAT_a` float DEFAULT NULL,
  `DOUBLE_a` double DEFAULT NULL,
  `DECIMAL_a` decimal(10,0) DEFAULT NULL,
  `VARCHAR_a` varchar(500) DEFAULT NULL,
  `CHAR_a` char(100) DEFAULT NULL,
  `TEXT_a` text,
  `TINYTEXT_a` tinytext,
  `MEDIUMTEXT_a` mediumtext,
  `LONGTEXT_a` longtext,
  `BINARY_a` binary(100) DEFAULT NULL,
  `VARBINARY_a` varbinary(500) DEFAULT NULL,
  `BLOB_a` blob,
  `TIMESTAMP_a` timestamp NULL DEFAULT NULL,
  `DATE_a` date DEFAULT NULL,
  `TIME_a` time DEFAULT NULL,
  `DATETIME_a` datetime DEFAULT NULL,
  `ENUM_a` enum('男','女','未知') DEFAULT NULL,
  `JSON_a` json DEFAULT NULL,
  `bit_1` bit(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) 

CREATE TABLE `template_target_dz1` (
  `id` int(11) NOT NULL ,
  `TINYINT_a` tinyint(4) DEFAULT NULL,
  `SMALLINT_a` smallint(6) DEFAULT NULL,
  `INTEGER_a` int(11) DEFAULT NULL,
  `MEDIUMINT_a` mediumint(9) DEFAULT NULL,
  `BIGINT_a` bigint(20) DEFAULT NULL,
  `FLOAT_a` float DEFAULT NULL,
  `DOUBLE_a` double DEFAULT NULL,
  `DECIMAL_a` decimal(10,0) DEFAULT NULL,
  `VARCHAR_a` varchar(500) DEFAULT NULL,
  `CHAR_a` char(100) DEFAULT NULL,
  `TEXT_a` text,
  `TINYTEXT_a` tinytext,
  `MEDIUMTEXT_a` mediumtext,
  `LONGTEXT_a` longtext,
  `BINARY_a` binary(100) DEFAULT NULL,
  `VARBINARY_a` varbinary(500) DEFAULT NULL,
  `BLOB_a` blob,
  `TIMESTAMP_a` timestamp NULL DEFAULT NULL,
  `DATE_a` date DEFAULT NULL,
  `TIME_a` time DEFAULT NULL,
  `DATETIME_a` datetime DEFAULT NULL,
  `ENUM_a` enum('男','女','未知') DEFAULT NULL,
  `JSON_a` json DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

## 1.3. 创建序列

```lua
CREATE SEQUENCE "seq_tb2" MINVALUE 1 INCREMENT BY 1 NoMaxValue START WITH 1;

select "seq_1000w2".nextval;
```

## 1.4. 创建存储过程

```lua
delimiter $$
drop PROCEDURE if exists szgt_source.batch_c_d $$
CREATE PROCEDURE szgt_source.batch_c_d(in i_num int)
begin
    DECLARE i int default 1;
   
--  雪花id
   DECLARE b_current_time BIGINT;
	DECLARE b_time_tick BIGINT;
	DECLARE i_work_id INT;
	DECLARE i_work_id_big_length INT;
	DECLARE i_seq_big_length INT;
	DECLARE f_random FLOAT;
	DECLARE b_res BIGINT;
	
	SET i_work_id = 1;
	SET i_work_id_big_length = 4;
	SET i_seq_big_length = 8;
	

	
	while i <= i_num do 
	
		SET b_current_time = (REPLACE(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)),'.','')) + 0;
		SET b_time_tick = b_current_time - 1582136402000;
		SET f_random = RAND();
		SET b_res = b_time_tick * POWER(2, i_work_id_big_length + i_seq_big_length) + i_work_id * POWER(2, i_seq_big_length) + (5 + round((POWER(2, i_seq_big_length)-1) * f_random, 0));

		insert
			into
			szgt_source.template_target_e2 (id,
			TINYINT_a,
			SMALLINT_a,
			INTEGER_a,
			MEDIUMINT_a,
			BIGINT_a,
			FLOAT_a,
			DOUBLE_a,
			DECIMAL_a,
			VARCHAR_a,
			CHAR_a,
			TEXT_a,
			TINYTEXT_a,
			MEDIUMTEXT_a,
			LONGTEXT_a,
			BINARY_a,
			VARBINARY_a,
			BLOB_a,
			TIMESTAMP_a,
			DATE_a,
			TIME_a,
			DATETIME_a,
			ENUM_a,
			JSON_a,
			create_time)
		values(b_res,
		11,
		32767,
		2147483647,
		8388607,
		9223372036854775807,
		1.14,
		1.2250738585072014,
		100,
		'测试VARCHAR',
		'测',
		'测试TEXT',
		'测试TINYTEXT',
		'测试MEDIUMTEXT',
		'测试LONGTEXT',
		0x42000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000,
		0x7465737456415242494E415259,
		0x74657374424C4F42,
		'2023-08-13 14:11:28',
		'2023-08-13',
		'14:11:28',
		'2023-08-13 14:11:28',
		'男',
		'{"taskSpecDTO": {"id": "1115227003363229696", "page": {"pageNum": 1, "pageSize": 10}}}',
		'2023-08-13 14:11:28');
	
		delete from szgt_source.template_target_e2 where id = b_res;
	
	set i=i+1;

	end while;
  commit;

END$$

delimiter
```

## 1.5. 其他脚本

```lua
-- 临时设置数据库连接超长时间
SET  global wait_timeout=2000;
SET  global interactive_timeout=2000;
SHOW  global VARIABLES   like '%timeout%';

-- 删除binlog
flush logs;
show binary logs;
purge binary logs to 'mysql-bin.000005';
```

### 1.5.1. shell定时清除binlog

```shell
# 设置MySQL的用户名和密码
USER="dfo"
PASSWORD="wellcom"

# 设置要保留的binlog数量
NUM_BINLOGS_TO_KEEP=2

mysql -u"$USER" -p"$PASSWORD" -e "flush logs;PURGE BINARY LOGS TO '$(mysql -u "$USER" -p"$PASSWORD" -e "SHOW BINARY LOGS;"| tail -n +$((NUM_BINLOGS_TO_KEEP + 1)) | awk '{print $1}' | sed -n '1p')';"
```

# 2. Oracle

## 2.1. 数据类型

## 2.2. 创建表

```lua
CREATE TABLE "TEST"."template_source_o_1" 
   (	"ID" NUMBER(*,0), 
	"INTEGER_A" NUMBER(*,0), 
	"FLOAT_A" FLOAT(126), 
	"BINARY_DOUBLE_A" BINARY_DOUBLE, 
	"BINARY_FLOAT_A" BINARY_FLOAT, 
	"DECIMAL_A" NUMBER(*,0), 
	"VARCHAR_A" VARCHAR2(500), 
	"VARCHAR2_A" VARCHAR2(500), 
	"NCHAR_A" NCHAR(500), 
	"NVARCHAR2_A" NVARCHAR2(500), 
	"LONG_A" LONG, 
	"DATE_A" DATE, 
	"TIMESTAMP_A" TIMESTAMP (6), 
	"CLOB_A" CLOB, 
	"BLOB_A" BLOB,
  CONSTRAINT "NEWTABLE_PK1" PRIMARY KEY ("ID")
   );


CREATE TABLE "TEST"."template_target1" 
   (	"ID" NUMBER(*,0), 
	"INTEGER_A" NUMBER(*,0), 
	"FLOAT_A" FLOAT(126), 
	"BINARY_DOUBLE_A" BINARY_DOUBLE, 
	"BINARY_FLOAT_A" BINARY_FLOAT, 
	"DECIMAL_A" NUMBER(*,0), 
	"VARCHAR_A" VARCHAR2(500), 
	"VARCHAR2_A" VARCHAR2(500), 
	"NCHAR_A" NCHAR(500), 
	"NVARCHAR2_A" NVARCHAR2(500), 
	"LONG_A" LONG, 
	"DATE_A" DATE, 
	"TIMESTAMP_A" TIMESTAMP (6), 
	"CLOB_A" CLOB, 
	"BLOB_A" BLOB
   );
```

## 2.3. 创建序列

```lua
CREATE SEQUENCE "seq_tb2" MINVALUE 1 INCREMENT BY 1 NoMaxValue START WITH 1;

select "seq_1000w2".nextval;
```

## 2.4. 创建存储过程

### 2.4.1. 例子

```lua
CREATE OR REPLACE PROCEDURE ADMIN.insert_sql(
	v_in_num IN NUMBER
) AS 
	vi NUMBER;
	v_id NUMBER;

BEGIN
--	vi := 1 ;
for vi IN 1..v_in_num LOOP
	v_id := "seq_tb2".nextval;
	
INSERT INTO
	"template_source2" (ID,
	INTEGER_A,
	FLOAT_A,
	BINARY_DOUBLE_A,
	BINARY_FLOAT_A,
	DECIMAL_A,
	VARCHAR_A,
	VARCHAR2_A,
	NCHAR_A,
	NVARCHAR2_A,
	LONG_A,
	DATE_A,
	TIMESTAMP_A
--	CLOB_A,
--	BLOB_A
)
VALUES(v_id,
11,
3.33,
4.44444,
5.55555,
11.111,
'测试varchar2字段',
'测试varchar2字段',
'测试nchar字段                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           ',
'测试nvarchar2字段',
3132333132333132,
TIMESTAMP '2023-11-12 06:22:22.000000',
TIMESTAMP '2023-11-12 06:22:22.000000'
--'clob',
--rawtohex('blob')
);
-- UPDATE  DFO."template_source2" SET VARCHAR_A = 'update varchar' WHERE id = v_id;
-- UPDATE  DFO."template_source2" SET VARCHAR2_A = 'update varchar2' WHERE id = v_id;

-- DELETE FROM DFO."template_source2" WHERE id = v_id;
   	IF MOD(vi,200)=0 THEN
   	COMMIT;
   	END IF;
END loop;
  commit;

END insert_sql;
```

### 2.4.2. 新增、编辑、删除同时提交

```lua
CREATE OR REPLACE PROCEDURE insert_sql(
	v_in_num IN NUMBER
) AS 
	vi NUMBER;
	v_id NUMBER;

BEGIN
	vi := 1 ;
for vi IN 1..v_in_num LOOP
	v_id := "seq_tb2".nextval;
	
INSERT INTO
	DFO."template_source2" (ID,
	INTEGER_A,
	FLOAT_A,
	BINARY_DOUBLE_A,
	BINARY_FLOAT_A,
	DECIMAL_A,
	VARCHAR_A,
	VARCHAR2_A,
	NCHAR_A,
	NVARCHAR2_A,
	LONG_A,
	DATE_A,
	TIMESTAMP_A,
	CLOB_A,
	BLOB_A
)
VALUES(v_id,
11,
3.33,
4.44444,
5.55555,
11.111,
'测试varchar2字段',
'测试varchar2字段',
'测试nchar字段                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           ',
'测试nvarchar2字段',
3132333132333132,
TIMESTAMP '2023-11-12 06:22:22.000000',
TIMESTAMP '2023-11-12 06:22:22.000000',
'clob',
rawtohex('blob')
);
-- UPDATE  DFO."template_source2" SET VARCHAR_A = 'update varchar' WHERE id = v_id;
-- UPDATE  DFO."template_source2" SET VARCHAR2_A = 'update varchar2' WHERE id = v_id;

-- DELETE FROM DFO."template_source2" WHERE id = v_id;
   	IF MOD(vi,5000)=0 THEN
   	COMMIT;
   	END IF;
-- vi := vi + 1;
END loop;
  commit;

END insert_sql;
```

### 2.4.3. 批量建表

```lua
GRANT CREATE TABLE TO admin;

CREATE OR REPLACE PROCEDURE ADMIN.create_tab(
    v_in_num IN NUMBER,
    v_in_tabname IN varchar2 DEFAULT 't_source_auto'
) AS
    vi NUMBER;
    v_id NUMBER;
    v_temp_sql varchar2(5000);
    v_sql varchar2(5000);

BEGIN
    --	vi := 1 ;
    v_temp_sql := 'CREATE TABLE  "template_source_o"
   (	"ID" NUMBER(*,0),
	"INTEGER_A" NUMBER(*,0),
	"FLOAT_A" FLOAT(126),
	"BINARY_DOUBLE_A" BINARY_DOUBLE,
	"BINARY_FLOAT_A" BINARY_FLOAT,
	"DECIMAL_A" NUMBER(*,0),
	"VARCHAR_A" VARCHAR2(500),
	"VARCHAR2_A" VARCHAR2(500),
	"NCHAR_A" NCHAR(500),
	"NVARCHAR2_A" NVARCHAR2(500),
	"LONG_A" LONG,
	"DATE_A" DATE,
	"TIMESTAMP_A" TIMESTAMP (6),
	"CLOB_A" CLOB,
	"BLOB_A" BLOB,
  CONSTRAINT "template_source_o_PK" PRIMARY KEY ("ID")
   )';
    for vi IN 1..v_in_num LOOP
            v_sql:=replace(v_temp_sql,'template_source_o', v_in_tabname||vi);
            DBMS_OUTPUT.PUT_LINE(v_sql);
            execute immediate v_sql;
            commit;
        END loop;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END create_tab;
```

### 2.4.4. 批量建表大量字段

```lua
GRANT CREATE TABLE TO admin;
CREATE OR REPLACE PROCEDURE ADMIN.create_tab_field(
    v_in_num IN NUMBER,
    v_in_tabname IN varchar2 DEFAULT 't_source_auto',
    v_in_field_num IN NUMBER
) AS
    vi NUMBER;
    vj NUMBER;
    v_id NUMBER;
    v_temp_sql varchar2(5000);
    v_sql varchar2(5000);
    v_field_sql varchar2(5000) DEFAULT '';

BEGIN
    --	vi := 1 ;
    v_temp_sql := 'CREATE TABLE  "t_source_auto"
   (
	"ID" NUMBER(32,0) NOT NULL ENABLE, 
	"XM" VARCHAR2(64), 
	"ZJHM" VARCHAR2(64), 
	"YLJGMC" VARCHAR2(64), 
	"MJZH" VARCHAR2(64), 
	"YWSJ" VARCHAR2(64), 
	"SJH" VARCHAR2(64), 
	"JBMC" VARCHAR2(128), 
	"JZD" VARCHAR2(64), 
	"XB" VARCHAR2(64), 
	"KS" VARCHAR2(64), 
	"AGE" NUMBER(10,0), 
	"WS02_01_039_001" VARCHAR2(64), 
	"CT02_01_040_01" VARCHAR2(100), 
	"WS02_01_026_01" VARCHAR2(100), 
	"WS02_01_030_01" VARCHAR2(100), 
	"WS02_01_010_02" VARCHAR2(100), 
	"WS02_01_901_11" VARCHAR2(100), 
	"WS01_00_010_01" VARCHAR2(100), 
	{field_auto}
  CONSTRAINT "t_source_auto_PK" PRIMARY KEY ("ID")
   )';
  IF  v_in_field_num > 0 then
	for vj IN 1..v_in_field_num LOOP
		v_field_sql:='"AT_FIELD'|| vj||'"' ||' varchar2(1000) ' ||','||  v_field_sql;
	END loop;
  END IF;
 
   v_temp_sql:=replace(v_temp_sql,'{field_auto}', v_field_sql);
  
    for vi IN 1..v_in_num LOOP
            v_sql:=replace(v_temp_sql,'t_source_auto', v_in_tabname||vi);
            DBMS_OUTPUT.PUT_LINE(v_sql);
            execute immediate v_sql;
            commit;
        END loop;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END create_tab_field;
```

## 2.5. 其他

### 2.5.1. 查询大量表数量

```sql
select table_name, count_rows(table_name) nrows
from user_tables where table_name like 't_pressure_%' order by nrows desc;
```

### 2.5.2. 查锁表

```lua
SELECT DISTINCT 'alter system kill session ''' || s.sid || ',' || s.serial# || ',@' ||
                s.inst_id || ''' immediate;' AS kill_session_scripts
               ,s.sql_id
               ,a.sql_text
               ,s.sid
               ,s.serial#
  FROM dba_ddl_locks l
      ,gv$session    s
      ,gv$sqlarea     a
 WHERE 1 = 1
   AND l.session_id = s.sid
   AND s.sql_id = a.sql_id
   AND lower(a.sql_text) NOT LIKE '%alter system kill session %'
-- AND l.owner IN ('TZQ','LOG')
;


|KILL_SESSION_SCRIPTS                               |SQL_ID       |SQL_TEXT                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |SID  |SERIAL#|
|---------------------------------------------------|-------------|--------------------------------------------------|-----|-------|
|alter system kill session '2274,1259,@1' immediate;|5rsm4y10jd4p2|BEGIN DBMS_OUTPUT.GET_LINE(:1 , :2 ); END;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |2,274|1,259  |
```

# 3. SQLServer

## 3.1. 数据类型

## 3.2. 创建表

```lua
CREATE TABLE TEST."template_source1" (
	ID NUMBER(38,0) NOT NULL,
	INTEGER_A NUMBER(38,0) NOT NULL,
	FLOAT_A FLOAT NOT NULL,
	BINARY_DOUBLE_A BINARY_DOUBLE NULL,
	BINARY_FLOAT_A BINARY_FLOAT NULL,
	DECIMAL_A NUMBER(38,0) NULL,
	VARCHAR_A VARCHAR2(500) NULL,
	VARCHAR2_A VARCHAR2(500) NULL,
	NCHAR_A NCHAR(500) NULL,
	NVARCHAR2_A NVARCHAR2(500) NULL,
	LONG_A LONG NULL,
	DATE_A DATE NULL,
	TIMESTAMP_A TIMESTAMP NULL,
	CLOB_A CLOB NULL,
	BLOB_A BLOB NULL,
	 CONSTRAINT "NEWTABLE_PK1" PRIMARY KEY ("ID")
);
```



# 4. 达梦

## 4.1. 数据类型

## 4.2. 创建表

```lua
CREATE TABLE template_source1 (
	ID number(32) NOT NULL,
	INT_A INT,
	INTEGER_A INTEGER,
	BIGINT_A BIGINT,
	TINYINT_A TINYINT,
	BYTE_A BYTE,
	SMALLINT_A SMALLINT,
	BINARY_A BINARY(100),
	RAW_A VARBINARY(8188),
	FLOAT_A FLOAT,
	DOUBLE_ DOUBLE,
	REAL_A REAL,
	DOUBLE_PRECISION DOUBLE PRECISION,
	VARBINARY_A VARBINARY(8188),
	NUMERIC_A NUMERIC(10,2),
	DECIMAL_A NUMERIC(10,2),
	DEC_A DEC,
	NUMBER_A NUMBER(10,2),
	CHAR_A CHAR(100),
	CHARACTER_A CHARACTER(100),
	VARCHAR_A VARCHAR(8188),
	VARCHAR2_A VARCHAR2(8188),
	BIT_A BIT,
	DATE_A DATE,
	TIME_A TIME,
	TIMESTAMP_A TIMESTAMP,
	INTERVAL_YEAR_TO_MONTH INTERVAL YEAR TO MONTH,
	INTERVAL_DAY_TO_MINUTE INTERVAL DAY TO MINUTE,
	TEXT_A TEXT,
	BLOB_A BLOB,
	CLOB_A CLOB,
	IMAGE_A IMAGE,
	BFILE_A BFILE,
	CONSTRAINT TEMPLATE_source2_PK PRIMARY KEY (ID)
);

CREATE TABLE template_target1 (
	ID number(32) NOT NULL,
	INT_A INT,
	INTEGER_A INTEGER,
	BIGINT_A BIGINT,
	TINYINT_A TINYINT,
	BYTE_A BYTE,
	SMALLINT_A SMALLINT,
	BINARY_A BINARY(100),
	RAW_A VARBINARY(8188),
	FLOAT_A FLOAT,
	DOUBLE_ DOUBLE,
	REAL_A REAL,
	DOUBLE_PRECISION DOUBLE PRECISION,
	VARBINARY_A VARBINARY(8188),
	NUMERIC_A NUMERIC(10,2),
	DECIMAL_A NUMERIC(10,2),
	DEC_A DEC,
	NUMBER_A NUMBER(10,2),
	CHAR_A CHAR(100),
	CHARACTER_A CHARACTER(100),
	VARCHAR_A VARCHAR(8188),
	VARCHAR2_A VARCHAR2(8188),
	BIT_A BIT,
	DATE_A DATE,
	TIME_A TIME,
	TIMESTAMP_A TIMESTAMP,
	INTERVAL_YEAR_TO_MONTH INTERVAL YEAR TO MONTH,
	INTERVAL_DAY_TO_MINUTE INTERVAL DAY TO MINUTE,
	TEXT_A TEXT,
	BLOB_A BLOB,
	CLOB_A CLOB,
	IMAGE_A IMAGE,
	BFILE_A BFILE,
	CONSTRAINT TEMPLATE_TARGET2_PK PRIMARY KEY (ID)
);
```

## 4.3. 创建序列

```lua
CREATE SEQUENCE "seq_tb2" MINVALUE 1 INCREMENT BY 1 NoMaxValue START WITH 1;

select "seq_1000w2".nextval;
```

## 4.4. 创建存储过程

dbeaver执行一定要选中全部执行！

```lua
CREATE OR replace PROCEDURE batch_insert(num IN  INT) AS
	vi INT:=1;
	v_temp varchar(10);
BEGIN
 	FOR v_i IN 1..num loop
 		INSERT INTO TEMPLATE_SOURCE1 (ID) VALUES (v_i);
 	
-- 	500条提交一次
 		IF (MOD(v_i,500) == 0) THEN
 		COMMIT;
 		END IF;
 	end loop;
 	COMMIT;
END;
---------------------------------------------------------------------------------------------

CREATE OR replace PROCEDURE RESOURCE_SZGT_PROCEDURE(num IN  INT) AS
	vi INT:=1;
	v_temp varchar(10);
BEGIN
SET IDENTITY_INSERT SYSDBA.RESOURCE_SZGT ON;
FOR v_i IN 1..num loop
INSERT INTO SYSDBA.RESOURCE_SZGT (NAME, CODE, RESOURCE_TYPE, PARENT_CODE, CODE_PATH, NAME_PATH, EXTERNAL_LINK,
                                  EXTERNAL_LINK_TYPE, EXTERNAL_RESOURCE_ID, LINE_RULE, COLUMN_RULE, API_URL, ORDER_NO,
                                  DESCRIPTION, ICON, BUILT_IN_FLAG, APP_CODE, TENANT_ID, ENV_FLAG, STATUS,
                                  RESOURCE_EXPAND_MESSAGE, DELETE_FLAG, CREATOR, UPDATOR, CREAT_AT, UPDATE_AT)
VALUES (
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           1,
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 2),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           DBMS_RANDOM.RANDOM_STRING('x', 10),
           '2024-05-08 17:53:35',
           '2024-05-08 17:53:35');

end loop;
END;
```

### 4.4.1. scn查询

```lua
-- 支持参数组合
-- call SCN_SELECT3(20606161, 20606172 , 'LOG_MINING_FLUSH,RESOURCE_SZGT');

CREATE OR replace PROCEDURE SCN_SELECT3(vi_scn_start IN INT, 
                                       vi_scn_end IN INT,
                                       vi_table_names in VARCHAR(50)) 

AS
    v_path varchar(255);
    v_sql_str varchar2;
    v_sql_str2 varchar2;
    v_t_names VARCHAR2 DEFAULT vi_table_names;

    v_msg varchar(255);
    -- 1.查询有哪些归档日志
    CURSOR c_log_paths IS
        SELECT name FROM V$ARCHIVED_LOG ;

BEGIN
    OPEN c_log_paths;
    loop
        FETCH c_log_paths INTO v_path;
        EXIT WHEN c_log_paths%NOTFOUND;
    
        -- 2.logminer加入日志文件
        dbms_logmnr.add_logfile(v_path);
        
        PRINT v_path;
        v_path:=null;
    end loop;
    CLOSE c_log_paths;
      -- 3.开启归档日志
    sys.dbms_logmnr.start_logmnr(Options=>2128 );
    
    -- 4.基于oracle的 进行查询达梦的redolog日志
   v_sql_str := 'SELECT SCN,
           SQL_REDO,
           OPERATION_CODE,
           "TIMESTAMP",
           XID,
           CSF,
           TABLE_NAME SEG_OWNER,
           OPERATION,
           USERNAME,
           ROW_ID,
           "ROLL_BACK",
           RS_ID,
           STATUS,
           INFO,
           SSN,
           THREAD#
      FROM V$LOGMNR_CONTENTS
     WHERE';
     v_sql_str2 :=	'(SEG_OWNER IS NULL 
               OR SEG_OWNER NOT IN (''APPQOSSYS'', 
                                    ''AUDSYS'', 
                                    ''CTXSYS'', 
                                    ''DVSYS'', 
                                    ''DBSFWUSER'', 
                                    ''DBSNMP'', 
                                    ''GSMADMIN_INTERNAL'',
                                    ''LBACSYS'', 
                                    ''MDSYS'', 
                                    ''OJVMSYS'', 
                                    ''OLAPSYS'', 
                                    ''ORDDATA'', 
                                    ''ORDSYS'', 
                                    ''OUTLN'', 
                                    ''SYS'', 
                                    ''SYSTEM'',
                                    ''WMSYS'', 
                                    ''XDB''))
       AND OPERATION_CODE IN (1, 
                              2, 
                              3, 
                              5, 
                              6, 
                              7, 
                              9, 
                              10, 
                              11, 
                              29, 
                              34, 
                              36, 
                              255)';
                              
   -- SCN > vi_scn_start AND SCN <= vi_scn_end  
   IF vi_scn_start is NOT NULL THEN
   	v_sql_str:= v_sql_str || ' SCN > ' || vi_scn_start ||' AND ';
   END IF;
   
   IF vi_scn_end is NOT NULL THEN
   	v_sql_str:= v_sql_str || ' SCN <= ' || vi_scn_end || ' AND ';
   END IF;	
   
   IF vi_table_names is NOT NULL THEN
   	-- 处理多个表         
   	v_t_names := REPLACE (vi_table_names, ',', ''',''');
   	v_sql_str:= v_sql_str || ' TABLE_NAME in ('''|| v_t_names || ''') AND ';
   	
   END IF;
   
   v_sql_str := v_sql_str || v_sql_str2;
   -- PRINT 'sql:' || v_sql_str;                           
   EXECUTE IMMEDIATE v_sql_str;
   --  5.终止归档日志文件分析；
   DBMS_LOGMNR.END_LOGMNR();
                        
   EXCEPTION
		WHEN OTHERS THEN 
			PRINT 'error:' || SQLERRM;
			PRINT 'sql:' || v_sql_str;
			
			DBMS_OUTPUT.PUT_LINE('error:' || SQLERRM);
			DBMS_OUTPUT.PUT_LINE('sql:' || v_sql_str);
			DBMS_LOGMNR.END_LOGMNR();
			
 
END;
```

### 4.4.2. scn查询2

```shell
-- 支持参数组合
-- call SCN_SELECT3(2024-05-14 00:00:00, 20606161, 20606172 , 'LOG_MINING_FLUSH,RESOURCE_SZGT');

CREATE OR replace PROCEDURE SCN_SELECT4(vi_log_start IN date DEFAULT (sysdate() - 7), 
										vi_scn_start IN INT, 
                                       vi_scn_end IN INT,
                                       vi_table_names in VARCHAR(50)) 

AS
    v_path varchar(255);
    v_sql_str varchar2;
    v_sql_str2 varchar2;
    v_t_names VARCHAR2 DEFAULT vi_table_names;
    v_msg varchar(255);
    -- 1.查询有哪些归档日志
    CURSOR c_log_paths IS
        -- SELECT name FROM V$ARCHIVED_LOG ;
        SELECT name FROM V$ARCHIVED_LOG where FIRST_TIME >= vi_log_start;

BEGIN
	
    OPEN c_log_paths;
    loop
        FETCH c_log_paths INTO v_path;
        EXIT WHEN c_log_paths%NOTFOUND;
    
        -- 2.logminer加入日志文件
        dbms_logmnr.add_logfile(v_path);
        
        PRINT v_path;
        v_path:=null;
    end loop;
    CLOSE c_log_paths;
      -- 3.开启归档日志
    sys.dbms_logmnr.start_logmnr(Options=>2128,STARTTIME=> TO_DATE(vi_log_start,'YYYY-MM-DD HH24:MI:SS'));
    
    -- 4.基于oracle的 进行查询达梦的redolog日志
   v_sql_str := 'SELECT SCN,
           SQL_REDO,
           OPERATION_CODE,
           "TIMESTAMP",
           XID,
           CSF,
           TABLE_NAME SEG_OWNER,
           OPERATION,
           USERNAME,
           ROW_ID,
           "ROLL_BACK",
           RS_ID,
           STATUS,
           INFO,
           SSN,
           THREAD#
      FROM V$LOGMNR_CONTENTS
     WHERE';
     v_sql_str2 :=	'(SEG_OWNER IS NULL 
               OR SEG_OWNER NOT IN (''APPQOSSYS'', 
                                    ''AUDSYS'', 
                                    ''CTXSYS'', 
                                    ''DVSYS'', 
                                    ''DBSFWUSER'', 
                                    ''DBSNMP'', 
                                    ''GSMADMIN_INTERNAL'',
                                    ''LBACSYS'', 
                                    ''MDSYS'', 
                                    ''OJVMSYS'', 
                                    ''OLAPSYS'', 
                                    ''ORDDATA'', 
                                    ''ORDSYS'', 
                                    ''OUTLN'', 
                                    ''SYS'', 
                                    ''SYSTEM'',
                                    ''WMSYS'', 
                                    ''XDB''))
       AND OPERATION_CODE IN (1, 
                              2, 
                              3, 
                              5, 
                              6, 
                              7, 
                              9, 
                              10, 
                              11, 
                              29, 
                              34, 
                              36, 
                              255)';
                              
   -- SCN > vi_scn_start AND SCN <= vi_scn_end  
   IF vi_scn_start is NOT NULL THEN
   	v_sql_str:= v_sql_str || ' SCN > ' || vi_scn_start ||' AND ';
   END IF;
   
   IF vi_scn_end is NOT NULL THEN
   	v_sql_str:= v_sql_str || ' SCN <= ' || vi_scn_end || ' AND ';
   END IF;	
   
   IF vi_table_names is NOT NULL THEN
   	-- 处理多个表         
   	v_t_names := REPLACE (vi_table_names, ',', ''',''');
   	v_sql_str:= v_sql_str || ' TABLE_NAME in ('''|| v_t_names || ''') AND ';
   	
   END IF;
   
   v_sql_str := v_sql_str || v_sql_str2;
   -- PRINT 'sql:' || v_sql_str;                           
   EXECUTE IMMEDIATE v_sql_str;
   --  5.终止归档日志文件分析；
   DBMS_LOGMNR.END_LOGMNR();
                     
   EXCEPTION
		WHEN OTHERS THEN 
	--		PRINT 'sql:' || v_sql_str;
	--		PRINT 'error:' || SQLERRM;
						
	--		DBMS_OUTPUT.PUT_LINE('sql:' || v_sql_str);
			DBMS_OUTPUT.PUT_LINE('error:' || SQLERRM);
			DBMS_LOGMNR.END_LOGMNR();
			
 
END; 
```

### 4.4.3. 插入数据

```shell
CREATE OR REPLACE PROCEDURE "BATCH_IUD"(IN_DATA_NUM IN INT)
AUTHID DEFINER
 AS
	--vi INT:=1;
	v_temp varchar(10);
	v_id bigint;


BEGIN
	v_id := SEQ_BATCH_IUD.nextval;
		INSERT INTO TEMPLATE_SOURCE_DM32 (ID, 
	       CHAR_NAME, 
	       CHARACTER_NAME, 
	       VARCHAR_NAME, 
	       VARCHAR2_NAME, 
	       NUMERIC_NAME, 
	       DECIMAL_NAME, 
	       DEC_NAME, 
	       NUMBER_NAME, 
	       INTEGER_NAME, 
	       INT_NAME, 
	       BIGINT_NAME, 
	       TINYINT_NAME, 
	       BYTE_NAME, 
	       SMALLINT_NAME, 
	       VARBINARY_NAME, 
	       RAW_NAME, 
	       FLOAT_NAME, 
	       DOUBLE_NAME, 
	       REAL_NAME, 
	       DOUBLE_PRECISION_NAME, 
	       BIT_NAME, 
	       DATE_NAME, 
	       TIME_NAME, 
	       TEXT_NAME)
	       VALUES(v_id, 
	       '1nvhme', 
	       NULL, 
	       'qfqhkov2kzw1w8fuintfqdj1tixldw03sp5z64eve7fj37dojoio6a15dmlr3vux2u07jvpra8oszpns3ez1ytq8f4ukd5vhwwwwnlifvbu606ky', 
	       's2n0lbg25dbgmutn3sn03ceqf04whs077e0798zykqn1yrsmhqdlujbyhplu9b456s8v', 
	       NULL, 
	       100.2, 
	       NULL, 
	       NULL, 
	       1959754112, 
	       1662080802, 
	       -9206966268696905219, 
	       57, 
	       NULL, 
	       3874, 
	       0x7465737456415242494E415259, 
	       0x746573, 
	       NULL, 
	       6.250123123E100, 
	       NULL, 
	       NULL, 
	       1, 
	       '2024-05-29', 
	       '14:11:28', 
	       '');
 	
	for vi in 1.. IN_DATA_NUM LOOP
		UPDATE TEMPLATE_SOURCE_DM32 set VARCHAR2_NAME = vi WHERE id = v_id;
	
		PRINT vi;
	end loop;
	COMMIT;
END;
```

### 4.4.4. 增删改

```shell
-- 1.创建表
DROP TABLE IF EXISTS template_source_dm1;
CREATE TABLE template_source_dm1 (
	id BIGINT  NOT NULL,
	INT_A INT,
	INTEGER_A INTEGER,
	BIGINT_A BIGINT,
	TINYINT_A TINYINT,
	BYTE_A BYTE,
	SMALLINT_A SMALLINT,
	BINARY_A BINARY(100),
	RAW_A VARBINARY,
	FLOAT_A FLOAT,
	DOUBLE_ DOUBLE,
	REAL_A REAL,
	DOUBLE_PRECISION DOUBLE PRECISION,
	VARBINARY_A VARBINARY,
	NUMERIC_A NUMERIC(10,2),
	DECIMAL_A NUMERIC(10,2),
	DEC_A DEC,
	NUMBER_A NUMBER(10,2),
	CHAR_A CHAR(100),
	CHARACTER_A CHARACTER(100),
	VARCHAR_A VARCHAR,
	VARCHAR2_A VARCHAR2,
	BIT_A BIT,
	DATE_A DATE,
	TIME_A TIME,
	TIMESTAMP_A TIMESTAMP,
	long_a long,
	TEXT_A TEXT,
	BLOB_A BLOB,
	CLOB_A CLOB,
	IMAGE_A IMAGE,
	BFILE_A BFILE,
	CONSTRAINT template_source_dm1_PK PRIMARY KEY (ID)
);

-- 2.创建序列号
CREATE SEQUENCE IF NOT EXISTS SEQ_BATCH_IUD MINVALUE 1 INCREMENT BY 1 NoMaxValue START WITH 1;

-- 3.创建存储过程
CREATE OR REPLACE PROCEDURE BATCH_IUD(IN_DATA_NUM IN INT)
AUTHID DEFINER
 AS
	v_temp varchar(10);
	v_id bigint;
	v_id2 bigint;

BEGIN
		
	for vi in 1.. IN_DATA_NUM LOOP
		v_id := SEQ_BATCH_IUD.nextval;
		v_id2 := SEQ_BATCH_IUD.nextval;
		insert into TEMPLATE_SOURCE_DM21 (ID, 
       INT_A, 
       INTEGER_A, 
       BIGINT_A, 
       TINYINT_A, 
       BYTE_A, 
       SMALLINT_A, 
       BINARY_A, 
       RAW_A, 
       FLOAT_A, 
       DOUBLE_, 
       REAL_A, 
       DOUBLE_PRECISION, 
       VARBINARY_A, 
       NUMERIC_A, 
       DECIMAL_A, 
       DEC_A, 
       NUMBER_A, 
       CHAR_A, 
       CHARACTER_A, 
       VARCHAR_A, 
       VARCHAR2_A, 
       BIT_A, 
       DATE_A, 
       TIME_A, 
       TIMESTAMP_A, 
       LONG_A, 
       TEXT_A, 
       BLOB_A, 
       CLOB_A, 
       IMAGE_A, 
       BFILE_A) 
       values (v_id, 
       1, 
       2, 
       3333333333333, 
       4, 
       6, 
       7, 
       0xB2E2CAD462696E617279000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 
       0xB2E2CAD4726177, 
       8.888, 
       9.99999, 
       1.111, 
       2.22222, 
       0xB2E2CAD476617262696E617279, 
       333.33, 
       4.44, 
       5.555555, 
       6.66, 
       '测试char'||v_id, 
       '测试character'||v_id, 
       '测试varchar'||v_id, 
       '测试varchar2'||v_id, 
       1, 
       '2024-05-31', 
       '10:24:09', 
       '2024-05-31 10:24:04.948000', 
       v_id||'测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob', 
       v_id||'测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text', 
       0xFFD8FFE000104A46494600010101009000900000FFDB0043000302020302020303030304030304050805050404050A070706080C0A0C0C0B0A0B0B0D0E12100D0E110E0B0B1016101113141515150C0F171816141812141514FFDB00430103040405040509050509140D0B0D1414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414FFC00011080037003E03012200021101031101FFC4001F0000010501010101010100000000000000000102030405060708090A0BFFC400B5100002010303020403050504040000017D01020300041105122131410613516107227114328191A1082342B1C11552D1F02433627282090A161718191A25262728292A3435363738393A434445464748494A535455565758595A636465666768696A737475767778797A838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE1E2E3E4E5E6E7E8E9EAF1F2F3F4F5F6F7F8F9FAFFC4001F0100030101010101010101010000000000000102030405060708090A0BFFC400B51100020102040403040705040400010277000102031104052131061241510761711322328108144291A1B1C109233352F0156272D10A162434E125F11718191A262728292A35363738393A434445464748494A535455565758595A636465666768696A737475767778797A82838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE2E3E4E5E6E7E8E9EAF2F3F4F5F6F7F8F9FAFFDA000C03010002110311003F00F99E8A2B5B41D252FA479EE1643670901963FBD2B9FBB1AFB9C1E7B004F6AFD92A4E34E2E52D91F0D18B93B22BE9FA35D6A48F246AA96F19C3DC4CC12353E858F7F61CFB54D7969A3695B45F6B0EAEDD05B5A1607E9BD909FCABD0D34E5B7B36B894446EA38CF931AAFEEADF8E88BFD4F27A9AF9D6E6D751F1C6A8E7CC4F38C84890F1F857C1E3388E71972D05A77FEB7FC0FA8C264EAA2BD467A459E9DA5EAF196D3B57DE40CE2EAD9A3FD54B81F8E0555D434BBAD2E445B98B6071B92452191C7AAB0E08FA1A3C1FF0B75AD1EDC5CA5F4724711F31A15272F81D2BD574AD3E0F13786D277B4F2CCC0F996EE301C8E33FECB7A30E7D7238A583E249CA7C9885A77162B298C17352678FD15A5E20D15F43D41A03B9A161BA291860B2E48E476208208F506B36BEFA138D48A945E8CF98945C5D985779A1DB8B5B7B18F1FEAA1FB437BC92F43F84613FEFA3EB5C1D7A2D9B868E361FC56D6C47D0408BFCD5ABE773FA92A7847CBD7FE1BF26CF4F2D8A956572CCD3B32B73938AF0BFB1EA1637C2EED0A42237CB8CF7CF4AF6C91C293935E23E24D5A16F10DCDA5BDD2341E7EF0636E339CE33F5AFCAE49EE7DFE15AE67167BBF8661D62DF50313BC52E9AD12B86FE30C4722BB8D3E110D9C48176003EED79D781F5859268165B84F3278D531B8F2DF8F7C57A6A74AAA366AE8E4C45D4ACCE13E29692926962E947CF13893A763856FD7CBFC8FAD795D7B5FC479923F08DF230F9E454553FF6D509FE55E295FAAE435253C1FBDD1B5F91F1398C546BE9D5057476FAF3DA78625B98E16B99F4E4224850FCCD096DC187FBACCC0FB38F4AE72AC69FA84DA5DDA5C5BB6D917239190C08C1523B823823DEBD4C6E1638CA2E948E4C3D6742A29A38CF137C50D5EE034491C76B6AC02492C64390586719CFA1AE8747F843E59B6F38ACF633C6B324A87D403D6A1D67E1BD8788B545D4744912D246915E7D26670B9C0C1F259BE523FD96E7EB5EC5A269ED6BE1BD2F4DB83E4DFC76CA0C520DAC0E3EEFE19C7E15F9563B05570CB924AC7DBE131719BE64F73CBFE23B0F07F85EC86887ECB710DCA4BE6EECB965E8327AF5E956B44F8FFA969BF66B5BF8EDEE0CB284F35DFE741D08603D0D5AF8AFE05BFD7345B0B5B5B77B9BF374AD22C20B7951F39248E83DCD72BA078074EF0ECFF6ED5C45AA6AAAE5E2B55904B04473C348C38723AED191EA7AA9ACBF055710B9211B938CC4D3A6F99B3D2BC75E2E6D6B4BD3ADCC7E4C922ADC4D186C85E0EC1D3B825BE856B89A92E2E25BBB892699DA596462CEEC72493D4D475FAAE130D1C2518D28F43E26B5575AA39B0A28A2BB0C02AF5A6BBA969E812D750BAB65FEEC33328FD0D1454CA3192B495C69B5AA1979ABDF6A231777B71743FE9B4ACFFCCD54A28A23151568AB036DEAC28A28AA11FFD9 
       , 
       '测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob', 
       0xFFD8FFE000104A46494600010101009000900000FFDB0043000302020302020303030304030304050805050404050A070706080C0A0C0C0B0A0B0B0D0E12100D0E110E0B0B1016101113141515150C0F171816141812141514FFDB00430103040405040509050509140D0B0D1414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414FFC00011080037003E03012200021101031101FFC4001F0000010501010101010100000000000000000102030405060708090A0BFFC400B5100002010303020403050504040000017D01020300041105122131410613516107227114328191A1082342B1C11552D1F02433627282090A161718191A25262728292A3435363738393A434445464748494A535455565758595A636465666768696A737475767778797A838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE1E2E3E4E5E6E7E8E9EAF1F2F3F4F5F6F7F8F9FAFFC4001F0100030101010101010101010000000000000102030405060708090A0BFFC400B51100020102040403040705040400010277000102031104052131061241510761711322328108144291A1B1C109233352F0156272D10A162434E125F11718191A262728292A35363738393A434445464748494A535455565758595A636465666768696A737475767778797A82838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE2E3E4E5E6E7E8E9EAF2F3F4F5F6F7F8F9FAFFDA000C03010002110311003F00F99E8A2B5B41D252FA479EE1643670901963FBD2B9FBB1AFB9C1E7B004F6AFD92A4E34E2E52D91F0D18B93B22BE9FA35D6A48F246AA96F19C3DC4CC12353E858F7F61CFB54D7969A3695B45F6B0EAEDD05B5A1607E9BD909FCABD0D34E5B7B36B894446EA38CF931AAFEEADF8E88BFD4F27A9AF9D6E6D751F1C6A8E7CC4F38C84890F1F857C1E3388E71972D05A77FEB7FC0FA8C264EAA2BD467A459E9DA5EAF196D3B57DE40CE2EAD9A3FD54B81F8E0555D434BBAD2E445B98B6071B92452191C7AAB0E08FA1A3C1FF0B75AD1EDC5CA5F4724711F31A15272F81D2BD574AD3E0F13786D277B4F2CCC0F996EE301C8E33FECB7A30E7D7238A583E249CA7C9885A77162B298C17352678FD15A5E20D15F43D41A03B9A161BA291860B2E48E476208208F506B36BEFA138D48A945E8CF98945C5D985779A1DB8B5B7B18F1FEAA1FB437BC92F43F84613FEFA3EB5C1D7A2D9B868E361FC56D6C47D0408BFCD5ABE773FA92A7847CBD7FE1BF26CF4F2D8A956572CCD3B32B73938AF0BFB1EA1637C2EED0A42237CB8CF7CF4AF6C91C293935E23E24D5A16F10DCDA5BDD2341E7EF0636E339CE33F5AFCAE49EE7DFE15AE67167BBF8661D62DF50313BC52E9AD12B86FE30C4722BB8D3E110D9C48176003EED79D781F5859268165B84F3278D531B8F2DF8F7C57A6A74AAA366AE8E4C45D4ACCE13E29692926962E947CF13893A763856FD7CBFC8FAD795D7B5FC479923F08DF230F9E454553FF6D509FE55E295FAAE435253C1FBDD1B5F91F1398C546BE9D5057476FAF3DA78625B98E16B99F4E4224850FCCD096DC187FBACCC0FB38F4AE72AC69FA84DA5DDA5C5BB6D917239190C08C1523B823823DEBD4C6E1638CA2E948E4C3D6742A29A38CF137C50D5EE034491C76B6AC02492C64390586719CFA1AE8747F843E59B6F38ACF633C6B324A87D403D6A1D67E1BD8788B545D4744912D246915E7D26670B9C0C1F259BE523FD96E7EB5EC5A269ED6BE1BD2F4DB83E4DFC76CA0C520DAC0E3EEFE19C7E15F9563B05570CB924AC7DBE131719BE64F73CBFE23B0F07F85EC86887ECB710DCA4BE6EECB965E8327AF5E956B44F8FFA969BF66B5BF8EDEE0CB284F35DFE741D08603D0D5AF8AFE05BFD7345B0B5B5B77B9BF374AD22C20B7951F39248E83DCD72BA078074EF0ECFF6ED5C45AA6AAAE5E2B55904B04473C348C38723AED191EA7AA9ACBF055710B9211B938CC4D3A6F99B3D2BC75E2E6D6B4BD3ADCC7E4C922ADC4D186C85E0EC1D3B825BE856B89A92E2E25BBB892699DA596462CEEC72493D4D475FAAE130D1C2518D28F43E26B5575AA39B0A28A2BB0C02AF5A6BBA969E812D750BAB65FEEC33328FD0D1454CA3192B495C69B5AA1979ABDF6A231777B71743FE9B4ACFFCCD54A28A23151568AB036DEAC28A28AA11FFD9 
       , 
       null);
      
      insert into TEMPLATE_SOURCE_DM21 (ID, 
       INT_A, 
       INTEGER_A, 
       BIGINT_A, 
       TINYINT_A, 
       BYTE_A, 
       SMALLINT_A, 
       BINARY_A, 
       RAW_A, 
       FLOAT_A, 
       DOUBLE_, 
       REAL_A, 
       DOUBLE_PRECISION, 
       VARBINARY_A, 
       NUMERIC_A, 
       DECIMAL_A, 
       DEC_A, 
       NUMBER_A, 
       CHAR_A, 
       CHARACTER_A, 
       VARCHAR_A, 
       VARCHAR2_A, 
       BIT_A, 
       DATE_A, 
       TIME_A, 
       TIMESTAMP_A, 
       LONG_A, 
       TEXT_A, 
       BLOB_A, 
       CLOB_A, 
       IMAGE_A, 
       BFILE_A) 
       values (v_id2, 
       1, 
       2, 
       3333333333333, 
       4, 
       6, 
       7, 
       0xB2E2CAD462696E617279000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 
       0xB2E2CAD4726177, 
       8.888, 
       9.99999, 
       1.111, 
       2.22222, 
       0xB2E2CAD476617262696E617279, 
       333.33, 
       4.44, 
       5.555555, 
       6.66, 
       '测试char', 
       '测试character', 
       '测试varchar', 
       '测试varchar2', 
       1, 
       '2024-05-31', 
       '10:24:09', 
       '2024-05-31 10:24:04.948000', 
       '测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob', 
       '测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text测试text', 
       0xFFD8FFE000104A46494600010101009000900000FFDB0043000302020302020303030304030304050805050404050A070706080C0A0C0C0B0A0B0B0D0E12100D0E110E0B0B1016101113141515150C0F171816141812141514FFDB00430103040405040509050509140D0B0D1414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414FFC00011080037003E03012200021101031101FFC4001F0000010501010101010100000000000000000102030405060708090A0BFFC400B5100002010303020403050504040000017D01020300041105122131410613516107227114328191A1082342B1C11552D1F02433627282090A161718191A25262728292A3435363738393A434445464748494A535455565758595A636465666768696A737475767778797A838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE1E2E3E4E5E6E7E8E9EAF1F2F3F4F5F6F7F8F9FAFFC4001F0100030101010101010101010000000000000102030405060708090A0BFFC400B51100020102040403040705040400010277000102031104052131061241510761711322328108144291A1B1C109233352F0156272D10A162434E125F11718191A262728292A35363738393A434445464748494A535455565758595A636465666768696A737475767778797A82838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE2E3E4E5E6E7E8E9EAF2F3F4F5F6F7F8F9FAFFDA000C03010002110311003F00F99E8A2B5B41D252FA479EE1643670901963FBD2B9FBB1AFB9C1E7B004F6AFD92A4E34E2E52D91F0D18B93B22BE9FA35D6A48F246AA96F19C3DC4CC12353E858F7F61CFB54D7969A3695B45F6B0EAEDD05B5A1607E9BD909FCABD0D34E5B7B36B894446EA38CF931AAFEEADF8E88BFD4F27A9AF9D6E6D751F1C6A8E7CC4F38C84890F1F857C1E3388E71972D05A77FEB7FC0FA8C264EAA2BD467A459E9DA5EAF196D3B57DE40CE2EAD9A3FD54B81F8E0555D434BBAD2E445B98B6071B92452191C7AAB0E08FA1A3C1FF0B75AD1EDC5CA5F4724711F31A15272F81D2BD574AD3E0F13786D277B4F2CCC0F996EE301C8E33FECB7A30E7D7238A583E249CA7C9885A77162B298C17352678FD15A5E20D15F43D41A03B9A161BA291860B2E48E476208208F506B36BEFA138D48A945E8CF98945C5D985779A1DB8B5B7B18F1FEAA1FB437BC92F43F84613FEFA3EB5C1D7A2D9B868E361FC56D6C47D0408BFCD5ABE773FA92A7847CBD7FE1BF26CF4F2D8A956572CCD3B32B73938AF0BFB1EA1637C2EED0A42237CB8CF7CF4AF6C91C293935E23E24D5A16F10DCDA5BDD2341E7EF0636E339CE33F5AFCAE49EE7DFE15AE67167BBF8661D62DF50313BC52E9AD12B86FE30C4722BB8D3E110D9C48176003EED79D781F5859268165B84F3278D531B8F2DF8F7C57A6A74AAA366AE8E4C45D4ACCE13E29692926962E947CF13893A763856FD7CBFC8FAD795D7B5FC479923F08DF230F9E454553FF6D509FE55E295FAAE435253C1FBDD1B5F91F1398C546BE9D5057476FAF3DA78625B98E16B99F4E4224850FCCD096DC187FBACCC0FB38F4AE72AC69FA84DA5DDA5C5BB6D917239190C08C1523B823823DEBD4C6E1638CA2E948E4C3D6742A29A38CF137C50D5EE034491C76B6AC02492C64390586719CFA1AE8747F843E59B6F38ACF633C6B324A87D403D6A1D67E1BD8788B545D4744912D246915E7D26670B9C0C1F259BE523FD96E7EB5EC5A269ED6BE1BD2F4DB83E4DFC76CA0C520DAC0E3EEFE19C7E15F9563B05570CB924AC7DBE131719BE64F73CBFE23B0F07F85EC86887ECB710DCA4BE6EECB965E8327AF5E956B44F8FFA969BF66B5BF8EDEE0CB284F35DFE741D08603D0D5AF8AFE05BFD7345B0B5B5B77B9BF374AD22C20B7951F39248E83DCD72BA078074EF0ECFF6ED5C45AA6AAAE5E2B55904B04473C348C38723AED191EA7AA9ACBF055710B9211B938CC4D3A6F99B3D2BC75E2E6D6B4BD3ADCC7E4C922ADC4D186C85E0EC1D3B825BE856B89A92E2E25BBB892699DA596462CEEC72493D4D475FAAE130D1C2518D28F43E26B5575AA39B0A28A2BB0C02AF5A6BBA969E812D750BAB65FEEC33328FD0D1454CA3192B495C69B5AA1979ABDF6A231777B71743FE9B4ACFFCCD54A28A23151568AB036DEAC28A28AA11FFD9 
       , 
       '测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob测试clob', 
       0xFFD8FFE000104A46494600010101009000900000FFDB0043000302020302020303030304030304050805050404050A070706080C0A0C0C0B0A0B0B0D0E12100D0E110E0B0B1016101113141515150C0F171816141812141514FFDB00430103040405040509050509140D0B0D1414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414FFC00011080037003E03012200021101031101FFC4001F0000010501010101010100000000000000000102030405060708090A0BFFC400B5100002010303020403050504040000017D01020300041105122131410613516107227114328191A1082342B1C11552D1F02433627282090A161718191A25262728292A3435363738393A434445464748494A535455565758595A636465666768696A737475767778797A838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE1E2E3E4E5E6E7E8E9EAF1F2F3F4F5F6F7F8F9FAFFC4001F0100030101010101010101010000000000000102030405060708090A0BFFC400B51100020102040403040705040400010277000102031104052131061241510761711322328108144291A1B1C109233352F0156272D10A162434E125F11718191A262728292A35363738393A434445464748494A535455565758595A636465666768696A737475767778797A82838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE2E3E4E5E6E7E8E9EAF2F3F4F5F6F7F8F9FAFFDA000C03010002110311003F00F99E8A2B5B41D252FA479EE1643670901963FBD2B9FBB1AFB9C1E7B004F6AFD92A4E34E2E52D91F0D18B93B22BE9FA35D6A48F246AA96F19C3DC4CC12353E858F7F61CFB54D7969A3695B45F6B0EAEDD05B5A1607E9BD909FCABD0D34E5B7B36B894446EA38CF931AAFEEADF8E88BFD4F27A9AF9D6E6D751F1C6A8E7CC4F38C84890F1F857C1E3388E71972D05A77FEB7FC0FA8C264EAA2BD467A459E9DA5EAF196D3B57DE40CE2EAD9A3FD54B81F8E0555D434BBAD2E445B98B6071B92452191C7AAB0E08FA1A3C1FF0B75AD1EDC5CA5F4724711F31A15272F81D2BD574AD3E0F13786D277B4F2CCC0F996EE301C8E33FECB7A30E7D7238A583E249CA7C9885A77162B298C17352678FD15A5E20D15F43D41A03B9A161BA291860B2E48E476208208F506B36BEFA138D48A945E8CF98945C5D985779A1DB8B5B7B18F1FEAA1FB437BC92F43F84613FEFA3EB5C1D7A2D9B868E361FC56D6C47D0408BFCD5ABE773FA92A7847CBD7FE1BF26CF4F2D8A956572CCD3B32B73938AF0BFB1EA1637C2EED0A42237CB8CF7CF4AF6C91C293935E23E24D5A16F10DCDA5BDD2341E7EF0636E339CE33F5AFCAE49EE7DFE15AE67167BBF8661D62DF50313BC52E9AD12B86FE30C4722BB8D3E110D9C48176003EED79D781F5859268165B84F3278D531B8F2DF8F7C57A6A74AAA366AE8E4C45D4ACCE13E29692926962E947CF13893A763856FD7CBFC8FAD795D7B5FC479923F08DF230F9E454553FF6D509FE55E295FAAE435253C1FBDD1B5F91F1398C546BE9D5057476FAF3DA78625B98E16B99F4E4224850FCCD096DC187FBACCC0FB38F4AE72AC69FA84DA5DDA5C5BB6D917239190C08C1523B823823DEBD4C6E1638CA2E948E4C3D6742A29A38CF137C50D5EE034491C76B6AC02492C64390586719CFA1AE8747F843E59B6F38ACF633C6B324A87D403D6A1D67E1BD8788B545D4744912D246915E7D26670B9C0C1F259BE523FD96E7EB5EC5A269ED6BE1BD2F4DB83E4DFC76CA0C520DAC0E3EEFE19C7E15F9563B05570CB924AC7DBE131719BE64F73CBFE23B0F07F85EC86887ECB710DCA4BE6EECB965E8327AF5E956B44F8FFA969BF66B5BF8EDEE0CB284F35DFE741D08603D0D5AF8AFE05BFD7345B0B5B5B77B9BF374AD22C20B7951F39248E83DCD72BA078074EF0ECFF6ED5C45AA6AAAE5E2B55904B04473C348C38723AED191EA7AA9ACBF055710B9211B938CC4D3A6F99B3D2BC75E2E6D6B4BD3ADCC7E4C922ADC4D186C85E0EC1D3B825BE856B89A92E2E25BBB892699DA596462CEEC72493D4D475FAAE130D1C2518D28F43E26B5575AA39B0A28A2BB0C02AF5A6BBA969E812D750BAB65FEEC33328FD0D1454CA3192B495C69B5AA1979ABDF6A231777B71743FE9B4ACFFCCD54A28A23151568AB036DEAC28A28AA11FFD9 
       , 
       null);
      
      UPDATE TEMPLATE_SOURCE_DM21 SET VARCHAR_A = v_id WHERE id = v_id;
      UPDATE TEMPLATE_SOURCE_DM21 SET VARCHAR2_A = v_id WHERE id = v_id;
     
	  DELETE FROM TEMPLATE_SOURCE_DM21 WHERE id = v_id2; 
	  
	  IF (MOD(vi,500) == 0) THEN
 		COMMIT;
 	  END IF;	  
	end loop;
	COMMIT;
END;
```

### 4.4.5. 动态创建表

```plain
CREATE OR replace PROCEDURE proc_create_tab(in_num IN INT, in_tabname varchar2(100) := 't_template_auto') AS
	vi INT:=1;
	v_temp varchar(10);
	v_sql varchar2(500);
BEGIN
 	FOR v_i IN 1..in_num loop
	 	v_sql:= '
		CREATE TABLE template_auto (
			id BIGINT  NOT NULL,
			INT_A INT,
			INTEGER_A INTEGER,
			BIGINT_A BIGINT,
			TINYINT_A TINYINT,
			BYTE_A BYTE,
			SMALLINT_A SMALLINT,
			BINARY_A BINARY(100),
			RAW_A VARBINARY,
			FLOAT_A FLOAT,
			DOUBLE_ DOUBLE,
			REAL_A REAL,
			DOUBLE_PRECISION DOUBLE PRECISION,
			VARBINARY_A VARBINARY,
			NUMERIC_A NUMERIC(10,2),
			DECIMAL_A NUMERIC(10,2),
			DEC_A DEC,
			NUMBER_A NUMBER(10,2),
			CHAR_A CHAR(100),
			CHARACTER_A CHARACTER(100),
			VARCHAR_A VARCHAR,
			VARCHAR2_A VARCHAR2,
			BIT_A BIT,
			DATE_A DATE,
			TIME_A TIME,
			TIMESTAMP_A TIMESTAMP,
			long_a long,
			TEXT_A TEXT,
			BLOB_A BLOB,
			CLOB_A CLOB,
			IMAGE_A IMAGE,
			BFILE_A BFILE,
		CONSTRAINT template_auto_PK PRIMARY KEY (ID)
	)';
	 	
	v_sql := replace(v_sql, 'template_auto', in_tabname||v_i);
    dbms_output.put_line('v_sql:'||v_sql);
	execute IMMEDIATE v_sql;
 	end loop;
 	COMMIT;
END;
```

## 4.5. 其他脚本

```lua
-- 修改最大句柄
select name,type,value,description  from v$parameter where name in ('MEMORY_POOL', 'MAX_SESSION_STATEMENT');
alter system set 'max_session_statement' = 110;

-- 大字段失效问题
sp_set_para_value(1,'LOGMNR_PARSE_LOB',1);
SELECT VALUE FROM V$PARAMETER WHERE NAME = 'LOGMNR_PARSE_LOB';
```

### 4.5.1. 查询scn点位

```shell
DM 执行CDC logminer 执行分析的SQL步骤：

-- 查询有哪些归档日志
SELECT NAME FROM V$ARCHIVED_LOG;
-- logminer加入日志文件
dbms_logmnr.add_logfile ('C:\dmdbms\dmarch\ARCHIVE_LOCAL1_0xCAA3409_EP0_2024-05-10_15-56-22.log');
dbms_logmnr.add_logfile ('C:\dmdbms\dmarch\ARCHIVE_LOCAL1_0xCAA3409_EP0_2024-05-13_10-23-53.log');
dbms_logmnr.add_logfile ('C:\dmdbms\dmarch\ARCHIVE_LOCAL1_0xCAA3409_EP0_2024-05-13_10-32-53.log');
dbms_logmnr.add_logfile ('C:\dmdbms\dmarch\ARCHIVE_LOCAL1_0xCAA3409_EP0_2024-05-14_20-25-49.log');
dbms_logmnr.add_logfile ('C:\dmdbms\dmarch\ARCHIVE_LOCAL1_0xCAA3409_EP0_2024-05-14_20-29-14.log');
dbms_logmnr.add_logfile ('C:\dmdbms\dmarch\ARCHIVE_LOCAL1_0xCAA3409_EP0_2024-05-14_20-47-48.log');
-- 开启归档日志
sys.dbms_logmnr.start_logmnr(Options=>2128 );
-- 查询redolog select operation_code, scn, sql_redo , timestamp ,seg_owner, table_name from V$LOGMNR_CONTENTS where seg_owner = 'SYSDBA' ;
-- 基于oracle的 进行查询达梦的redolog日志
SELECT SCN,
       SQL_REDO,
       OPERATION_CODE,
       "TIMESTAMP",
       XID,
       CSF,
       TABLE_NAME,
       SEG_OWNER,
       OPERATION,
       USERNAME,
       ROW_ID,
       "ROLL_BACK",
       RS_ID,
       STATUS,
       INFO,
       SSN,
       THREAD#
FROM V$LOGMNR_CONTENTS
WHERE
--     SCN >20606161
--   AND SCN <=20606172 AND TBALE_NAME IN ()
    (SEG_OWNER IS NULL OR SEG_OWNER NOT IN
                          ('APPQOSSYS', 'AUDSYS', 'CTXSYS', 'DVSYS', 'DBSFWUSER', 'DBSNMP', 'GSMADMIN_INTERNAL',
                           'LBACSYS', 'MDSYS', 'OJVMSYS', 'OLAPSYS', 'ORDDATA', 'ORDSYS', 'OUTLN', 'SYS', 'SYSTEM',
                           'WMSYS', 'XDB'))
  AND OPERATION_CODE IN (1, 2, 3, 5, 6, 7, 9, 10, 11, 29, 34, 36, 255);

--  终止归档日志文件分析；
DBMS_LOGMNR.END_LOGMNR();

@李攀(李攀)  
```

# 5. PostgreSQL

## 5.1. 数据类型

| 名字                                           | 别名                             | 描述                                          |
| ---------------------------------------------- | -------------------------------- | --------------------------------------------- |
| bigint                                         | int8                             | 有符号的8字节整数                             |
| bigserial                                      | serial8                          | 自动增长的8字节整数                           |
| bit [ (***n\***) ]                             |                                  | 定长位串                                      |
| bit varying [ (***n\***) ]                     | varbit [ (***n\***) ]            | 变长位串                                      |
| boolean                                        | bool                             | 逻辑布尔值（真/假）                           |
| box                                            |                                  | 平面上的普通方框                              |
| bytea                                          |                                  | 二进制数据（“字节数组”）                      |
| character [ (***n\***) ]                       | char [ (***n\***) ]              | 定长字符串                                    |
| character varying [ (***n\***) ]               | varchar [ (***n\***) ]           | 变长字符串                                    |
| cidr                                           |                                  | IPv4或IPv6网络地址                            |
| circle                                         |                                  | 平面上的圆                                    |
| date                                           |                                  | 日历日期（年、月、日）                        |
| double precision                               | float8                           | 双精度浮点数（8字节）                         |
| inet                                           |                                  | IPv4或IPv6主机地址                            |
| integer                                        | int, int4                        | 有符号4字节整数                               |
| interval [ ***fields\*** ] [ (***p\***) ]      |                                  | 时间段                                        |
| json                                           |                                  | 文本 JSON 数据                                |
| jsonb                                          |                                  | 二进制 JSON 数据，已分解                      |
| line                                           |                                  | 平面上的无限长的线                            |
| lseg                                           |                                  | 平面上的线段                                  |
| macaddr                                        |                                  | MAC（Media Access Control）地址               |
| macaddr8                                       |                                  | MAC（Media Access Control）地址（EUI-64格式） |
| money                                          |                                  | 货币数量                                      |
| numeric [ (***p\***, ***s\***) ]               | decimal [ (***p\***, ***s\***) ] | 可选择精度的精确数字                          |
| path                                           |                                  | 平面上的几何路径                              |
| pg_lsn                                         |                                  | PostgreSQL日志序列号                          |
| pg_snapshot                                    |                                  | 用户级事务ID快照                              |
| point                                          |                                  | 平面上的几何点                                |
| polygon                                        |                                  | 平面上的封闭几何路径                          |
| real                                           | float4                           | 单精度浮点数（4字节）                         |
| smallint                                       | int2                             | 有符号2字节整数                               |
| smallserial                                    | serial2                          | 自动增长的2字节整数                           |
| serial                                         | serial4                          | 自动增长的4字节整数                           |
| text                                           |                                  | 变长字符串                                    |
| time [ (***p\***) ] [ without time zone ]      |                                  | 一天中的时间（无时区）                        |
| time [ (***p\***) ] with time zone             | timetz                           | 一天中的时间，包括时区                        |
| timestamp [ (***p\***) ] [ without time zone ] |                                  | 日期和时间（无时区）                          |
| timestamp [ (***p\***) ] with time zone        | timestamptz                      | 日期和时间，包括时区                          |
| tsquery                                        |                                  | 文本搜索查询                                  |
| tsvector                                       |                                  | 文本搜索文档                                  |
| txid_snapshot                                  |                                  | 用户级别事务ID快照(废弃; 参见 pg_snapshot)    |
| uuid                                           |                                  | 通用唯一标识码                                |
| xml                                            |                                  | XML数据                                       |

## 5.2. 创建表

```lua
-- 常用字段
CREATE TABLE template_source_2 (
  id bigint NOT null PRIMARY KEY,
  BIGINT_a bigint DEFAULT NULL,
  bit_a bit default null,
  varbit_a varbit(10) default null,
  bool_a bool default null,
  bytea_a bytea default null,
  char_a char(100) default null,
  varchar_a varchar(500) DEFAULT NULL,
  date_a date default NULL,
  float8_a float8 default NULL,
  integer_a integer default NULL,
  interval_a interval default NULL,
  json_a json default NULL,
  jsonb_a jsonb default NULL,
  money_a money default NULL,
  numeric_a numeric(10,2) default NULL,
  float4_a float4 default NULL,
  int2_a int2 default NULL,
  text_a text default NULL,
  time_a time default NULL,
  timestamp_a timestamp default NULL,
  uuid_a uuid default NULL,
  xml_a xml default NULL
);

-- 全字段
CREATE TABLE template_source_all (
  id bigint NOT null PRIMARY KEY,
  BIGINT_a bigint DEFAULT NULL,
  serial8_a serial8,
  bit_a bit default null,
  varbit_a varbit(10) default null,
  bool_a bool default null,
  box_a box default null,
  bytea_a bytea default null,
  char_a char(100) default null,
  varchar_a varchar(500) DEFAULT NULL,
  cidr_a cidr default NULL,
  circle_a circle default NULL,
  date_a date default NULL,
  float8_a float8 default NULL,
  integer_a integer default NULL,
  interval_a interval default NULL,
  json_a json default NULL,
  jsonb_a jsonb default NULL,
  line_a line default NULL,
  lseg_a lseg default NULL,
  macaddr_a macaddr default NULL,
  macaddr8_a macaddr8 default NULL,
  money_a money default NULL,
  numeric_a numeric(10,2) default NULL,
  path_a path default NULL,
  pg_lsn_a pg_lsn default NULL,
  pg_snapshot_a pg_snapshot default NULL,
  point_a point default NULL,
  polygon_a polygon default NULL,
  float4_a float4 default NULL,
  int2_a int2 default NULL,
  serial2_a serial2,
  serial4_a serial4,
  text_a text default NULL,
  time_a time default NULL,
  timestamp_a timestamp default NULL,
  tsquery_a tsquery default NULL,
  tsvector_a tsvector default NULL,
  txid_snapshot_a txid_snapshot default NULL,
  uuid_a uuid default NULL,
  xml_a xml default NULL
  
) ;
```

## 5.3. 创建序列

```lua
CREATE SEQUENCE public.ts2_pk_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- 使用方法
select nextval('ts2_pk_seq');
```

## 5.4. 插入数据

```lua
insert
	into
	public.template_source_2 (id,
	bigint_a,
	serial8_a,
	bit_a,
	varbit_a,
	bool_a,
	bytea_a,
	char_a,
	varchar_a,
	date_a,
	float8_a,
	integer_a,
	interval_a,
	json_a,
	jsonb_a,
	money_a,
	numeric_a,
	float4_a,
	int2_a,
	serial2_a,
	serial4_a,
	text_a,
	time_a,
	timestamp_a,
	uuid_a,
	xml_a)
values(nextval('ts2_pk_seq') ,
1,
5,
'1',
B'101',
true,
decode('41', 'hex'),
'char                                                                                                ',
'测试varchar',
'2024-03-28',
1.1,
22,
'00:10:01'::interval,
'{
"a":"a"
}'::json,
'{"a": "二进制"}'::jsonb,
'$3.33',
1.00,
1.11,
22,
5,
5,
'test',
'10:30:19',
'2024-03-28 10:30:25.731',
'3c8d4435-1612-4125-8443-6031c534c069'::uuid,
'<xml>
<testxml>
</testxml>
</xml>');
```

## 5.5. 创建存储过程

### 5.5.1. 例子

### 5.5.2. 新增、编辑、删除同时提交

```lua
CREATE OR REPLACE PROCEDURE insert_sql(
	v_in_num IN integer
) AS $$ 
DECLARE
	vi integer;
	v_id integer;

BEGIN
	vi := 0 ;
for vi IN 1..v_in_num LOOP
	v_id := nextval('ts2_pk_seq');
insert
	into
	public.template_source_2 (id,
	bigint_a,
	serial8_a,
	bit_a,
	varbit_a,
	bool_a,
	bytea_a,
	char_a,
	varchar_a,
	date_a,
	float8_a,
	integer_a,
	interval_a,
	json_a,
	jsonb_a,
	money_a,
	numeric_a,
	float4_a,
	int2_a,
	serial2_a,
	serial4_a,
	text_a,
	time_a,
	timestamp_a,
	uuid_a,
	xml_a)
values(v_id,
1,
5,
'1',
B'101',
true,
decode('41', 'hex'),
'char                                                                                                ',
'测试varchar',
'2024-03-28',
1.1,
22,
'00:10:01'::interval,
'{
"a":"a"
}'::json,
'{"a": "二进制"}'::jsonb,
'$3.33',
1.00,
1.11,
22,
5,
5,
'test',
'10:30:19',
'2024-03-28 10:30:25.731',
'3c8d4435-1612-4125-8443-6031c534c069'::uuid,
'<xml>
<testxml>
</testxml>
</xml>');

 UPDATE  "template_source_2" SET char_a = 'update char' WHERE id = v_id;
 UPDATE  "template_source_2" SET varchar_a = 'update varchar' WHERE id = v_id;

 DELETE FROM DFO."template_source2" WHERE id = v_id;

-- vi := vi + 1;
END loop;
  commit;

END  
$$ LANGUAGE  plpgsql;
```

# 6. PolarDB

## 6.1. PolarDB MySQL

## 6.2. Polardb PostgreSQL

阿里云的PostgreSQL

### 6.2.1. 支持的数据类型

| **名字**          | **存储尺寸** | **说明**                         | **范围**                                     |
| ----------------- | ------------ | -------------------------------- | -------------------------------------------- |
| smallint          | 2字节        | 小范围整数。                     | -32768 to +32767                             |
| integer           | 4字节        | 整数的典型选择。                 | -2147483648 to +2147483647                   |
| bigint            | 8字节        | 大范围整数。                     | -9223372036854775808 to +9223372036854775807 |
| decimal           | 可变         | 用户指定精度，精确。             | 最高小数点前131072位，以及小数点后16383位    |
| numeric           | 可变         | 用户指定精度，精确。             | 最高小数点前131072位，以及小数点后16383位    |
| real              | 4字节        | 可变精度，不精确。               | 6位十进制精度                                |
| double precision  | 8字节        | 可变精度，不精确。               | 15位十进制精度                               |
| smallserial       | 2字节        | 自动增加的小整数。               | 1到32767                                     |
| serial            | 4字节        | 自动增加的整数。                 | 1到2147483647                                |
| bigserial         | 8字节        | 自动增长的大整数。               | 1到9223372036854775807                       |
| BINARY_INTEGER    | 4字节        | 有符号整数，integer的别名。      | -2,147,483,648 到+2,147,483,647              |
| NUMBER            | 可变         | 用户指定的精度，精确。           | 最高1000位十进制精度                         |
| NUMBER(p [, s ] ) | 可变         | 最大精度p和可选刻度s的精确数字。 | 最高1000位十进制精度                         |
| PLS_INTEGER       | 4字节        | 有符号整数，integer的别名。      | -2,147,483,648 到 +2,147,483,647             |
| ROWID             | 8字节        | 带符号的8位整数。                | -9223372036854775808 到9223372036854775807   |

### 6.2.2. 创建表

创建源表SQL



## 6.3. Polardb PostgreSQL（兼容Oracle）

### 6.3.1. 支持的数据类型

### 6.3.2. 创建表

```json
CREATE TABLE template_source_pg_11 (
  id bigint NOT null PRIMARY KEY,
  varchar_a varchar(500) DEFAULT NULL,
  datetime_a datetime default NULL,
  timestamp_a timestamp default NULL,
  char_a char(100) default null,
  int_a int default null,
  text_a text default NULL,
  tinyint_a tinyint default NULL,
  decimal_a decimal(10,5) default NULL,
  bit_a bit default null,
  BIGINT_a bigint DEFAULT NULL
);
```

### 6.3.3. 创建序列

```shell
CREATE SEQUENCE tspg11_pk_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;
```

### 6.3.4. 创建存储过程

```json
CREATE OR REPLACE PROCEDURE public.insert_sql(v_in_num integer)
 SECURITY INVOKER
AS $procedure$ 
DECLARE
	vi integer;
	v_id integer;

BEGIN
	-- vi := 0 ;
for vi IN 1..v_in_num LOOP
	v_id := nextval('tspg11_pk_seq');

	insert
		into
		public.template_source_pg_11
		(id,
		varchar_a,
		datetime_a,
		timestamp_a,
		char_a,
		int_a,
		text_a,
		tinyint_a,
		decimal_a,
		bit_a,
		bigint_a)
	values(v_id,
	'测试varchar'||v_id,
	'2024-05-21 15:44:47.794',
	'2024-05-21 15:44:50.778',
	'测试char'||v_id,
	1,
	'测试text'||v_id,
	2,
	3.33000,
	'1',
	112312312312321);
 	UPDATE  "template_source_pg_11" SET varchar_a = vi WHERE id = v_id;
 	UPDATE  "template_source_pg_11" SET char_a = vi WHERE id = v_id;
 
    DELETE FROM "template_source_pg_11" WHERE id = v_id;
  
	if mod(v_in_num, 1000) = 0 then 
		commit;
	end if;
  
-- vi := vi + 1;
END loop;
  commit;

END  
$procedure$
 LANGUAGE plpgsql
;
```

#### 6.3.4.1. 动态创建表字段

```shell
CREATE OR REPLACE PROCEDURE public.create_table(v_in_num integer)
AS $$
DECLARE
	-- vi integer;
	v_create_sql varchar2;
	v_tab_name varchar2 default 'temp_mpl_field_'|| EXTRACT (EPOCH from CURRENT_TIMESTAMP(0));
	v_field_sql varchar2;
BEGIN
	-- vi := 0 ;
	v_create_sql := 'create table '|| v_tab_name ||' ( id BIGINT NOT null PRIMARY KEY)';
	RAISE NOTICE '%',v_create_sql;
    EXECUTE v_create_sql;

	for vi IN 1..v_in_num loop
		v_field_sql := 'ALTER TABLE ' || v_tab_name || ' ADD field_' || vi || ' varchar NULL';
		RAISE NOTICE '%',v_field_sql;
		EXECUTE v_field_sql;
	END loop;

  commit;

END
$$
 LANGUAGE plpgsql
;
```

# 7. OceanBase

## 7.1. 数据类型

## 7.2. 建表语句

```lua
CREATE TABLE sggt."template_source3" (
	ID number NOT NULL,
	number_a number NOT NULL,
	FLOAT_A FLOAT NOT NULL,
	BINARY_DOUBLE_A BINARY_DOUBLE NULL,
	BINARY_FLOAT_A BINARY_FLOAT NULL,
	DECIMAL_A NUMBER(38,0) NULL,
	CHAR_A char(500) NULL,
	VARCHAR2_A VARCHAR2(500) NULL,
	NCHAR_A NCHAR(500) NULL,
	NVARCHAR2_A NVARCHAR2(500) NULL,
	DATE_A DATE NULL,
	TIMESTAMP_A TIMESTAMP NULL,
	TIMESTAMP_withTime_A TIMESTAMP WITH TIME ZONE NULL,
	TIMESTAMP_withlocal_A	TIMESTAMP WITH LOCAL TIME ZONE	NULL,
	CLOB_A CLOB NULL,
	BLOB_A BLOB NULL,
	json_a JSON  NULL,
	xmltype_a XMLType NULL,
	CONSTRAINT "NEWTABLE_PK3" PRIMARY KEY ("ID")
);
-- 加注释
COMMENT ON COLUMN template_source3.VARCHAR2_A IS '用户的电子邮件地址';
```



## 7.3. 查询

### 7.3.1. 分页查询

```lua
SELECT *
FROM TEMPLATE_SOURCE2
ORDER BY id
OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY;
```

# 8. DataHub

## 8.1. 数据类型

## 8.2. console命令

### 8.2.1. 下载数据

```shell
down -p datasource0410 -t template_source_p1 -s 0 -d 17120239889721JV6H -f C:\\Users\\lipan\\Desktop\\test.csv -ti "2024-04-01 18:14:17" -l 30000 -g 0
```

# 9. GBase

## 9.1. 数据类型

## 9.2. 创建表

```lua
CREATE TABLE gbase8s1:template_source_gb11 (
	id BIGINT NOT null PRIMARY key,
	integer_a INTEGER,
	int8_a INT8,
	bigint_a BIGINT,
	smallint_a SMALLINT,
	float_a FLOAT,
	decimal_a DECIMAL(20,0),
	decimal_b DECIMAL(20,10),
	char_a CHAR(100),
	varchar_a VARCHAR(100),
	lvarchar_a LVARCHAR(100),
	nvarchar_a NVARCHAR(100),
	nvarchar2_a VARCHAR2(2048) NOT NULL,
	date_a DATE,
	datetime_a DATETIME YEAR TO YEAR,
	text_a TEXT
)
 ;
```

## 9.3. 创建序列

```lua
CREATE SEQUENCE seq_gb11
 INCREMENT BY 1 START WITH 1 
```

## 9.4. 插入数据

```lua
  insert
	into
	gbase8s1:template_source_gb11 (id,
	integer_a,
	int8_a,
	bigint_a,
	smallint_a,
	float_a,
	decimal_a,
	decimal_b,
	char_a,
	varchar_a,
	lvarchar_a,
	nvarchar_a,
	date_a,
	datetime_a,
	text_a)
	values (seq_gb11.nextval,
	33,
	2,
	55,
	444,
	12.34,
	13,
	33.5500000000,
	'testchar',
	'zkd',
	'lvarchar',
	'nvchar',
	'2024-06-21',
	'2024',
	null);
```

## 9.5. 创建存储过程

dbevaer执行存储过程需要选中内容执行！！！

### 9.5.1. 插入数据

```lua
CREATE SEQUENCE seq_gb11
 INCREMENT BY 1 START WITH 1 

drop procedure if exists proc_insert;
create PROCEDURE proc_insert(v_in_num int)
 returning varchar(100);
 DEFINE v_sql,v_res varchar(100);
 DEFINE v_i int;

 for v_i = 1 to v_in_num
  insert
	into
	gbase8s1:template_source_gb11 (id,
	integer_a,
	int8_a,
	bigint_a,
	smallint_a,
	float_a,
	decimal_a,
	decimal_b,
	char_a,
	varchar_a,
	lvarchar_a,
	nvarchar_a,
	date_a,
	datetime_a,
	text_a)
	values (seq_gb11.nextval,
	33,
	2,
	55,
	444,
	12.34,
	13,
	33.5500000000,
	'testchar'||v_i,
	'zkd'||v_i,
	'lvarchar'||v_i,
	'nvchar'||v_i,
	'2024-06-21',
	'2024',
	null);

 end for;

return v_in_num;

END PROCEDURE
DOCUMENT '生成数据';

call proc_insert(5);
```

### 9.5.2. 插入编辑删除数据

```lua
drop procedure if exists proc_insert3;
create PROCEDURE proc_insert3(v_in_num int)
 returning varchar(100);
 DEFINE v_sql,v_res,v_id varchar(100);
 DEFINE v_i int;


BEGIN work;
 for v_i = 1 to v_in_num
  insert
	into
	gbase8s1:template_source_gb16 (id,
	integer_a,
	int8_a,
	bigint_a,
	smallint_a,
	float_a,
	decimal_a,
	decimal_b,
	char_a,
	varchar_a,
	lvarchar_a,
	nvarchar_a
--	date_a,
--	datetime_a,
--	text_a
	)
	values (seq_gb12.nextval,
	33,
	2,
	55,
	444,
	12.34,
	13,
	33.5500000000,
	'testchar'||v_i,
	'zkd'||v_i,
	'lvarchar'||v_i,
	'nvarchar'||v_i
--	'2024-06-21',
--	'2024-06-01 00:00:01.001',
--	null
);
let v_id = seq_gb12.nextval;
 insert
	into
	gbase8s1:template_source_gb16 (id,
	integer_a,
	int8_a,
	bigint_a,
	smallint_a,
	float_a,
	decimal_a,
	decimal_b,
	char_a,
	varchar_a,
	lvarchar_a,
	nvarchar_a
	)
	values (v_id,
	33,
	2,
	55,
	444,
	12.34,
	13,
	33.5500000000,
	'testchar'||v_i,
	'zkd'||v_i,
	'lvarchar'||v_i,
	'nvarchar'||v_i
);
--修改
update gbase8s1:template_source_gb16 set varchar_a = '修改'||v_i where id = v_id;
update gbase8s1:template_source_gb16 set varchar_a = '修改'||v_i where id = v_id;

--删除
delete gbase8s1:template_source_gb16 where id = v_id;
 if mod(v_i, 1000) = 0 then
 	commit work;
 end if;

 end for;

commit work;

return v_in_num;

END PROCEDURE
DOCUMENT '生成数据';
```

### 9.5.3. 插入编辑删除数据2

```shell
drop procedure if exists proc_insert11;
create PROCEDURE proc_insert11(v_in_num int)
 returning varchar(100);
 DEFINE v_sql,v_res,v_id varchar(100);
 DEFINE v_i int;

 BEGIN work;

 for v_i = 1 to v_in_num
  insert
	into
	gbase8s1:template_source_gb11 (id,
	integer_a,
	int8_a,
	bigint_a,
	smallint_a,
	float_a,
	decimal_a,
	decimal_b,
	char_a,
	varchar_a,
	lvarchar_a,
	nvarchar_a,
	nvarchar2_a,
	date_a,
	datetime_a,
	text_a)
	values (seq_gb11.nextval,
	33,
	2,
	55,
	444,
	12.34,
	13,
	33.5500000000,
	'testchar'||v_i,
	'zkd'||v_i,
	'lvarchar'||v_i,
	'nvarchar'||v_i,
	'nvarchar2'||v_i,
	'2024-06-21',
	'2024-06-01 00:00:01',
	null
);
let v_id = seq_gb11.nextval;
   insert
	into
	gbase8s1:template_source_gb11 (id,
	integer_a,
	int8_a,
	bigint_a,
	smallint_a,
	float_a,
	decimal_a,
	decimal_b,
	char_a,
	varchar_a,
	lvarchar_a,
	nvarchar_a,
	nvarchar2_a,
	date_a,
	datetime_a,
	text_a
	)
	values (v_id,
	33,
	2,
	55,
	444,
	12.34,
	13,
	33.5500000000,
	'testchar'||v_i,
	'zkd'||v_i,
	'lvarchar'||v_i,
	'nvarchar'||v_i,
	'nvarchar2'||v_i,
	'2024-06-21',
	'2024-06-01 00:00:01',
	null
);
--修改
update gbase8s1:template_source_gb11 set varchar_a = '修改'||v_i where id = v_id;
update gbase8s1:template_source_gb11 set varchar_a = '修改'||v_i where id = v_id;

--删除
delete gbase8s1:template_source_gb11 where id = v_id;
 if mod(v_i, 1000) = 0 then
 	commit work;
 end if;

 end for;

commit work;

return v_in_num;

END PROCEDURE
DOCUMENT '生成数据';
```

### 9.5.4. 动态创建表

```plain
drop procedure if exists proc_create_tab;
create PROCEDURE proc_create_tab(v_in_num int, v_in_tabname varchar(100) default 't_template_auto')
 returning varchar(100);
 DEFINE v_sql,v_res varchar(500);
 DEFINE v_i int;

 for v_i = 1 to v_in_num
  let v_sql = '
  CREATE TABLE t_template_auto (
	id BIGINT NOT null PRIMARY key,
	integer_a INTEGER,
	int8_a INT8,
	bigint_a BIGINT,
	smallint_a SMALLINT,
	float_a FLOAT,
	decimal_a DECIMAL(20,0),
	decimal_b DECIMAL(20,10),
	char_a CHAR(100),
	varchar_a VARCHAR(100),
	lvarchar_a LVARCHAR(100),
	nvarchar_a NVARCHAR(100),
	nvarchar2_a VARCHAR2(2048),
	date_a DATE,
	datetime_a DATETIME YEAR TO SECOND,
	text_a TEXT);';
   let v_sql = replace(v_sql, 't_template_auto', v_in_tabname||v_i);
   EXECUTE IMMEDIATE v_sql;

 end for;

return v_in_num;

END PROCEDURE
DOCUMENT '生成表';

call proc_create_tab(5);
```




## 9.6 sqlserver cdc :

查看是否开启cdc：
SELECT name,is_cdc_enabled FROM sys.databases WHERE name = 'zkdServer';
执行开启cdc：
EXECUTE sys.sp_cdc_enable_db;
查看是否启用日志：
SELECT is_cdc_enabled,CASE WHEN is_cdc_enabled=0 THEN 'CDC功能禁用' ELSE 'CDC功能启用' END 描述
FROM sys.databases    
WHERE NAME = 'zkdServer'     # 数据库名

注意要启动一下sqlserver代理

--查看agent状态
EXEC master.dbo.xp_servicecontrol N'QUERYSTATE', N'SQLSERVERAGENT'

-- 对当前数据库禁用 CDC
USE schoolmasterreport_db
GO
EXEC sys.sp_cdc_disable_db
GO

-- 禁用表CDC
USE 库名
GO
EXEC sys.sp_cdc_disable_table
@source_schema = 'dbo',
@source_name   = '表名',
@capture_instance = 'dbo_表名'
GO

-- 开启表CDC
USE 库名
GO
EXEC sys.sp_cdc_enable_table
@source_schema = 'dbo',
@source_name   = '表名',
@role_name     = NULL
GO



go
exec sp_cdc_enable_table
@source_schema='dbo',
@source_name='test_source1',
@role_name=null,
@supports_net_changes = 1
go

go
EXEC sys.sp_cdc_disable_table
@source_schema = 'dbo',
@source_name = 'test_source1',
@capture_instance = 'dbo_test_source1'
go

-- capture_instance一般为schema_table的格式拼接而成，可以通过以下命令，查询实际的值
exec sys.sp_cdc_help_change_data_capture
@source_schema = 'dbo',
@source_name = 'test_source1';