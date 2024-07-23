

CREATE OR REPLACE PROCEDURE ADMIN.create_tab(
	v_in_num IN NUMBER
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
	v_sql:=replace(v_temp_sql,'template_source_o','template_source_o2'||vi);
	  DBMS_OUTPUT.PUT_LINE(v_sql);
execute immediate v_sql;
commit;
END loop;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END create_tab;