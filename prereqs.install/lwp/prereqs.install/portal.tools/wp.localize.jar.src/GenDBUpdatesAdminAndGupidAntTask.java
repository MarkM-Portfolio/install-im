/* @copyright module */
package com.ibm.wps.localize;

/***********************************************************************
 ***********************************************************************
 * IBM Confidential
 * OCO Source Materials
 * IBM eNetwork Computing
 * (c) Copyright IBM Corp 2004
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 ***********************************************************************
 * CMVC Location:  %W%
 * Version:        %I%
 * Last Modified:  %G%
 ***********************************************************************
 *
 * Class: GenDBUpdatesAdminAngGupidAntTask
 *
 ***********************************************************************
 ***********************************************************************
 *
 * CHANGE HISTORY:
 * ---------------------------------------------------------------------
 *  REASON |   DATE    | OWNER  | REASON
 * ---------------------------------------------------------------------
 *  93407    07/06/04  seekamp Fix comments
 * 106859   11/15/04  seekamp Bypass entries with blank primary key and
 *                                               allow combined primary keys
 ***********************************************************************
 ***********************************************************************/

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

/**
 * <b>GenDBUpdatesAdminAndGupidAntTask</b> is the Ant task that parses exported data from a database table into
 * an UPDATE statement for updating the Admin userid and updating Object IDs
 *
 **/
public class GenDBUpdatesAdminAndGupidAntTask extends Task
{
  /** IBM copyright information */
  private final static String COPYRIGHT = com.ibm.wps.Copyright.SHORT;

  /** Export file name attribute */
  public final static String ATTR_EXPORT_FILE_NAME = "file";

  /** DB User name attribute */
  public final static String ATTR_DB_USER = "dbuser";

  /** Primary field number attribute name */
  public final static String ATTR_PRIMARY_FIELD_NUMBER = "primary";

  /** Primary field nameattribute name */
  public final static String ATTR_PRIMARY_FIELD_NAME = "primaryName";

  /** ObjectID field number attribute name */
  public final static String ATTR_OBJECTID_FIELD_NUMBER = "objectid";

  /** ObjectID field name attribute name */
  public final static String ATTR_OBJECTID_FIELD_NAME = "objectidName";

  /** Admin Userid field names attribute name */
  public final static String ATTR_ADMINID_FIELD_NAMES= "adminNames";

  private static class FileInfo
  {
    public FileInfo(String filePath, String dbUser, ArrayList primaryNameList, ArrayList primaryNumberList, String objectIdName, int objectIdNumber, String[] adminNames)
    {
      this.filePath = filePath;
      this.dbUser = dbUser;
      this.primaryFieldNameList = primaryNameList;
      this.primaryFieldList = primaryNumberList;
      this.objectIdFieldName = objectIdName;
      this.objectIdField = objectIdNumber;
      this.adminIdFieldNames = adminNames;
    }

    public String toString()
    {
      StringBuffer pBuffer = new StringBuffer();
      pBuffer.append("FileInfo(filepath=");
      pBuffer.append(filePath);
      pBuffer.append(", dbUser=");
      pBuffer.append(dbUser);
      pBuffer.append(", primaryNameList=");
      pBuffer.append(primaryFieldNameList);
      pBuffer.append(", primaryFieldList=");
      pBuffer.append(primaryFieldList);
      pBuffer.append(", objectIdName=");
      pBuffer.append(objectIdFieldName);
      pBuffer.append(", objectIdField=");
      pBuffer.append(objectIdField);
      pBuffer.append(", adminNames=");
      if(adminIdFieldNames == null)
      {
        pBuffer.append("null");
      }
      else
      {
        for(int index=0; index < adminIdFieldNames.length; ++index)
        {
          if(index > 0)
          {
            pBuffer.append(", ");
          }
          pBuffer.append(adminIdFieldNames[index]);
        }
      }
      pBuffer.append(")");

      return pBuffer.toString();
    }

    String filePath;
    String dbUser;
    ArrayList primaryFieldNameList;
    ArrayList primaryFieldList;
    String objectIdFieldName;
    int objectIdField;
    String[] adminIdFieldNames;
  }

