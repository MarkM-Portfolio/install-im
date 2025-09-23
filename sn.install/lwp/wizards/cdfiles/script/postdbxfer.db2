-- ***************************************************************** 
--                                                                   
-- Licensed Materials - Property of IBM                              
--                                                                   
-- 5724-S68                                                          
--                                                                   
-- Copyright IBM Corp. 2001, 2006  All Rights Reserved.              
--                                                                   
-- US Government Users Restricted Rights - Use, duplication or       
-- disclosure restricted by GSA ADP Schedule Contract with           
-- IBM Corp.                                                         
--                                                                   
-- ***************************************************************** 

CONNECT TO PEOPLEDB;

-- Restore generated expressions after importing data into database
SET INTEGRITY FOR EMPINST.EMPLOYEE OFF; 
ALTER TABLE EMPINST.EMPLOYEE ALTER COLUMN  PROF_MAIL_LOWER SET GENERATED ALWAYS AS (LOWER(PROF_MAIL)); 
SET INTEGRITY FOR EMPINST.EMPLOYEE IMMEDIATE CHECKED FORCE GENERATED; 
COMMIT;

SET INTEGRITY FOR EMPINST.PEOPLE_TAG OFF; 
ALTER TABLE EMPINST.PEOPLE_TAG ALTER COLUMN PROF_TAG_ID SET GENERATED AS IDENTITY;
SET INTEGRITY FOR EMPINST.PEOPLE_TAG IMMEDIATE CHECKED FORCE GENERATED; 
COMMIT;

CONNECT RESET;

TERMINATE;
