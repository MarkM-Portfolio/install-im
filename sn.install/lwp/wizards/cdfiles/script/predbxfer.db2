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

-- Remove generated expressions prior to importing database records
-- to prevent errors when attempting to write into generated fields
ALTER TABLE EMPINST.PEOPLE_TAG ALTER COLUMN PROF_TAG_ID DROP IDENTITY ; 
COMMIT;

ALTER TABLE EMPINST.EMPLOYEE ALTER COLUMN PROF_MAIL_LOWER DROP EXPRESSION ; 
COMMIT;

CONNECT RESET;

TERMINATE;