  /**
   * Construct this GenDBUpdatesAdminAndGupidAntTask
   *
   **/
  public GenDBUpdatesAdminAndGupidAntTask()
  {
    m_separator = "_";
    m_append = false;
  }

  /**
   * Execute the task
   *
   * @throws BuildException
   **/
  public void execute() throws BuildException
  {
    if(
      (m_inDirPath == null) ||
      (m_ctlFileName == null) ||
      (m_ctlFileSep == null) ||
      (m_outFilepath == null)||
      (m_beginDelim == null) ||
      (m_endDelim == null)
    )
    {
      throw new BuildException("Error: in " + getClass().getName() + " ctlFileName, ctlFileSep, inDir, outFilepath,  beginDelim, endDelim must be specified");
    }

    if((m_endDelim.length() == 0) || (m_beginDelim.length() == 0) || m_beginDelim.equals(m_endDelim))
    {
      throw new BuildException("Error: in " + getClass().getName() + " beginning and ending delimiters must be different and both not empty");
    }

    if(m_ctlFileSep.length() != 1)
    {
      throw new BuildException("Error: in " + getClass().getName() + " control file separator must be a single character");
    }

    m_inDir = new File(m_inDirPath);
    if(!m_inDir.exists())
    {
      throw new BuildException("Error: in " + getClass().getName() +  " directory " + m_inDirPath + " does not exist");
    }

    ArrayList fileInfoList = parseControlFile(m_inDir);
    try
    {
      BufferedWriter out = new BufferedWriter(new FileWriter(m_outFilepath, m_append));

      for(int fileInfoIndex=0; fileInfoIndex < fileInfoList.size(); ++fileInfoIndex)
      {
        FileInfo fileInfo = (FileInfo) fileInfoList.get(fileInfoIndex);
        System.out.println("Processing file with info " + fileInfo);

        File exportFile = new File(fileInfo.filePath);
        if(!exportFile.exists())
        {
          throw new BuildException("Error: in " + getClass().getName()+  " file " + fileInfo.filePath + " does not exist");
        }
        if(!exportFile.canRead())
        {
          throw new BuildException("Error: in " + getClass().getName()+  " file " + fileInfo.filePath + " cannot be read");
        }
        BufferedReader in = new BufferedReader(new FileReader(exportFile));
        String aLine = in.readLine();
        while((aLine != null) )
        {
          String updateStatement = generateUpdateStatement(aLine, fileInfo);
          if(updateStatement != null)
          {
            out.write(updateStatement);
            out.newLine();
          }
          aLine = in.readLine();
        }
        in.close();
      }

      out.close();
    }
    catch(Exception pe)
    {
      if(pe instanceof BuildException)
      {
        throw (BuildException) pe;
      }
      else
      {
        pe.printStackTrace();
        throw new BuildException("Error: in " + getClass().getName() +  " exception " + pe);
      }
    }

  }

  /**
   * Set the name of the control file
   *
   * @param name
   *
   **/
  public void setCtlFileName(String name)
  {
    m_ctlFileName = name;
  }

  /**
   * Set the encoded Portal ID value
   *
   * @param encodedID
   *
   **/
  public void setEncodedId(String encodedId)
  {
    m_encodedId = encodedId;
  }

  /**
   * Set the new Admin UserId value
   *
   * @param id
   *
   **/
  public void setNewAdminId(String id)
  {
    m_newAdminId = id;
  }

  /**
   * Set the control file separator character
   *
   * @param name
   *
   **/
  public void setCtlFileSep(String separator)
  {
    m_ctlFileSep = separator;
  }

  /**
    * Set the field beginning delimiter
    *
    * @param begin delimiter
    *
    **/
  public void setBeginDelim(String beginDelim)
  {
    m_beginDelim = beginDelim;
  }

  /**
    * Set the field end delimiter
    *
    * @param end delimter
    *
    **/
  public void setEndDelim(String endDelim)
  {
    m_endDelim = endDelim;
  }

