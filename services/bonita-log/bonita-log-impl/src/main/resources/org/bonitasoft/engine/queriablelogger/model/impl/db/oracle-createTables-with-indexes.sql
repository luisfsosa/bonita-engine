CREATE TABLE queriable_log (
  tenantid NUMBER(19, 0) NOT NULL,
  id NUMBER(19, 0) NOT NULL,
  timeStamp NUMBER(19, 0) NOT NULL,
  year SMALLINT NOT NULL,
  month SMALLINT NOT NULL,
  dayOfYear SMALLINT NOT NULL,
  weekOfYear SMALLINT NOT NULL,
  userId VARCHAR2(50 CHAR) NOT NULL,
  threadNumber NUMBER(19, 0) NOT NULL,
  clusterNode VARCHAR2(50 CHAR),
  productVersion VARCHAR2(50 CHAR) NOT NULL,
  severity VARCHAR2(50 CHAR) NOT NULL,
  actionType VARCHAR2(50 CHAR) NOT NULL,
  actionScope VARCHAR2(100 CHAR),
  actionStatus SMALLINT NOT NULL,
  rawMessage VARCHAR2(255 CHAR) NOT NULL,
  callerClassName VARCHAR2(200 CHAR),
  callerMethodName VARCHAR2(80 CHAR),
  numericIndex1 NUMBER(19, 0),
  numericIndex2 NUMBER(19, 0),
  numericIndex3 NUMBER(19, 0),
  numericIndex4 NUMBER(19, 0),
  numericIndex5 NUMBER(19, 0),
  PRIMARY KEY (tenantid, id)
);

CREATE TABLE queriablelog_p (
  tenantid NUMBER(19, 0) NOT NULL,
  id NUMBER(19, 0) NOT NULL,
  queriableLogId NUMBER(19, 0) NOT NULL,
  name VARCHAR2(50 CHAR) NOT NULL,
  stringValue VARCHAR2(255 CHAR),
  blobId NUMBER(19, 0),
  valueType VARCHAR2(30 CHAR)
  PRIMARY KEY (tenantid, id)
);

CREATE INDEX idx_queriablelog ON queriablelog_p (queriableLogId);
ALTER TABLE queriablelog_p ADD CONSTRAINT fk_queriableLogId FOREIGN KEY (tenantid, queriableLogId) REFERENCES queriable_log(tenantid, id);
