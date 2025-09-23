===========================
IBM Prerequisite Scanner - Windows
Version: 1.0.43
Build:   20101204
===========================

==================================
Extending the Prereq Scanner (PRS)
==================================
This readme describes the steps to extend the Prerequisite Scanner in different ways.

---------------------------------------------
High-Level Design of the Prerequisite Scanner
---------------------------------------------
For a high-level design overview of the PRS, see the overview presentation linked from the Wiki:
https://w3.tap.ibm.com/w3ki02/display/PRCTool/HOME
Direct link to presentation:
https://w3.tap.ibm.com/w3ki02/download/attachments/100000339409/PrerequisiteChecking20100330d.odp?version=1

------------------------------------------------------------------
Support New Products/Components/Versions using existing properties
------------------------------------------------------------------

To add support for a new product or version:
   1) Read about the properties available for use in the Readme file.
   2) Copy the example.cfg file and name it [product_code]_[version].cfg.
      For example, ABC_07022300.cfg - for product code ABC, version 7.2.23.0.
   3) Test the plugin:
      prereq_checker.bat ABC detail
   4) If a new product code was used, add a line to codename.cfg to add a description
   
-----------------
Adding Collectors
-----------------
Collectors are plugins that provide property values that can be used in configuration filess.

To collect values for properties and make them available to ALL configuration files:
   Add a file in .\lib with a name *_plug.vbs or *_plug.bat (e.g. 'myNewProperty.vbs')
   The script must ONLY output name=value pairs to standard out.  A common collector script
   may support multiple properties, one per line.  For an example, see .\lib\os_plug.vbs.


To collect values for properties and make them available to ONLY ONE configuration file:
   Add a file in .\Windows with a name [product code]_[version].vbs or [product_code]_[version].bat
   (e.g. 'ABC_07022300.vbs').  The script must only output name=value pairs to standard out.  A
   common collector script may support multiple properties, one per line.

----------------
Custom Comparers
----------------
Some property values require more advanced comparison code to determine PASS/FAIL.  By default, the
Windows code handles numbers, size (1024KB, 512MB, 50GB - integers only for now), speed (900MHz, 3.4GHz)
and boolean values automatically without any custom comparison code.  For more complex properties
such as network.availablePorts and version strings, custom code is needed to properly determine
the PASS/FAIL value.

    To create a custom comparer:   
    Create a file in the .\Windows directory named [property name]_compare.vbs/.bat or
    [product_code]_[property name]_compare.vbs/.bat
	Replace any spaces in property names with an underscore '_'
    Two parameters will be passed into the script:
        - The first parameter is the expected value (the value from the configuration file)
        - The second parameter is the real value (the value discovered on the machine)
    The script must ONLY output the following:
        expect: [first parameter]
        real value: [second parameter]
        'PASS' or 'FAIL'