  /**
   * Set the input directory path
   *
   * @param file path
   *
   **/
  public void setInDir(String filepath)
  {
    m_inDirPath = filepath;
  }

  /**
   * Set the output file path
   *
   * @param file path
   *
   **/
  public void setOutFilepath(String filepath)
  {
    m_outFilepath = filepath;
  }

  /**
    * Set the separator string
    *
    * @param separator
    *
    **/
  public void setSeparator(String separator)
  {
    m_separator = separator;
  }
  
  /**
    * Set whether to append to the output file (default is false)
    *
    * @param setting
    *
    **/
  public void setAppend(String setting)
  {
    if(setting.equalsIgnoreCase("true"))
    {
      m_append = true;
    }
    else
    {
      m_append = false;
    }
  }

  /**
    *Parse the given value into an integer for the named field
   * @param value vString alue to parse
   * @param name to use in generated BuildException if error
    *
    * @throws BuildException
    *
    **/
  protected int parseInteger(String value, int minValue, String name) throws BuildException
  {
    int result = -1;
    try
    {
      result = Integer.parseInt(value);
      if(result < minValue)
      {
        throw new BuildException("Error: in " + getClass().getName() + name +  " must be integer >=" + minValue);
      }
    }
    catch(NumberFormatException nfe)
    {
      throw new BuildException("Error: in " + getClass().getName() + name + " must be integer >= " + minValue);
    }

    return result;
  }

  /**
    * Parse the line into fields
    *
    * @param aLine the line
   * @param fileName file name for generated exception
    *
    **/
  protected ArrayList parseLine(String aLine, String fileName) throws BuildException
  {
    ArrayList fieldList = new ArrayList();
    String sep = ",";
    int searchCount = 1;
    int searchPos = 0;
    int beginDelimLength = m_beginDelim.length();
    int endDelimLength = m_endDelim.length();
    int startPos = 0;
    int sepPos = aLine.indexOf(sep);
    while(sepPos >= 0)
    {
      String aToken = aLine.substring(startPos, sepPos).trim();
      String value = "";
      if(aToken.length() > 0)
      {
        if(!aToken.startsWith(m_beginDelim))
        {
          throw new BuildException("Error: in " + getClass().getName()+  " non empty field does not begin with expected delimiter in " + fileName);
        }
        startPos += beginDelimLength;
        int endPos = aLine.indexOf(m_endDelim, startPos);
        if(endPos < 0)
        {
          throw new BuildException("Error: in " + getClass().getName()+  " non empty field does not end with expected delimiter in " + fileName);
        }
        value = aLine.substring(startPos, endPos);
        startPos = endPos + endDelimLength;
      }
      fieldList.add(value);

      if(startPos < aLine.length())
      {
        if(!aLine.substring(startPos, startPos + sep.length()).equals(sep))
        {
          throw new BuildException("Error: in " + getClass().getName() + " did not find expected separator following  \"" + aToken + "\" in "+ fileName);
        }
        startPos += sep.length();
        sepPos = aLine.indexOf(sep, startPos);
      }
      else
      {
        sepPos = -1;
      }
    }
    //-------------------------------------------------------
    // Handle possible value after last separator
    //-------------------------------------------------------
    if(startPos < aLine.length())
    {
      String last = aLine.substring(startPos);
      if(!last.startsWith(m_beginDelim))
      {
        throw new BuildException("Error: in " + getClass().getName()+  " last field does not begin with expected delimiter in " + fileName);
      }
      int pos2 = last.indexOf(m_endDelim, beginDelimLength);
      if(pos2 < 0)
      {
        throw new BuildException("Error: in " + getClass().getName()+  " last field does not end with expected delimiter in " + fileName);
      }
      fieldList.add(last.substring(beginDelimLength, pos2));
    }

    return fieldList;
  }

  /**
    * Parse the line into fields
    *
    * @param aLine the line
   * @param ctlFilePath path to control file for generated Exceptions
    *
    **/
  protected FileInfo parseControlFileLine(String aLine, String ctlFilePath) throws BuildException
  {
    FileInfo result = null;

    String filePath = null;
    String dbUser = null;
    String[] adminNames = null;
    ArrayList primaryFieldNumberList = null;
    ArrayList primaryFieldNameList = null;
    int objectIdFieldNumber = -1;
    String objectIdFieldName = null;

    StringTokenizer tokenizer = new StringTokenizer(aLine, m_ctlFileSep);
    boolean valid = true;
    while(valid && tokenizer.hasMoreTokens())
    {
      String aToken = tokenizer.nextToken();
      int equalsPos = aToken.indexOf('=');
      if(equalsPos < 0)
      {
        throw new BuildException("Error: in " + getClass().getName() + " missing equals in control file " + ctlFilePath + " in line:" + aLine);
      }
      String lhs = aToken.substring(0, equalsPos).trim();
      String rhs = aToken.substring(equalsPos + 1).trim();
      if(lhs.equals(ATTR_ADMINID_FIELD_NAMES))
      {
        ArrayList nameList = new ArrayList();
        int startPos = 0;
        int commaPos = rhs.indexOf(",");
        while(commaPos >= 0)
        {
          String aName = rhs.substring(startPos, commaPos).trim();
          nameList.add(aName);
          startPos = commaPos + 1;
          commaPos = rhs.indexOf(",", startPos);
        }
        //-----------------------------------------------------
        // Handle name after last separator if any
        //-----------------------------------------------------
        if(startPos < rhs.length())
        {
          String lastName = rhs.substring(startPos).trim();
          if(lastName.length() > 0)
          {
            nameList.add(lastName);
          }
        }

        adminNames = new String[nameList.size()];
        for(int index=0; index < nameList.size(); ++index)
        {
          adminNames[index] = (String) nameList.get(index);
        }
      }
      else if(lhs.equals(ATTR_EXPORT_FILE_NAME))
      {
        filePath = m_inDir.getAbsolutePath() + File.separator + rhs;
      }
      else if(lhs.equals(ATTR_DB_USER))
      {
        dbUser = rhs;
      }
      else if(lhs.equals(ATTR_PRIMARY_FIELD_NUMBER))
      {
        primaryFieldNumberList = new ArrayList();
        int startPos = 0;
        int commaPos = rhs.indexOf(',');
        if(commaPos >= 0)
        {
          while(commaPos >= 0)
          {
            String aFieldNumberString = rhs.substring(startPos, commaPos);
            int aFieldNumber = parseInteger(aFieldNumberString, 1, ATTR_PRIMARY_FIELD_NUMBER);
            primaryFieldNumberList.add(new Integer(aFieldNumber));
            startPos = commaPos + 1;
            commaPos = rhs.indexOf(',', startPos);
          }
        }
        else
        {
          int aFieldNumber = parseInteger(rhs, 1, ATTR_PRIMARY_FIELD_NUMBER);
          primaryFieldNumberList.add(new Integer(aFieldNumber));
        }
      }
      else if(lhs.equals(ATTR_PRIMARY_FIELD_NAME))
      {
        primaryFieldNameList = new ArrayList();
        int startPos = 0;
        int commaPos = rhs.indexOf(',');
        if(commaPos >= 0)
        {
          while(commaPos >= 0)
          {
            String aFieldNameString = rhs.substring(startPos, commaPos);
            primaryFieldNameList.add(aFieldNameString);
            startPos = commaPos + 1;
            commaPos = rhs.indexOf(',', startPos);
          }
        }
        else
        {
          primaryFieldNameList.add(rhs);
        }
      }
      else if(lhs.equals(ATTR_OBJECTID_FIELD_NUMBER))
      {
        objectIdFieldNumber = parseInteger(rhs, 1, ATTR_OBJECTID_FIELD_NUMBER);
      }
      else if(lhs.equals(ATTR_OBJECTID_FIELD_NAME))
      {
        objectIdFieldName = rhs;
      }
      else
      {
        throw new BuildException("Error: in " + getClass().getName() + " unrecognized attribute " + lhs + " in " + ctlFilePath + " in line:" + aLine);
      }
    }
    if(
      (filePath == null) ||
      (dbUser == null) ||
      (primaryFieldNameList == null) ||
      (primaryFieldNumberList == null) ||
      (((objectIdFieldName == null) || (objectIdFieldNumber <= 0)) && (adminNames == null))
    )
    {
      throw new BuildException("Error: in " + getClass().getName() + " file, dbuser, primary, primaryName and either objectid and objectidName or adminNames required in " + ctlFilePath + " in line:" + aLine);
    }

    if(primaryFieldNumberList.size() != primaryFieldNameList.size())
    {
      throw new BuildException("Error: in " + getClass().getName() + " primaryField and primaryFieldName must have the same number of entries in " + ctlFilePath + " in line:" + aLine);
    }

    result = new FileInfo(filePath, dbUser, primaryFieldNameList, primaryFieldNumberList, objectIdFieldName, objectIdFieldNumber, adminNames);

    return result;
  }

  /**
    * Parse the line into fields
    *
    * @param aLine the line
    *
    **/
  protected ArrayList parseControlFile(File inDir) throws BuildException
  {
    ArrayList fileInfoList = new ArrayList();
    String ctlFilePath = inDir.getAbsolutePath() + File.separator + m_ctlFileName;
    File ctlFile = new File(ctlFilePath);
    if(!ctlFile.exists())
    {
      throw new BuildException("Error: in " + getClass().getName()+  " file " + ctlFilePath + " does not exist");
    }
    if(!ctlFile.canRead())
    {
      throw new BuildException("Error: in " + getClass().getName()+  " file " + ctlFilePath + " cannot be read");
    }

    try
    {
      BufferedReader in = new BufferedReader(new FileReader(ctlFile));
      String aLine = in.readLine();
      while((aLine != null) )
      {
        FileInfo fileInfo = parseControlFileLine(aLine, ctlFilePath);
        fileInfoList.add(fileInfo);
        aLine = in.readLine();
      }
    }
    catch(IOException ioe)
    {
      throw new BuildException("Error: in " + getClass().getName() + " IOException processing file " + ctlFilePath + " : " + ioe);
    }

    return fileInfoList;
  }

  /**
    * Generate a SQL UPDATE statement from the given line and FileInfo
    *
    * @param aLine the line
   * @param fileInfo the FileInfo object
    *
    **/
  protected String generateUpdateStatement(String aLine, FileInfo fileInfo) throws BuildException
  {
    String result = null;
    ArrayList fieldList = parseLine(aLine, fileInfo.filePath);

    int index;
    ArrayList primaryValues = new ArrayList();
    boolean havePrimaryValues = true;

    for(index=0; havePrimaryValues && (index < fileInfo.primaryFieldList.size()); ++index)
    {
      Integer fieldIndexInteger = (Integer) fileInfo.primaryFieldList.get(index);
      String aPrimaryValue = (String) fieldList.get(fieldIndexInteger.intValue() - 1);
      if(aPrimaryValue.length() > 0)
      {
        primaryValues.add(aPrimaryValue);
      }
      else
      {
        havePrimaryValues = false;
      }
    }

    if(havePrimaryValues)
    {
      String path = fileInfo.filePath.replace('\\', '/');
      int lastSlash = path.lastIndexOf("/");
      if(lastSlash > 0)
      {
        path = path.substring(lastSlash + 1);
      }
      String filename = path;
      int dotPos = path.indexOf(".");
      if(dotPos > 0)
      {
        filename = path.substring(0, dotPos);
      }
      boolean fieldSet = false;
      StringBuffer lineBuffer = new StringBuffer();
      lineBuffer.append("UPDATE ");
      lineBuffer.append(fileInfo.dbUser);
      lineBuffer.append(".");
      lineBuffer.append(filename); //filename must be table name
      lineBuffer.append(" SET ");

      //-------------------------------------------------------
      // Handle updating of old Object ID with new one if properties set
      // for that
      //-------------------------------------------------------
      if((fileInfo.objectIdField > 0) && (fileInfo.objectIdFieldName != null))
      {
        if(m_encodedId == null)
        {
          throw new BuildException("Error: in " + getClass().getName() + " encodedId must be set to update ObjectID fields");
        }
        String updatedObjectId = getUpdatedObjectId((String) fieldList.get(fileInfo.objectIdField - 1), m_encodedId);
        lineBuffer.append(fileInfo.objectIdFieldName);
        lineBuffer.append(" = \'");
        if (updatedObjectId == null){
        	updatedObjectId="";
        }
        lineBuffer.append(updatedObjectId);
        lineBuffer.append("\'");
        fieldSet = true;
      }

      //-------------------------------------------------------
      // Handle updating of admin ID if properties set for that
      //-------------------------------------------------------
      if(fileInfo.adminIdFieldNames != null)
      {
        if(m_newAdminId == null)
        {
          throw new BuildException("Error: in " + getClass().getName() + " newAdminId must be set to update Admin UserID fields");
        }
        for(int adminIndex=0; adminIndex < fileInfo.adminIdFieldNames.length; ++adminIndex)
        {
          if(fieldSet)
          {
            lineBuffer.append(", ");
          }
          lineBuffer.append(fileInfo.adminIdFieldNames[adminIndex]);
          lineBuffer.append(" = \'");
          lineBuffer.append(m_newAdminId);
          lineBuffer.append("\'");
          fieldSet = true;
        }
      }

      //-------------------------------------------------------
      // Now added on the WHERE clause
      //-------------------------------------------------------
      lineBuffer.append(" WHERE ");
      for(index=0; index < primaryValues.size(); ++index)
      {
        if(index > 0)
        {
          lineBuffer.append(" AND ");
        }
        String aPrimaryName = (String) fileInfo.primaryFieldNameList.get(index);
        lineBuffer.append(aPrimaryName);
        lineBuffer.append(" = \'");
        lineBuffer.append(primaryValues.get(index));
        lineBuffer.append("\'");
      }

      lineBuffer.append(";");

      result = lineBuffer.toString();
    }

    return result;
  }

  /**
    * Generate an updated ObjectId given the new encoded Portal ID to use
    *
    * @param oldObjectId entire old object ID
   * @param newEncodedId encoded Portal ID part
    *
    **/
  protected String getUpdatedObjectId(String oldObjectId, String newEncodedId) throws BuildException
  {
    String result = null;
    boolean valid = false;

    //-------------------------------------------------------
    // Expected format is _ddd_pid_ddd_ where '_' is separator, ddd is
    // digits and pid is the encoded portal id
    //-------------------------------------------------------
    int sep1 = oldObjectId.indexOf(m_separator);
    if(sep1 >= 0)
    {
      int sep2 = oldObjectId.indexOf(m_separator, sep1 + m_separator.length());
      if(sep2 > 0)
      {
        int sep3 = oldObjectId.indexOf(m_separator, sep2 + m_separator.length());
        if(sep3 > 0)
        {
          valid = true;
          result = oldObjectId.substring(0, sep2 + 1) + newEncodedId + oldObjectId.substring(sep3);
        }
      }
    }
    if (oldObjectId == "" || oldObjectId == null ){
    	valid = true;
    }
    if(!valid)
    {
      throw new BuildException("Error: in " + getClass().getName() + " old ObjectID " + oldObjectId + " not in recognized format");
    }

    return result;
  }

  protected File m_inDir;                               //directory containing control file and export files
  protected String m_inDirPath;                     //path to directory containing control file and export files
  protected String m_ctlFileName;                  //control file name
  protected String m_ctlFileSep;                     //control file field separator
  protected String m_outFilepath;                   //path to output file
  protected String m_beginDelim;                  //beginning delimiter
  protected String m_endDelim;                     //ending delimiter
  protected String m_separator;                     //value separator string for values
  protected String m_encodedId;                   //encoded PortalID
  protected String m_newAdminId;                 //new Admin Userid
  protected boolean m_append;                   //whether to append to output file
}
